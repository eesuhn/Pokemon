package pokemon.util

import pokemon.MainApp
import scalafx.scene.image.Image
import scalafx.scene.text.Font
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

object ResourceUtil {

  /**
    * Load a resource layout from "view" folder
    *
    * @param path
    *
    * @throws Exception if resource is not found
    * @return
    */
  def resourceLayout(path: String): FXMLLoader = {
    val resource = MainApp.getClass.getResourceAsStream(s"view/${path}.fxml")
    if (resource == null) throw new Exception(s"Cannot load resource: $path")

    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    loader
  }

  /**
    * Load a resource image from any folder
    *
    * @param path
    *
    * @throws Exception if resource is not found
    * @return
    */
  def resouceImage(path: String): Image = {
    val resource = MainApp.getClass.getResourceAsStream(path)
    if (resource == null) throw new Exception(s"Cannot load resource: $path")

    new Image(resource)
  }

  /**
    * Load specifically TTF, from "font" folder
    *
    * @param path
    *
    * @throws Exception if resource is not found
    */
  def loadFont(path: String): Unit = {
    val fontResource = MainApp.getClass.getResourceAsStream(s"font/${path}")
    if (fontResource == null) throw new Exception(s"Cannot load font: $path")

    Font.loadFont(fontResource, 1)
  }
}
