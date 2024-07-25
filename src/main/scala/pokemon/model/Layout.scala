package pokemon.model

import pokemon.MainApp
import pokemon.util.ResourceUtil
import javafx.{scene => jfxs}

object Layout {

  def rootLayout(): jfxs.layout.BorderPane = {
    val loader = ResourceUtil.resourceLayout("RootLayout")
    loader.getRoot[jfxs.layout.BorderPane]
  }

  def showGameLayout(): Unit = {
    val loader = ResourceUtil.resourceLayout("GameLayout")
    val gameLayout = loader.getRoot[jfxs.layout.AnchorPane]
    MainApp.rootLayout.setCenter(gameLayout)
  }
}
