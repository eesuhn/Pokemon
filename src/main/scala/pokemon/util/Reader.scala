package pokemon.util

import pokemon.MainApp
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

object Reader {
  def resourceLayout(path: String): FXMLLoader = {
    val resource = MainApp.getClass.getResourceAsStream(s"view/${path}")
    if (resource == null) throw new Exception(s"Cannot load resource: $path")

    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    loader
  }
}
