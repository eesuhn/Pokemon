package pokemon.model

import javafx.{scene => jfxs}
import pokemon.MainApp
import pokemon.util.ResourceUtil

object Layout {

  def rootLayout(): jfxs.layout.BorderPane = {
    val loader = ResourceUtil.resourceLayout("RootLayout")
    loader.getRoot[jfxs.layout.BorderPane]
  }

  def battleLayout(): Unit = {
    val loader = ResourceUtil.resourceLayout("BattleLayout")
    val gameLayout = loader.getRoot[jfxs.layout.AnchorPane]
    MainApp.rootLayout.setCenter(gameLayout)
  }

  def landingLayout(): Unit = {
    val loader = ResourceUtil.resourceLayout("LandingLayout")
    val landingLayout = loader.getRoot[jfxs.layout.AnchorPane]
    MainApp.rootLayout.setCenter(landingLayout)
  }

  def tutorialLayout(): Unit = {
    val loader = ResourceUtil.resourceLayout("TutorialLayout")
    val tutorialLayout = loader.getRoot[jfxs.layout.AnchorPane]
    MainApp.rootLayout.setCenter(tutorialLayout)
  }
}
