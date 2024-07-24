package pokemon.util

import pokemon.MainApp
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.scene.image.Image
import scalafx.scene.text.Font

object ResourceUtil {

  /**
    * Load a resource layout from "view" folder
    *
    * @param path
    * @return
    */
  def resourceLayout(path: String): FXMLLoader = {
    val resource = MainApp.getClass.getResourceAsStream(s"view/${path}")
    if (resource == null) throw new Exception(s"Cannot load resource: $path")

    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    loader
  }

  def resouceImage(path: String): Image = {
    val resource = MainApp.getClass.getResourceAsStream(path)
    if (resource == null) throw new Exception(s"Cannot load resource: $path")

    new Image(resource)
  }

  /**
    * Load specifically TTF, from "font" folder
    *
    * @param path
    */
  def loadFont(path: String): Unit = {
    val fontResource = MainApp.getClass.getResourceAsStream(s"font/${path}")
    if (fontResource == null) throw new Exception(s"Cannot load font: $path")

    Font.loadFont(fontResource, 1)
  }
}
