# NetLogo 7 Migration Notes

This is a handoff note for the `netlogo-7-migration` branch. It records the state of the migration as of 2026-05-01 so the work can be resumed from the repository alone, without depending on chat history or temporary files on the original machine.

## Current State

- Branch: `netlogo-7-migration`
- Base branch at the start of migration work: `master` at `725e07a` (`add note in xw/build.sbt`)
- Current migration target: NetLogo `7.0.3`
- Current Scala version: `3.7.0`
- Current sbt version: `1.7.2`
- Java used for verification: OpenJDK 17, with `JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64`
- NetLogo extension plugin: `org.nlogo:netlogo-extension-plugin:7.0.3-d1d7e42`
- Version in `build.sbt`: `3.0.0-SNAPSHOT`
- Fatal warning enforcement has been restored with Scala 3 `-Werror` in `build.sbt`.
- The normal headless test suite is enabled and passing.
- The `xw / test` suite currently runs 24 tests, including focused coverage for `xw:on-change`, property-specific `xw:on-...-change`, button command execution, installed widget discovery, and malformed property-map parsing.
- The package task produces `xw/xw-3.0.0-SNAPSHOT.zip`.
- A manual NetLogo 7 desktop GUI test model exists at `test-models/xw-gui-test.nlogox`. It contains its own Info-tab instructions and phase-specific popup prompts.

## Commit Trail

- `02794d5 Prepare migration build baseline`
- `a20c9ea Compile against NetLogo 7`
- `24deb7c Write test exports under target`
- `16f3b94 Replace deprecated unicode arrows`
- `0c879b9 Clean Scala 3 warnings`
- `b9da884 Test and fix on-change callbacks`
- `fba80d0 Use concise one-argument anonproc syntax in tests`
- `5f890f3 Note concise anonproc doc guidance`
- `72742dd Test button command execution`
- `b9d5744 Add explicit packaged smoke task`
- `6bf7099 Use packaged widget jars for primitive metadata`
- `e1313d7 Test installed widget jar discovery`
- `07f6b2c Fix property map parse errors`

The commit messages are intentionally small, but the important context is that this branch first made the project compile and test under NetLogo 7, then moved generated test JSON files under `target`, then did a dedicated source-wide cleanup of deprecated unicode Scala operators, and finally cleaned Scala 3 warnings before turning fatal warnings back on. Later commits added targeted coverage for callback/job behavior and packaged-extension loading, preserved dynamic primitive metadata generation without directory-scanning workarounds, and fixed the property-map parsing error paths.

## Verified Commands

These commands passed on 2026-04-30:

```bash
env JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
  PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:/usr/bin:/bin \
  sbt clean test
```

Result: 16 tests run, 16 passed.

```bash
env JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
  PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:/usr/bin:/bin \
  sbt 'xw / packageZip'
```

Result: package generation passed and produced `xw/xw-3.0.0-SNAPSHOT.zip`.

The package task also ran NetLogo's `org.nlogo.build.PrimsJson` and generated `prims.json` successfully. This matters because xw has dynamic primitives derived from widget kinds and properties, which makes metadata generation more complicated than a typical static NetLogo extension.

This command passed on 2026-05-01:

```bash
env JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
  PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:/usr/bin:/bin \
  sbt 'xw / test'
```

Result: 24 tests run, 24 passed.

## Packaged Smoke Test

A package-level smoke test was run after `sbt 'xw / packageZip'`. The point was to exercise the built zip in an installed-extension layout instead of relying on the in-repo sbt project classpath.

The smoke test method was:

- Create a fresh temporary directory.
- Create an `extensions/xw` directory under it.
- Unpack `xw/xw-3.0.0-SNAPSHOT.zip` into that `extensions/xw` directory.
- Build a Java classpath from NetLogo and third-party dependency jars, explicitly excluding xw project jars under `/home/nicolas/workspace/xw`.
- Launch NetLogo headless through a small Java single-file runner.
- Set `netlogo.extensions.dir` to the temporary `extensions` directory.
- Initialize a headless workspace with `extensions [xw]`.
- Create a tab, slider, checkbox, and chooser.
- Verify `xw:tabs`, `xw:widgets`, and `xw:get`.
- Confirm that the loaded `ExtraWidgetsExtension` class came from the unpacked `xw.jar` in the temporary install, not from the project classpath.

The important result was:

```text
xw smoke ok
loaded from file:<temporary-dir>/extensions/xw/xw.jar
```

Do not treat the original temporary directory path as meaningful. The reusable fact is the install layout and classpath isolation described above.

The smoke runner used this shape:

```java
import java.util.ArrayList;
import java.util.List;

import org.nlogo.api.ClassManager;
import org.nlogo.headless.HeadlessWorkspace;

public class Smoke {
  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      throw new IllegalArgumentException("usage: Smoke <netlogo-extensions-dir>");
    }

    System.setProperty("netlogo.extensions.dir", args[0]);

    HeadlessWorkspace workspace = HeadlessWorkspace.newInstance();
    try {
      workspace.initForTesting(0, "extensions [xw]");

      workspace.command("xw:create-tab \"smoke-tab\" [ xw:set-title \"Smoke\" ]");
      workspace.command("xw:create-slider \"smoke-slider\" [ xw:set-minimum 0 xw:set-maximum 10 xw:set-value 7 ]");
      workspace.command("xw:create-checkbox \"smoke-checkbox\" [ xw:set-selected? true ]");
      workspace.command("xw:create-chooser \"smoke-chooser\" [ xw:set-items [\"a\" \"b\"] xw:set-selected-item \"b\" ]");

      assertReport(workspace.report("xw:tabs"), "[smoke-tab]");
      assertReport(workspace.report("xw:widgets"), "[smoke-checkbox, smoke-chooser, smoke-slider]");
      assertReport(workspace.report("xw:get \"smoke-slider\""), "7.0");
      assertReport(workspace.report("xw:get \"smoke-checkbox\""), "true");
      assertReport(workspace.report("xw:get \"smoke-chooser\""), "b");

      List<String> locations = new ArrayList<>();
      for (ClassManager manager : workspace.getExtensionManager().loadedExtensions()) {
        if (manager.getClass().getName().contains("ExtraWidgetsExtension")) {
          locations.add(String.valueOf(manager.getClass().getProtectionDomain().getCodeSource().getLocation()));
        }
      }

      if (locations.isEmpty()) {
        throw new AssertionError("xw extension manager was not loaded");
      }
      if (!locations.get(0).endsWith("/extensions/xw/xw.jar")) {
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
```

This smoke test has since been converted into the explicit `xw / packagedSmoke` sbt task. It is intentionally not part of the default `test` task because it launches a child JVM and verifies release/package layout rather than ordinary extension behavior. Run it during migration/release verification.

## Important Migration Changes Already Made

- `build.sbt` now uses Scala `3.7.0` and has `-Werror`, `-feature`, `-deprecation`, and `-unchecked`.
- `project/build.properties` now uses sbt `1.7.2`.
- `project/plugins.sbt` now uses the NetLogo 7 extension plugin.
- `xw/build.sbt` now targets NetLogo `7.0.3`.
- NetLogo 7 GUI API changes were handled in `api/src/main/scala/uk/ac/surrey/xw/api/RichWorkspace.scala` and `api/src/main/scala/uk/ac/surrey/xw/api/Tab.scala`, using `App`, `AppFrame`, `TabManager`, `TabsPanel`, and `TabLabel`.
- `xw/src/main/scala/uk/ac/surrey/xw/extension/prim/SelectTab.scala` now works against `AbstractWorkspace` and only performs GUI tab selection when the workspace is a `GUIWorkspace`; in headless mode it intentionally does nothing. In the GUI, it waits for the Swing event queue before selecting tabs so documented patterns like `xw:create-tab "my-tab"` followed by `xw:select-tab "my-tab"` work in a single NetLogo job.
- `xw/src/main/scala/uk/ac/surrey/xw/extension/ExtraWidgetsExtension.scala` now uses NetLogo 7 extension loading behavior and `JarLoader(workspace).locateExtension("xw")` to find the installed extension folder.
- `ExtraWidgetsExtension` has `primitiveMetadataFallback()` for metadata generation when NetLogo's `PrimsJson` calls `load` without a prior `runOnce`. `xw/build.sbt` now derives the widget jar list from `netLogoPackageExtras`, passes it through the build-only `uk.ac.surrey.xw.primitiveMetadataWidgetJars` system property, and the fallback reuses real `WidgetKind` classes to build primitive metadata.
- `WidgetsLoader.loadWidgetKinds` still discovers externally authored widget jars in the installed extension layout under `widgets/<WidgetName>/<WidgetName>.jar`; this is covered by a focused test and is independent from `netLogoPackageExtras`.
- `Writer` was moved away from deprecated Scala 2 publisher APIs and now has its own subscriber/listener mechanism.
- Java collection conversions were moved from `scala.collection.JavaConverters` to `scala.jdk.CollectionConverters`.
- Deprecated `Either.right` projections were removed where they caused Scala 3 warnings.
- Existential type syntax was moved from Scala 2 wildcard forms like `WidgetKind[_]` to Scala 3 forms like `WidgetKind[?]`.
- Deprecated Scala vararg splice syntax was updated from `: _*` to `*`.
- `Manifest` usage in `Property` was reduced to `ClassTag`.
- The unicode operator cleanup replaced source-level operators such as `=>` and `<-` with ASCII equivalents. This was done as a dedicated commit after discussion, to keep the earlier migration diffs focused.
- `xw/tests.txt` now writes temporary export/import JSON files under `target/` so tests do not modify tracked repository files.
- NetLogo test code now uses concise one-argument anonymous procedure syntax where possible, such as `[ value -> ... ]`; bracketed argument lists remain appropriate for multi-argument anonymous procedures, such as `[ [a b] -> ... ]`.
- `xw/src/main/scala/uk/ac/surrey/xw/extension/util/package.scala` had pre-existing exception-handling bugs in `toPropertyMap`; these have been fixed and covered by focused ScalaTest tests.

## Known Fragile Areas

- The GUI tab integration has only been compile-tested and indirectly covered by headless tests. It still needs a manual or automated GUI smoke test in a real NetLogo 7 desktop session.
- `RichWorkspace.app` finds the `App` through `ws.getFrame.asInstanceOf[AppFrame].getLinkChildren.collectFirst`. This works at compile time against NetLogo 7.0.3, but it is coupled to NetLogo desktop internals and should be manually verified.
- `Tab.addToAppTabs`, `Tab.removeFromAppTabs`, `Tab.setTitle`, and `RichWorkspace.reorderTabs` depend on NetLogo 7 tab ordering and `TabLabel` behavior. These are likely to be the highest-risk GUI behaviors.
- `SelectTab` is intentionally a no-op in headless mode. That matches the existing test expectation that selecting tabs should not crash headless, but it does not verify desktop selection behavior.
- `ExtraWidgetsExtension.primitiveMetadataFallback()` remains a build-time bridge because NetLogo's `PrimsJson` tool calls `load` without an installed extension folder. The previous directory-scanning workaround has been removed; the fallback now uses the widget jars that sbt is already packaging.
- `xw/src/main/scala/uk/ac/surrey/xw/extension/util/package.scala` still uses NetLogo internals such as `Activation`, `Context`, and `makeConcurrentJob` for anonymous command execution. Headless tests now cover `xw:on-change`, property-specific `xw:on-...-change`, and button command callbacks, but desktop/user-driven callback scheduling still needs a real NetLogo 7 GUI smoke test.
- The smoke test proved package loading and basic headless primitive operation, but it did not prove Swing widget rendering, tab placement, user-driven event updates, GUI button clicks, or desktop unload/reload behavior.
- `test-models/xw-gui-test.nlogox` has been generated and verified to load through NetLogo 7 headless, but it still needs to be run manually in the NetLogo 7 desktop UI.
- `xw/build.sbt` currently has `netLogoHomepage := "https://github.com/NetLogo/NetLogo-Extension-Plugin"`, which looks like plugin sample metadata rather than the xw project homepage. Review before release.

## Suggested Next Steps

1. Push and resume from `netlogo-7-migration`.
2. Run `sbt clean test` and `sbt 'xw / packageZip'` on the new machine with Java 17.
3. Run `sbt 'xw / packagedSmoke'` to verify the packaged zip from a fresh temporary install layout.
4. Install the packaged zip, or symlink this repository's `xw` directory, into a real NetLogo 7 desktop extensions directory.
5. Open `test-models/xw-gui-test.nlogox` in NetLogo 7.0.3 and follow its Info-tab/popup instructions.
6. In that GUI test, verify tab ordering and selection visually, then use the model buttons to mechanically assert user-driven widget edits, GUI button clicks, `xw:on-change` callbacks, and tab removal.
7. If the generated GUI test passes, also test desktop unload/reload behavior by closing and reopening the model in a fresh NetLogo process.
8. Review `primitiveMetadataFallback()` after the rest of the migration is stable and decide whether the current sbt-to-`PrimsJson` handoff should remain as supported build infrastructure.
9. Update user and developer documentation for NetLogo 7, Scala 3, Java 17, and any installation changes. When updating NetLogo code examples, use concise one-argument anonymous procedure syntax such as `[ value -> ... ]`; keep bracketed argument lists for multi-argument anonymous procedures such as `[ [a b] -> ... ]`.

## Useful Resume Checklist

Run these first on a new machine:

```bash
git checkout netlogo-7-migration
git status --short --branch
java -version
env JAVA_HOME=<java-17-home> PATH=<java-17-home>/bin:/usr/bin:/bin sbt clean test
env JAVA_HOME=<java-17-home> PATH=<java-17-home>/bin:/usr/bin:/bin sbt 'xw / packageZip'
env JAVA_HOME=<java-17-home> PATH=<java-17-home>/bin:/usr/bin:/bin sbt 'xw / packagedSmoke'
```

If the build fails from missing dependencies, let sbt/coursier download them. The migration has already needed NetLogo artifacts, NetLogo extension plugin artifacts, Scala 3 artifacts, JOGL, GlueGen, and json-simple.

If package generation fails around primitive metadata, start by looking at `ExtraWidgetsExtension.load`, `primitiveMetadataFallback`, `primitiveMetadataWidgetJars`, and the `preparePrimitiveMetadata` task in `xw/build.sbt`.

If GUI behavior fails while headless tests pass, start by looking at `RichWorkspace`, `Tab`, `GUI`, and `SelectTab`.

If callbacks or command blocks fail, start by looking at `HasCommandBlock`, `OnChange`, `OnChangeProperty`, and `extension/util/package.scala`.
