package pokemon.view

import pokemon.model.Game
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.control.Label
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{AnchorPane, GridPane}
import scalafxml.core.macros.sfxml

@sfxml
class GameController(
  val battleBg: ImageView,
  val battleDialogOne: ImageView,
  val battleDialogTwo: ImageView,
  val pokemonLeftPane: AnchorPane,
  val pokemonRightPane: AnchorPane,
  val pokemonLeft: ImageView,
  val pokemonRight: ImageView,
  val buttonGrid: GridPane,
  val dialogBtn1: Label,
  val dialogBtn2: Label,
  val dialogBtn3: Label,
  val dialogBtn4: Label,
  val statusLabel: Label
) {

  val game: Game = new Game()
  val gameView: GameView = new GameView(battleBg, battleDialogOne, battleDialogTwo, statusLabel)
  val pokemonLeftView: GamePokemonView = new GamePokemonView(pokemonLeft, pokemonLeftPane)
  val pokemonRightView: GamePokemonView = new GamePokemonView(pokemonRight, pokemonRightPane)

  def initialize(): Unit = {
    this.game.start()
    gameView.setup()
    updatePokemonViews()
    initializeDialogController()
    setupKeyboardInput()
  }

  private def updatePokemonViews(): Unit = {
    pokemonLeftView.setup(s"${this.game.player.activePokemon.pName}-back")
    pokemonRightView.setup(s"${this.game.bot.activePokemon.pName}-front")
  }

  private def initializeDialogController(): Unit = {
    val dialogButtons = Array(dialogBtn1, dialogBtn2, dialogBtn3, dialogBtn4)
    DialogController.initialize(dialogButtons, () => setAttackDialogButtons())
  }

  private def setupKeyboardInput(): Unit = {
    Platform.runLater {
      val scene = buttonGrid.scene.value
      scene.onKeyPressed = (event: scalafx.scene.input.KeyEvent) => DialogController.handleKeyPress(event)
      buttonGrid.requestFocus()
    }
  }

  private def setAttackDialogButtons(): Unit = {
    val moves = this.game.player.activePokemon.moves
    val dialogBtns = moves.zipWithIndex.map { case (move, index) =>
      new DialogBtn(move.moveName, () => controlTurn(index))
    }
    DialogController.setDialogBtns(dialogBtns.toArray)
  }

  private def controlTurn(moveIndex: Int): Unit = {
    this.game.player.moveIndex(moveIndex)
    val results = this.game.performTurn()
    showResults(results)
  }

  /**
    * Parse results by new line and show each result with delay
    *
    * @param results
    */
  private def showResults(results: Seq[String]): Unit = {
    def showNextResult(index: Int): Unit = {
      if (index < results.length) {
        gameView.updateStatus(results(index))
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

  private def handleTurnEnd(): Unit = {
    if (this.game.isGameOver) {
      handleGameOver()
    } else {
      updatePokemonViews()
      DialogController.resetToMainMenu()
    }
  }

  private def handleGameOver(): Unit = this.game.winner match {
    case Some(trainer) => gameView.updateStatus(s"Game Over! ${trainer.name} wins!")
    case None => gameView.updateStatus("Game Over! It's a tie!")
  }

  initialize()
}
