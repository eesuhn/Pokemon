package pokemon.model

import pokemon.MainApp
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}

object Layout {

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

  def rootLayout(): jfxs.layout.BorderPane = {
    val loader = resourceLayout("RootLayout.fxml")
    loader.getRoot[jfxs.layout.BorderPane]
  }

  def showGameLayout(): Unit = {
    val loader = resourceLayout("GameLayout.fxml")
    val gameLayout = loader.getRoot[jfxs.layout.AnchorPane]
    MainApp.rootLayout.setCenter(gameLayout)
  }
}
