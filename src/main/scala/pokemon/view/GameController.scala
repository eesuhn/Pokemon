package pokemon.view

import pokemon.model.Game
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.control.Label
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{AnchorPane, GridPane, Pane}
import scalafxml.core.macros.sfxml

@sfxml
class GameController(
  val battleBg: ImageView,
  val battleDialogLeft: ImageView,
  val battleDialogRight: ImageView,
  val pokemonLeftPane: AnchorPane,
  val pokemonRightPane: AnchorPane,
  val pokemonLeft: ImageView,
  val pokemonRight: ImageView,
  val inputPane: Pane,
  val leftButtonGrid: GridPane,
  val leftDialogBtn1: Label,
  val leftDialogBtn2: Label,
  val leftDialogBtn3: Label,
  val leftDialogBtn4: Label,
  val rightButtonGrid: GridPane,
  val rightDialogBtn1: Label,
  val rightDialogBtn2: Label,
  val rightDialogBtn3: Label,
  val rightDialogBtn4: Label,
  val stateDialogTxt: Label,
  val powerTxt: Label,
  val accuracyTxt: Label,
) {

  val game: Game = new Game()
  val gameView: GameView = initGameView()

  def initialize(): Unit = {
    this.game.start()
    gameView.setupGameView()
    updatePokemonViews()
    initDialogController()
    setupKeyboardInput()
  }

  private def initGameView(): GameView = {
    val pokemonLeftView: GamePokemonView = new GamePokemonView(pokemonLeft, pokemonLeftPane)
    val pokemonRightView: GamePokemonView = new GamePokemonView(pokemonRight, pokemonRightPane)
    new GameView(battleBg, battleDialogLeft, battleDialogRight, pokemonLeftView, pokemonRightView)
  }

  private def updateStateDialogTxt(text: String): Unit = {
    // stateDialogTxt.text = text
    println(text)
  }

  private def updatePokemonViews(): Unit = {
    gameView.updatePokemonViews(
      this.game.player.activePokemon.pName,
      this.game.bot.activePokemon.pName
    )
  }

  private def initDialogController(): Unit = {
    val leftDialogBtns = Array(leftDialogBtn1, leftDialogBtn2, leftDialogBtn3, leftDialogBtn4)
    val rightDialogBtns = Array(rightDialogBtn1, rightDialogBtn2, rightDialogBtn3, rightDialogBtn4)

    DialogController.initialize(
      leftDialogBtns,
      rightDialogBtns,
      setMoveBtns = () => setMoveBtns()
    )
  }

  private def setupKeyboardInput(): Unit = {
    Platform.runLater {
      val scene = inputPane.scene.value
      scene.onKeyPressed = (event: scalafx.scene.input.KeyEvent) => DialogController.handleKeyPress(event)
      inputPane.requestFocus()
    }
  }

  /**
    * Obtain moves from active Pokemon and set them as dialog buttons
    */
  private def setMoveBtns(): Unit = {
    val moves = this.game.player.activePokemon.moves
    val dialogBtns = moves
      .zipWithIndex
      .map { case (move, index) =>
        new DialogBtn(move.moveName, () => controlTurn(index))
      }
    DialogController.setDialogBtns(dialogBtns.toArray)
  }

  private def controlTurn(moveIndex: Int): Unit = {
    this.game.player.moveIndex(moveIndex)
    val results = this.game.performTurn()
    showResultsInDialog(results)
  }

  /**
    * Parse results by new line and show them in dialog
    *
    * @param results
    */
  private def showResultsInDialog(results: Seq[String]): Unit = {
    def showNextResult(index: Int): Unit = {
      if (index < results.length) {
        updateStateDialogTxt(results(index))
        Platform.runLater {
          // TimeLine or PauseTransition for delay
          showNextResult(index + 1)
        }
      } else {
        handleTurnEnd()
      }
    }
    showNextResult(0)
  }

  /**
    * Handle turn end by checking if game is over or not
    */
  private def handleTurnEnd(): Unit = {
    if (this.game.isGameOver) {
      handleGameOver()
    } else {
      updatePokemonViews()
      DialogController.resetToMainMenu()
    }
  }

  private def handleGameOver(): Unit = this.game.winner match {
    case Some(trainer) => println(s"Game Over! ${trainer.name} wins!")
    case None => println("Game Over! It's a tie!")
  }

  initialize()
}
