package pokemon.view

import scalafx.scene.image.ImageView
import scalafx.scene.layout.{AnchorPane, GridPane}
import scalafx.scene.control.Label
import scalafx.Includes._
import scalafx.application.Platform
import scalafxml.core.macros.sfxml
import javafx.scene.{Node => JFXNode}

@sfxml
class GameController(
  val battleBg: ImageView,
  val battleDialogOne: ImageView,
  val battleDialogTwo: ImageView,
  val pokemonLeftPane: AnchorPane,
  val pokemonRightPane: AnchorPane,
  val pokemonLeft: ImageView,
  val pokemonRight: ImageView,
  val buttonGrid: GridPane
) {

  val gameView = new GameView(battleBg, battleDialogOne, battleDialogTwo)
  val pokemonLeftView = new GamePokemonView(pokemonLeft, pokemonLeftPane)
  val pokemonRightView = new GamePokemonView(pokemonRight, pokemonRightPane)

  gameView.setup()
  pokemonLeftView.setup("pokes/Mewtwo-back.gif")
  pokemonRightView.setup("pokes/Snorlax-front.gif")

  lazy val buttons: Array[Label] = buttonGrid.children.collect {
    case node: JFXNode if node.isInstanceOf[javafx.scene.control.Label] =>
      new Label(node.asInstanceOf[javafx.scene.control.Label])
  }.toArray

  Platform.runLater {
    val scene = buttonGrid.scene.value
    scene.onKeyPressed = (event: scalafx.scene.input.KeyEvent) => DialogController.handleKeyPress(event, buttons)
    buttonGrid.requestFocus()
    DialogController.updateSelectedButton(buttons)
  }
}
