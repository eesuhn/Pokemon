package pokemon.view

import scalafx.scene.image.ImageView
import scalafx.scene.layout.{AnchorPane, GridPane}
import scalafx.scene.control.Label
import scalafx.Includes._
import scalafx.application.Platform
import scalafxml.core.macros.sfxml
import javafx.scene.{Node => JFXNode}
import pokemon.model.{Game, Move, Player}

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
  val dialogBtn4: Label
) {

  val game = new Game()
  val gameView = new GameView(battleBg, battleDialogOne, battleDialogTwo)
  val pokemonLeftView = new GamePokemonView(pokemonLeft, pokemonLeftPane)
  val pokemonRightView = new GamePokemonView(pokemonRight, pokemonRightPane)

  def initialize(): Unit = {
    this.game.start()
    gameView.setup()
    updatePokemonViews()

    DialogController.initialize(dialogBtn1, dialogBtn2, dialogBtn3, dialogBtn4, () => setAttackDialogButtons())

    Platform.runLater {
      val scene = buttonGrid.scene.value
      scene.onKeyPressed = (event: scalafx.scene.input.KeyEvent) => DialogController.handleKeyPress(event)
      buttonGrid.requestFocus()
    }
  }

  private def updatePokemonViews(): Unit = {
    pokemonLeftView.setup(s"${this.game.player.activePokemon.pName}-back")
    pokemonRightView.setup(s"${this.game.bot.activePokemon.pName}-front")
  }

  private def setAttackDialogButtons(): Unit = {
    val moves = this.game.player.activePokemon.moves
    val dialogBtns = moves.map(move =>
      new DialogBtn(move.moveName, () =>
        performTurn(this.game.player.activePokemon.moves.indexOf(move))))

    DialogController.setDialogBtns(dialogBtns.toArray)
  }

  private def performTurn(playerMoveIndex: Int): Unit = {
    this.game.player.asInstanceOf[Player].setSelectedMoveIndex(playerMoveIndex)

    val results = this.game.performTurn()

    def showNextResult(index: Int): Unit = {
      if (index < results.length) {
        println(results(index))
        Platform.runLater {
          showNextResult(index + 1)
        }
      } else {
        if (this.game.isGameOver) {
          handleGameOver()
        } else {
          updatePokemonViews()
          DialogController.resetToMainMenu()
        }
      }
    }

    showNextResult(0)
  }

  private def handleGameOver(): Unit = this.game.winner match {
    case Some(trainer) => println(s"Game Over! ${trainer.name} wins!")
    case None => println("Game Over! It's a tie!")
  }

  initialize()
}
