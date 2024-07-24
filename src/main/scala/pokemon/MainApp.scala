package pokemon

import pokemon.model.Layout
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.Includes._

object MainApp extends JFXApp {
  val rootLayout = Layout.rootLayout()
  val cssResource = getClass.getResource("view/Theme.css")

  stage = new JFXApp.PrimaryStage {
    title = "Pokemon"
    scene = new Scene {
      root = rootLayout
      stylesheets = List(cssResource.toExternalForm)
    }
  }

  Layout.showGameLayout()
}
