package uk.ac.surrey.xw.image

import java.awt.Color.white
import java.io.File

import javax.swing.JLabel
import javax.swing.ImageIcon

import org.nlogo.api.ExtensionException
import org.nlogo.window.GUIWorkspace

import uk.ac.surrey.xw.api.BooleanProperty
import uk.ac.surrey.xw.api.ColorProperty
import uk.ac.surrey.xw.api.JComponentWidget
import uk.ac.surrey.xw.api.JComponentWidgetKind
import uk.ac.surrey.xw.api.State
import uk.ac.surrey.xw.api.StringProperty
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.XWException

class ImageKind[W <: Image] extends JComponentWidgetKind[W] {
  override val name = "IMAGE"
  override val newWidget = new Image(_, _, _)
  override val colorProperty = new ColorProperty[W](
    "COLOR", Some(_.setBackground(_)), _.getBackground, white)
  override val opaqueProperty = new BooleanProperty[W](
    "OPAQUE", Some((w, b) ⇒ { w.setOpaque(b); w.updateBorder() }), _.isOpaque, false)
  val pathProperty = new StringProperty[W]("PATH", Some(_.setPath(_)), _.getPath)
  val defaultProperty = Some(pathProperty)
  override def propertySet = super.propertySet + pathProperty
}

class Image(
  val key: WidgetKey,
  val state: State,
  val ws: GUIWorkspace)
  extends JLabel
  with JComponentWidget {
  val kind = new ImageKind[this.type]
  private var _path = ""

  def getPath = _path
  def setPath(path: String) {
    if (path.isEmpty) setIcon(null) else {
      try {
        val file = new File(ws.fileManager.attachPrefix(path))
        if (!file.exists) throw new XWException(file.getAbsolutePath + " does not exist.")
        if (!file.isFile) throw new XWException(file.getAbsolutePath + " is not a file.")
        val url = file.toURI.toURL
        val icon = new ImageIcon(url)
        setIcon(icon)
      } catch {
        case ex: XWException => throw ex
        case ex: Exception => throw new XWException("Error loading " + path, ex)
      }
    }
    _path = path
  }
}
