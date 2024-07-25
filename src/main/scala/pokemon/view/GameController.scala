package pokemon.view

import pokemon.model.Game
import pokemon.model.Player
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.control.Label
import scalafx.scene.image.ImageView
import scalafx.scene.layout.AnchorPane
import scalafx.scene.layout.GridPane
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
    // updateStatusLabel()

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

  private def updateStatusLabel(): Unit = {
    val playerPokemon = this.game.player.activePokemon
    val botPokemon = this.game.bot.activePokemon
    val statusText = s"Your ${playerPokemon.pName}: ${playerPokemon.currentHP} HP\n" +
                    s"Opponent's ${botPokemon.pName}: ${botPokemon.currentHP} HP"
    gameView.updateStatus(statusText)
  }

  private def setAttackDialogButtons(): Unit = {
    val moves = this.game.player.activePokemon.moves
    val dialogBtns = moves.map(move =>
      new DialogBtn(move.moveName, () =>
        performTurn(this.game.player.activePokemon.moves.indexOf(move))))

    DialogController.setDialogBtns(dialogBtns.toArray)
  }

  private def performTurn(playerMoveIndex: Int): Unit = {
    this.game.player match {
      case player: Player => player.setSelectedMoveIndex(playerMoveIndex)
      case _ => throw new Exception("Game player is not of type Player")
    }

    val results = this.game.performTurn()

    def showNextResult(index: Int): Unit = {
      if (index < results.length) {
        gameView.updateStatus(results(index))
        Platform.runLater {
          // Thread.sleep(1000)
          showNextResult(index + 1)
        }
      } else {
        if (this.game.isGameOver) {
          handleGameOver()
        } else {
          updatePokemonViews()
          // updateStatusLabel()
          DialogController.resetToMainMenu()
        }
      }
    }

    showNextResult(0)
  }

  private def handleGameOver(): Unit = this.game.winner match {
    case Some(trainer) => gameView.updateStatus(s"Game Over! ${trainer.name} wins!")
    case None => gameView.updateStatus("Game Over! It's a tie!")
  }

  initialize()
}
