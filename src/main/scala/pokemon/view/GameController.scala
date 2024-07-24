package pokemon.view

import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{AnchorPane, GridPane}
import scalafx.scene.input.KeyEvent
import scalafx.scene.control.Label
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import javafx.scene.{Node => JFXNode}
import scalafx.application.Platform
import scalafx.scene.Scene

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

  var currentSelection = 0
  lazy val buttons: Array[Label] = buttonGrid.children.collect {
    case node: JFXNode if node.isInstanceOf[javafx.scene.control.Label] =>
      new Label(node.asInstanceOf[javafx.scene.control.Label])
  }.toArray

  // CSS class for the selected button
  val selectedButtonStyle = "-fx-border-color: yellow; -fx-border-width: 2px;"

  def updateSelectedButton(): Unit = {
    println(s"Updating selected button: $currentSelection")  // Debug print
    buttons.zipWithIndex.foreach { case (button, index) =>
      if (index == currentSelection) {
        button.style = selectedButtonStyle
      } else {
        button.style = ""
      }
    }
  }

  def handleKeyPress(event: KeyEvent): Unit = {
    println(s"Key pressed: ${event.code}")  // Debug print
    event.code match {
      case scalafx.scene.input.KeyCode.W =>
        currentSelection = if (currentSelection < 2) currentSelection else currentSelection - 2
      case scalafx.scene.input.KeyCode.S =>
        currentSelection = if (currentSelection > 1) currentSelection else currentSelection + 2
      case scalafx.scene.input.KeyCode.A =>
        currentSelection = if (currentSelection % 2 == 0) currentSelection + 1 else currentSelection - 1
      case scalafx.scene.input.KeyCode.D =>
        currentSelection = if (currentSelection % 2 == 1) currentSelection - 1 else currentSelection + 1
      case scalafx.scene.input.KeyCode.Enter =>
        executeCurrentSelection()
      case _ =>
    }
    println(s"Current selection after key press: $currentSelection")  // Debug print
    updateSelectedButton()
  }

  def executeCurrentSelection(): Unit = currentSelection match {
    case 0 => handleDialogBtn1()
    case 1 => handleDialogBtn2()
    case 2 => handleDialogBtn3()
    case 3 => handleDialogBtn4()
  }

  def handleDialogBtn1(): Unit = DialogController.handleDialogBtn1()
  def handleDialogBtn2(): Unit = DialogController.handleDialogBtn2()
  def handleDialogBtn3(): Unit = DialogController.handleDialogBtn3()
  def handleDialogBtn4(): Unit = DialogController.handleDialogBtn4()

  def initialize(): Unit = {
    println("Initializing GameController")  // Debug print
    Platform.runLater {
      val scene = buttonGrid.scene.value
      scene.onKeyPressed = handleKeyPress
      buttonGrid.requestFocus()
      updateSelectedButton()
    }
  }

  // Call initialize when the controller is created
  initialize()
}
