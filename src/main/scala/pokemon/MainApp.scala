package pokemon

import pokemon.model.Game
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}

object MainApp extends JFXApp {
  val rootResource = getClass.getResourceAsStream("view/RootLayout.fxml")
  val loader = new FXMLLoader(null, NoDependencyResolver)
  loader.load(rootResource)
  val roots = loader.getRoot[jfxs.layout.BorderPane]

  stage = new JFXApp.PrimaryStage {
    title = "Pokemon"
    scene = new Scene {
      root = roots
    }
  }

  showGameLayout()

  def showGameLayout(): Unit = {
    val resource = getClass.getResourceAsStream("view/GameLayout.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    val gameLayout = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(gameLayout)
  }
}
