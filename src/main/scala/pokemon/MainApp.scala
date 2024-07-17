package pokemon

import pokemon.model.Game
import pokemon.util.Reader
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.Includes._
import javafx.{scene => jfxs}

object MainApp extends JFXApp {
  val loader = Reader.resourceLayout("RootLayout.fxml")
  val rootLayout = loader.getRoot[jfxs.layout.BorderPane]

  stage = new JFXApp.PrimaryStage {
    title = "Pokemon"
    scene = new Scene {
      root = rootLayout
    }
  }

  showGameLayout()

  def showGameLayout(): Unit = {
    val loader = Reader.resourceLayout("GameLayout.fxml")
    val gameLayout = loader.getRoot[jfxs.layout.AnchorPane]
    this.rootLayout.setCenter(gameLayout)
  }
}
