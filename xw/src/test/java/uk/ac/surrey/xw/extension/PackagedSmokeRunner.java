package uk.ac.surrey.xw.extension;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.nlogo.api.ClassManager;
import org.nlogo.headless.HeadlessWorkspace;

/**
 * Runs in a child JVM launched by the `xw / packagedSmoke` sbt task.
 *
 * The process boundary is intentional.  The parent sbt process has this repo's
 * xw classes on its test classpath, which can hide packaging mistakes.  This
 * runner receives a temporary NetLogo extensions directory, loads
 * `extensions [xw]` from that directory, and verifies that NetLogo actually
 * found the unpacked `extensions/xw/xw.jar`.
 */
public final class PackagedSmokeRunner {
  public static void main(String[] args) throws Exception {
    try {
      run(args);
      // NetLogo may leave AWT/job infrastructure threads around after the
      // headless workspace is disposed.  This class is only a child-process
      // smoke runner, so exiting explicitly keeps the sbt task deterministic.
      System.exit(0);
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

  private static void run(String[] args) throws Exception {
    if (args.length != 1) {
      throw new IllegalArgumentException("usage: PackagedSmokeRunner <netlogo-extensions-dir>");
    }
    File extensionsDir = new File(args[0]);
    File expectedJar = new File(new File(extensionsDir, "xw"), "xw.jar").getCanonicalFile();

    System.setProperty("netlogo.extensions.dir", extensionsDir.getAbsolutePath());

    HeadlessWorkspace workspace = HeadlessWorkspace.newInstance();
    try {
      workspace.initForTesting(0, "extensions [xw]");

      // Exercise dynamic primitives from widget jars.  This catches the class
      // of errors where the extension works on sbt's project classpath but the
      // packaged zip omits or misplaces bundled widget jars.
      workspace.command("xw:create-tab \"smoke-tab\" [ xw:set-title \"Smoke\" ]");
      workspace.command("xw:create-slider \"smoke-slider\" [ xw:set-minimum 0 xw:set-maximum 10 xw:set-value 7 ]");
      workspace.command("xw:create-checkbox \"smoke-checkbox\" [ xw:set-selected? true ]");
      workspace.command("xw:create-chooser \"smoke-chooser\" [ xw:set-items [\"a\" \"b\"] xw:set-selected-item \"b\" ]");

      assertReport(workspace.report("xw:tabs"), "[smoke-tab]");
      assertReport(workspace.report("xw:widgets"), "[smoke-checkbox, smoke-chooser, smoke-slider]");
      assertReport(workspace.report("xw:get \"smoke-slider\""), "7.0");
      assertReport(workspace.report("xw:get \"smoke-checkbox\""), "true");
      assertReport(workspace.report("xw:get \"smoke-chooser\""), "b");

      // Prove this was a package smoke test, not another in-repo classpath
      // test.  The extension manager must have loaded xw from the temp install.
      List<File> locations = new ArrayList<>();
      for (ClassManager manager : workspace.getExtensionManager().loadedExtensions()) {
        if (manager.getClass().getName().contains("ExtraWidgetsExtension")) {
          locations.add(new File(manager.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile());
        }
      }

      if (locations.isEmpty()) {
        throw new AssertionError("xw extension manager was not loaded");
      }
      if (!locations.get(0).equals(expectedJar)) {
        throw new AssertionError("xw was not loaded from the packaged smoke install: " + locations.get(0));
      }

      System.out.println("xw smoke ok");
      System.out.println("loaded from " + locations.get(0));
    } finally {
      workspace.dispose();
    }
  }

  private static void assertReport(Object actual, String expected) {
    String actualString = String.valueOf(actual);
    if (!actualString.equals(expected)) {
      throw new AssertionError("expected " + expected + " but got " + actualString);
    }
  }
}
