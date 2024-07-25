package pokemon.view

import scalafx.scene.image.ImageView
import scalafx.scene.layout.{AnchorPane, GridPane}
import scalafx.scene.control.Label
import scalafx.Includes._
import scalafx.application.Platform
import scalafxml.core.macros.sfxml
import javafx.scene.{Node => JFXNode}
import pokemon.model.{Game, Move}

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
    game.start()
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
    pokemonLeftView.setup(s"pokes/${game.player.activePokemon.pName}-back.gif")
    pokemonRightView.setup(s"pokes/${game.bot.activePokemon.pName}-front.gif")
  }

  private def setAttackDialogButtons(): Unit = {
    val moves = game.player.activePokemon.moves
    val dialogBtns = moves.map(move => new DialogBtn(move.moveName, () => performTurn(game.player.activePokemon.moves.indexOf(move))))
    DialogController.setDialogBtns(dialogBtns.toArray)
  }

  private def performTurn(playerMoveIndex: Int): Unit = {
    val results = game.performTurn(playerMoveIndex)

    def showNextResult(index: Int): Unit = {
      if (index < results.length) {
        println(results(index))
        Platform.runLater {
          // Thread.sleep(1000) // Wait for 1 second between messages
          showNextResult(index + 1)
        }
      } else {
        // After showing all results
        if (game.isGameOver) {
          handleGameOver()
        } else {
          updatePokemonViews()
          DialogController.resetToMainMenu()
        }
      }
    }

    showNextResult(0)
  }

  private def handleGameOver(): Unit = {
    val winner = game.winner
    winner match {
      case Some(player) => println(s"Game Over! ${player.playerName} wins!")
      case None => println("Game Over! It's a tie!")
    }
    // Disable further moves or implement a "New Game" option
  }

  initialize()
}
