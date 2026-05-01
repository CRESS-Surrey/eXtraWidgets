package uk.ac.surrey.xw.extension

import java.io.File
import java.nio.file.Files
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

import scala.jdk.CollectionConverters.*

import org.nlogo.window.GUIWorkspace
import org.scalatest.funsuite.AnyFunSuite

import uk.ac.surrey.xw.WidgetsLoader
import uk.ac.surrey.xw.api.ExtraWidget
import uk.ac.surrey.xw.api.State
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.WidgetKind

class WidgetDiscoveryTests extends AnyFunSuite {

  test("loads widget kinds added under the installed extension widgets directory") {
    val tempDir = Files.createTempDirectory("xw-widget-discovery").toFile

    try {
      val widgetDir = new File(tempDir, "widgets/ExternalRuntimeWidget")
      assert(widgetDir.mkdirs())

      // xw's public widget extension point is the installed directory layout:
      // `widgets/<name>/<name>.jar`.  Keep this test independent from
      // netLogoPackageExtras so it proves runtime discovery is not limited to
      // the jars bundled by the xw build.
      writeClassJar(
        new File(widgetDir, "ExternalRuntimeWidget.jar"),
        classOf[ExternalRuntimeWidgetKind])

      val widgetKinds = WidgetsLoader.loadWidgetKinds(tempDir)

      assert(widgetKinds.contains("TAB"))
      assert(widgetKinds.contains("EXTERNAL-RUNTIME-WIDGET"))
    } finally {
      deleteRecursively(tempDir)
    }
  }

  private def writeClassJar(jarFile: File, clazz: Class[?]): Unit = {
    val resourceName = clazz.getName.replace('.', '/') + ".class"
    val classBytes = Option(clazz.getClassLoader.getResourceAsStream(resourceName))
      .getOrElse(sys.error("Can't find test widget class resource: " + resourceName))

    val jar = new JarOutputStream(Files.newOutputStream(jarFile.toPath))
    try {
      jar.putNextEntry(new JarEntry(resourceName))
      try classBytes.transferTo(jar)
      finally {
        jar.closeEntry()
        classBytes.close()
      }
    } finally {
      jar.close()
    }
  }

  private def deleteRecursively(file: File): Unit =
    if (file.exists) {
      val files = Files.walk(file.toPath)
      try {
        files.iterator.asScala.toSeq
          .sortWith((left, right) => right.compareTo(left) < 0)
          .foreach(path => Files.deleteIfExists(path))
      } finally {
        files.close()
      }
    }
}

class ExternalRuntimeWidgetKind extends WidgetKind[ExtraWidget] {
  val name: String = "EXTERNAL-RUNTIME-WIDGET"
  val newWidget: (WidgetKey, State, GUIWorkspace) => ExtraWidget =
    (_, _, _) => throw new UnsupportedOperationException("not used by widget discovery tests")
  def defaultProperty: Option[Nothing] = None
}
