package pokemon

import java.net.URL

import javafx.scene.layout.BorderPane
import pokemon.model.Layout
import pokemon.util.ResourceUtil
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene

object MainApp extends JFXApp {
  val rootLayout: BorderPane = Layout.rootLayout()
  val cssResource: URL = getClass.getResource("view/Theme.css")

  stage = new JFXApp.PrimaryStage {
    title = "Pokemon"
    scene = new Scene {
      root = rootLayout
      icons += ResourceUtil.resouceImage("misc/app-icon.png")
      stylesheets = List(cssResource.toExternalForm)
    }
  }

  Layout.landingLayout()
}
