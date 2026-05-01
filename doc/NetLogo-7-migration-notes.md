# NetLogo 7 Migration Handover

This is the current handover for the NetLogo 7 migration of the xw
extension.  It is intended to let a future session resume the work from the
repository alone, without relying on chat history.

The older historical notes are kept below this handover for context only.  If
they disagree with this section, trust this section.

## Context

xw is a NetLogo extension for creating extra interface tabs and Swing-based
widgets from NetLogo code.  The migration branch upgrades it to NetLogo 7,
Scala 3, and Java 17 while preserving xw's original design:

- widget kinds are loaded dynamically from jars under the installed extension's
  `widgets/` directory
- primitives are derived from widget kinds and their properties
- externally authored widget jars should continue to be picked up if installed
  under `extensions/xw/widgets/<WidgetName>/<WidgetName>.jar`
- xw owns extra tabs in the NetLogo desktop UI, so some coupling to NetLogo
  desktop internals is unavoidable

The user prefers small, clean, scalable solutions and has explicitly asked to
avoid visual hacks or migration-specific workarounds.  Documentation should not
be updated in full until the implementation migration is complete.

## Current Branch State

- Branch: `netlogo-7-migration`
- Local status at handover time: clean before this handover edit
- Local branch status at handover time: ahead of `origin/netlogo-7-migration`
  by 14 commits
- Last code commit before this handover: `ebeffa3 Remove stale migration cleanup leftovers`
- Migration target: NetLogo `7.0.3`
- Scala version: `3.7.0`
- sbt version: `1.7.2`
- Verified Java: OpenJDK 17 with
  `JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64`
- NetLogo extension plugin:
  `org.nlogo:netlogo-extension-plugin:7.0.3-d1d7e42`
- Project version: `3.0.0-SNAPSHOT`

Java 17 is the intended alignment point for this branch.  Continue running
verification commands with Java 17 unless there is a specific reason to test
another JDK.

## Recent Commit Trail

Current migration commits from `master` at handover time:

```text
02794d5 Prepare migration build baseline
a20c9ea Compile against NetLogo 7
24deb7c Write test exports under target
16f3b94 Replace deprecated unicode arrows
0c879b9 Clean Scala 3 warnings
9bc95ea Document NetLogo 7 migration handoff
b9da884 Test and fix on-change callbacks
fba80d0 Use concise one-argument anonproc syntax in tests
5f890f3 Note concise anonproc doc guidance
72742dd Test button command execution
b9d5744 Add explicit packaged smoke task
6bf7099 Use packaged widget jars for primitive metadata
e1313d7 Test installed widget jar discovery
07f6b2c Fix property map parse errors
49506c6 Refresh migration notes
0e45c60 Add NetLogo GUI test model
e7359d7 Fix GUI tab selection timing
fffa407 Record GUI test pass
3572535 Add theme-aware default colours
ebeffa3 Remove stale migration cleanup leftovers
```

## What Is Done

The project compiles and tests under NetLogo 7 with Scala 3:

- `build.sbt` uses Scala `3.7.0` with fatal warnings enabled.
- `project/build.properties` uses sbt `1.7.2`.
- `project/plugins.sbt` uses the NetLogo 7 extension plugin.
- `xw/build.sbt` targets NetLogo `7.0.3`.
- The normal `xw / test` suite passes.
- The package task produces `xw/xw-3.0.0-SNAPSHOT.zip`.
- NetLogo's `PrimsJson` generation runs successfully.
- The explicit `xw / packagedSmoke` task passes and proves the extension loads
  from an unpacked packaged zip, not from sbt's in-repo classpath.

NetLogo 7 desktop integration has been migrated:

- `RichWorkspace`, `Tab`, and `SelectTab` use NetLogo 7 desktop classes such as
  `App`, `AppFrame`, `TabManager`, `TabsPanel`, and `TabLabel`.
- `xw:select-tab` waits for the Swing event queue so
  `xw:create-tab ... xw:select-tab ...` works reliably in one NetLogo job.
- `xw:select-tab` is a no-op in headless mode, which is covered by tests.
- The manual GUI smoke test model at `test-models/xw-gui-test.nlogox` passed in
  NetLogo 7.0.3.

Dynamic widget discovery and metadata generation are in good shape:

- Runtime widget discovery still scans the installed extension layout under
  `widgets/`.
- The earlier directory-scanning metadata workaround has been removed.
- `primitiveMetadataFallback()` now loads the widget jars that sbt is already
  packaging, supplied through the build-only
  `uk.ac.surrey.xw.primitiveMetadataWidgetJars` system property.
- `SELECT-TAB` primitive metadata construction no longer passes a `null`
  workspace; it uses `Option[AbstractWorkspace]`.

Tests and behavior added during this phase:

- `xw:on-change` and property-specific `xw:on-...-change` have focused tests.
- Button command execution has focused tests.
- Installed widget jar discovery has focused tests.
- Property-map parse errors have focused tests.
- Default theme-aware color behavior has focused tests.
- The GUI smoke model checks tab order, tab selection, all bundled widget kinds,
  default-vs-explicit colors, user edits, callbacks, button commands, and tab
  removal.
- The GUI smoke model is an internal migration/maintenance aid.  Do not assume
  it belongs in public-facing user docs, and be willing to delete or replace it
  if it stops carrying its weight.
- Old tracked JSON export outputs under `tests/` were removed; tests now write
  generated export/import files under `target/`.

## Important User Decisions

Do not update the user/developer docs until the migration implementation is
complete.  This migration note is the exception: it is a handover file.

When docs are eventually updated, NetLogo examples should use concise
one-argument anonymous procedure syntax:

```netlogo
[ value -> ... ]
```

Keep bracketed argument lists for multi-argument anonymous procedures:

```netlogo
[ [a b] -> ... ]
```

Styling decisions made so far:

- xw should not be stuck in the middle between native Swing and NetLogo widget
  styling.
- Pure native Swing looked wrong for tabs and did not honor NetLogo dark theme.
- The chosen path is theme-aware default colors with explicit user colors still
  respected.
- The special color value is `"default"`, not `"theme"`.
- `"default"` is the default stored value for color properties and means
  "follow the current NetLogo theme".
- Explicit colors remain explicit through theme changes.
- Swing's rounded `LineBorder` was tested and rejected.  It only barely rounded
  the top-left corner in practice, and thicker borders were not worth the
  tradeoff.
- NetLogo's `RoundedBorderPanel` was not used for this pass because the user
  wanted to keep the implementation simple and avoid visual machinery.  Do not
  treat that as a permanent ban; it can be reconsidered later if widget visual
  polish becomes important enough to justify the extra coupling.
- Current border styling is deliberately conservative: plain Swing line border,
  theme-following neutral color, light padding, no custom painting.

## Verification Commands

Use these commands from the repo root with Java 17:

```bash
env JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
  PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:/usr/bin:/bin \
  sbt 'xw / test'
```

At handover time this passed with 26 tests.

```bash
env JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
  PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:/usr/bin:/bin \
  sbt 'xw / packageZip' 'xw / packagedSmoke'
```

At handover time this passed and printed:

```text
xw smoke ok
loaded from /tmp/.../extensions/xw/xw.jar
```

Before a release-quality finish, also run:

```bash
env JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
  PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:/usr/bin:/bin \
  sbt clean test
```

The GUI smoke test is manual:

1. Build/package the extension.
2. Ensure NetLogo 7.0.3 can load `extensions [xw]`.
3. Open `test-models/xw-gui-test.nlogox` in the NetLogo 7 desktop app.
4. Click `setup-gui-test` and follow the popup plus Info-tab instructions.

The user reported the current GUI test as fine after the theme/default-color and
border cleanup work.

## Known Permanent Couplings

These are not considered migration leftovers:

- `RichWorkspace`, `Tab`, and `SelectTab` depend on NetLogo desktop internals.
  This is intrinsic to xw's extra-tab feature.
- `xw/src/main/scala/uk/ac/surrey/xw/extension/util/package.scala` uses NetLogo
  NVM internals such as `Activation`, `Context`, and `makeConcurrentJob` to run
  anonymous command blocks from callbacks and buttons.
- `primitiveMetadataFallback()` and `xw / packagedSmoke` are long-term useful
  for xw because xw has dynamic widget jars and dynamic primitives.  They are
  more than migration scaffolding.

## Known Issues And Review Items

Review before release:

- `xw/build.sbt` still has
  `netLogoHomepage := "https://github.com/NetLogo/NetLogo-Extension-Plugin"`.
  This looks like sample plugin metadata and should probably be changed to the
  xw project homepage.
- `core/src/main/scala/uk/ac/surrey/xw/gui/package.scala` contains an old TODO
  about putting stack traces in a dialog.  It appears unrelated to the NetLogo 7
  migration and may be unused, but it is still there.
- `xw/widgets/SliderWidget/src/main/scala/uk/ac/surrey/xw/slider/Slider.scala`
  still uses `scala.language.reflectiveCalls` because it extends/accesses
  NetLogo `SliderData` members.  It compiles with warnings-as-errors today, so
  this is not blocking, but it is worth understanding before release if doing a
  best-practice pass.
- The migration notes below this handover still contain older historical
  wording, including some stale details.  Use this current handover section
  first.

Longer-term packaging goal:

- The user would eventually like xw to be available through NetLogo's extension
  manager.  This is not part of the immediate migration finish, but packaging
  and metadata decisions should avoid making that harder.  In particular,
  review extension metadata, homepage, packaged layout, generated `prims.json`,
  versioning, and any NetLogo extension manager requirements before release.

## What Is Left To Do

### 1. API And Primitive Compatibility Audit

This is the next substantive step.

Compare the current implementation against:

- `doc/Primitives.md`
- `doc/Properties.md`
- `doc/Kinds.md`
- `doc/Colors.md`
- `doc/Creating your interface at startup.md`
- `doc/Creating-dynamic-interfaces.md`
- `doc/Widget-contexts.md`

Do not update those docs yet unless a code/test mismatch needs to be understood
locally.  The point of this pass is to find behavior mismatches first.

Specific things to check:

- every documented primitive still exists
- every dynamically generated getter/setter/on-change primitive is still
  generated for the right properties
- primitive syntax matches documented inputs and outputs
- `xw:ask`, `xw:of`, and `xw:with` still behave as documented
- widget creation still uses the intended default tab behavior
- tab order and `xw:select-tab` behavior match documented examples
- button command callbacks and widget change callbacks match documented
  behavior
- export/import still handles existing numeric and RGB/RGBA color values
- export/import preserves the new `"default"` color value
- old exported files from pre-`"default"` xw can still be imported
- `xw:color` and `xw:font-color` now report `"default"` for default-following
  colors and concrete values for explicit colors; decide whether this is the
  right public API for NetLogo 7

The old tracked JSON files were removed in `ebeffa3`, but they can still be
used as compatibility fixtures via git history if needed, for example:

```bash
git show ebeffa3^:tests/export-import-preserves-values.json
```

### 2. Add Or Adjust Tests Found By The Audit

Only add tests for behavior that matters long-term.  Avoid tests whose only
purpose is to lock in migration mechanics.

Likely valuable additions if not already covered enough:

- importing an old export file with numeric/list colors
- reporting and setting `"default"` through generic `xw:set` and specific
  setters
- a compact test proving `xw:font-color` follows the same default/explicit
  rules as `xw:color`
- a regression test for primitive metadata generation if the API audit touches
  dynamic primitive construction

### 3. Pre-Migration Test Coverage Audit

When work resumes, also look beyond migration-specific behavior and evaluate
coverage for xw behavior that predates NetLogo 7.  The goal is not to inflate
test count, but to find legacy behavior where a small test would protect
long-term maintenance.

Useful areas to inspect:

- documented primitives with little or no direct coverage
- each bundled widget kind's properties and default values
- export/import compatibility and error paths
- widget context behavior for `xw:ask`, `xw:of`, and `xw:with`
- removal/clear behavior across tabs and child widgets
- property validation and error messages users are likely to see
- callback behavior that existed before the migration, not just the new tests
  added while migrating

Prefer compact headless tests where possible.  Use the GUI smoke model only for
desktop behavior that cannot be proven headlessly.

### 4. Desktop Lifecycle Verification

The GUI smoke test passed, but still explicitly verify lifecycle behavior before
calling the implementation complete:

- open the GUI test model in a fresh NetLogo process
- run the GUI test
- close the model or quit NetLogo
- reopen and run it again
- confirm tabs do not duplicate
- confirm theme sync listeners do not appear duplicated
- confirm unloading clears xw tabs/widgets without leaving Swing components
  behind

The relevant code is:

- `xw/src/main/scala/uk/ac/surrey/xw/extension/ExtraWidgetsExtension.scala`
- `core/src/main/scala/uk/ac/surrey/xw/gui/GUI.scala`
- `api/src/main/scala/uk/ac/surrey/xw/api/Tab.scala`

### 5. GitHub Issue Review

Before declaring the migration complete, review the open issues on the GitHub
repo.  This requires checking current GitHub state, not relying on memory.

Classify issues into:

- fixed by the NetLogo 7 migration already
- newly low-hanging because of migration work already done
- still out of scope for this migration
- potentially affected by NetLogo 7 behavior changes

Do this after the API/primitive audit, because the audit will give better
context for whether an issue is truly fixed or merely changed.

### 6. Final Package Checks

After code/test fixes from the audit:

```bash
env JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
  PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:/usr/bin:/bin \
  sbt clean test

env JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
  PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:/usr/bin:/bin \
  sbt 'xw / packageZip' 'xw / packagedSmoke'
```

Also inspect the package if anything about packaging changed:

```bash
unzip -l xw/xw-3.0.0-SNAPSHOT.zip
jar tf xw/xw-3.0.0-SNAPSHOT.zip
```

Use the appropriate command for the artifact being inspected; `unzip -l` is the
more direct command for the zip.

### 7. Documentation Update

Only do this after the implementation and verification are complete.

Update user docs for:

- NetLogo 7 compatibility
- installation under NetLogo 7
- Java 17 alignment
- Scala 3 / NetLogo 7 developer setup
- `prims.json` and package metadata if documenting extension packaging
- `"default"` color behavior
- dark/light theme behavior
- explicit colors remaining explicit
- current anonymous procedure syntax
- any changed behavior discovered during the API audit

Do not put the GUI smoke test in public-facing user docs by default.  If it is
kept, document it only as maintainer/developer verification material.

The documentation refresh also needs screenshots, not just text.  A future
session may not be able to generate final screenshots directly, so prepare a
clear capture checklist for the user.  Likely screenshots to regenerate:

- extension overview/demo screenshot
- tab widget screenshot
- note widget screenshot
- slider widget screenshot
- checkbox widget screenshot
- chooser widget screenshot
- multi-chooser widget screenshot
- text input widget screenshot
- numeric input widget screenshot
- button widget screenshot
- enabled/disabled example screenshot
- dynamic interface generation screenshots
- any color/theme example screenshots added for `"default"` behavior

When making the screenshot checklist, include the NetLogo version, theme
selection, model/setup steps, expected window size if relevant, and exact files
under `doc/img/` that should be replaced.

When updating examples, use one-argument anonproc syntax without the old extra
argument brackets:

```netlogo
xw:on-change "slider" [ value -> print value ]
```

Use bracketed argument lists only for multi-argument anonprocs:

```netlogo
foreach pairs [ [a b] -> ... ]
```

## Files To Start With Next Session

For API/primitive audit:

- `xw/src/main/scala/uk/ac/surrey/xw/extension/ExtraWidgetsExtension.scala`
- `xw/src/main/scala/uk/ac/surrey/xw/extension/prim/*.scala`
- `api/src/main/scala/uk/ac/surrey/xw/api/Property.scala`
- `api/src/main/scala/uk/ac/surrey/xw/api/WidgetKind.scala`
- `xw/tests.txt`
- `doc/Primitives.md`
- `doc/Properties.md`
- `doc/Kinds.md`

For GUI lifecycle and styling:

- `core/src/main/scala/uk/ac/surrey/xw/gui/GUI.scala`
- `api/src/main/scala/uk/ac/surrey/xw/api/Tab.scala`
- `api/src/main/scala/uk/ac/surrey/xw/api/JComponentWidget.scala`
- `api/src/main/scala/uk/ac/surrey/xw/api/ThemeColors.scala`
- `test-models/xw-gui-test.nlogox`

For packaging and primitive metadata:

- `xw/build.sbt`
- `xw/src/main/scala/uk/ac/surrey/xw/extension/ExtraWidgetsExtension.scala`
- `core/src/main/scala/uk/ac/surrey/xw/WidgetsLoader.scala`
- `xw/src/test/java/uk/ac/surrey/xw/extension/PackagedSmokeRunner.java`
- `xw/src/test/scala/uk/ac/surrey/xw/extension/WidgetDiscoveryTests.scala`

## Historical Notes

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
- A manual NetLogo 7 desktop GUI test model exists at `test-models/xw-gui-test.nlogox`. It contains its own Info-tab instructions and phase-specific popup prompts, and it passed a manual run in NetLogo 7.0.3 on 2026-05-01.

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
- `49506c6 Refresh migration notes`
- `0e45c60 Add NetLogo GUI test model`
- `e7359d7 Fix GUI tab selection timing`

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

- The GUI tab integration has now passed the manual desktop smoke test in NetLogo 7.0.3. It is still coupled to NetLogo desktop internals, so keep it on the release verification checklist.
- `RichWorkspace.app` finds the `App` through `ws.getFrame.asInstanceOf[AppFrame].getLinkChildren.collectFirst`. This has been manually smoke-tested against NetLogo 7.0.3, but it remains coupled to NetLogo desktop internals.
- `Tab.addToAppTabs`, `Tab.removeFromAppTabs`, `Tab.setTitle`, and `RichWorkspace.reorderTabs` depend on NetLogo 7 tab ordering and `TabLabel` behavior. The manual GUI test covers tab creation, ordering, selection, and removal, but these remain the highest-risk desktop behaviors.
- `SelectTab` is intentionally a no-op in headless mode. That matches the existing test expectation that selecting tabs should not crash headless; desktop selection is covered by the manual GUI test model.
- `ExtraWidgetsExtension.primitiveMetadataFallback()` remains a build-time bridge because NetLogo's `PrimsJson` tool calls `load` without an installed extension folder. The previous directory-scanning workaround has been removed; the fallback now uses the widget jars that sbt is already packaging.
- `xw/src/main/scala/uk/ac/surrey/xw/extension/util/package.scala` still uses NetLogo internals such as `Activation`, `Context`, and `makeConcurrentJob` for anonymous command execution. Headless tests cover `xw:on-change`, property-specific `xw:on-...-change`, and button command callbacks; the manual GUI test covers user-driven widget edits and GUI button clicks.
- The smoke test proved package loading and basic headless primitive operation, and the manual GUI test proved Swing widget rendering, tab placement, user-driven event updates, GUI button clicks, and tab removal. Desktop unload/reload behavior still needs explicit verification.
- `test-models/xw-gui-test.nlogox` has been generated, verified to load through NetLogo 7 headless, and manually run successfully in the NetLogo 7.0.3 desktop UI.
- `xw/build.sbt` currently has `netLogoHomepage := "https://github.com/NetLogo/NetLogo-Extension-Plugin"`, which looks like plugin sample metadata rather than the xw project homepage. Review before release.

## Suggested Next Steps

1. Push and resume from `netlogo-7-migration`.
2. Run `sbt clean test` and `sbt 'xw / packageZip'` on the new machine with Java 17.
3. Run `sbt 'xw / packagedSmoke'` to verify the packaged zip from a fresh temporary install layout.
4. Keep `test-models/xw-gui-test.nlogox` in the release verification checklist and rerun it after GUI-affecting changes.
5. Test desktop unload/reload behavior by closing and reopening the GUI test model in a fresh NetLogo process.
6. Review `primitiveMetadataFallback()` after the rest of the migration is stable and decide whether the current sbt-to-`PrimsJson` handoff should remain as supported build infrastructure.
7. Update user and developer documentation for NetLogo 7, Scala 3, Java 17, and any installation changes. When updating NetLogo code examples, use concise one-argument anonymous procedure syntax such as `[ value -> ... ]`; keep bracketed argument lists for multi-argument anonymous procedures such as `[ [a b] -> ... ]`.

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
