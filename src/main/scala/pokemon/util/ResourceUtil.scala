package pokemon.util

import pokemon.MainApp
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.scene.image.Image

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
    new Image(MainApp.getClass.getResourceAsStream(path))
  }
}
