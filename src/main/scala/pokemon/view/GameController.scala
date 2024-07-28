package pokemon.view

import pokemon.model.Game
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control.{Label, ProgressBar}
import scalafx.scene.image.ImageView
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.{AnchorPane, Pane}
import scalafxml.core.macros.sfxml

@sfxml
class GameController(
  // background
  val battleBg: ImageView,
  val battleDialogLeft: ImageView,
  val battleDialogRight: ImageView,

  // pokemon
  val pokemonLeftPane: AnchorPane,
  val pokemonRightPane: AnchorPane,
  val pokemonLeftHpBar: ProgressBar,
  val pokemonLeft: ImageView,
  val pokemonRight: ImageView,
  val pokemonRightHpBar: ProgressBar,

  // input
  val inputPane: Pane,

  // left dialog
  val stateDialogTxt: Label,
  // left dialog buttons
  val leftDialogBtn1: Label,
  val leftDialogBtn2: Label,
  val leftDialogBtn3: Label,
  val leftDialogBtn4: Label,

  // right dialog
  val moveTypeImg: ImageView,
  val moveTypeTxt: Label,
  val moveCat: ImageView,
  val powerTxtLabel: Label,
  val powerTxt: Label,
  val accuracyTxtLabel: Label,
  val accuracyTxt: Label,
  // right dialog buttons
  val rightDialogBtn1: Label,
  val rightDialogBtn2: Label,
  val rightDialogBtn3: Label,
  val rightDialogBtn4: Label
) {

  private val _game: Game = new Game()
  private val _gameView: GameView = initGameView()
  private var _scene: Scene = null

  def initialize(): Unit = {
    _game.start()
    _gameView.setup()
    updatePokemonViews()
    initDialogController()

    Platform.runLater {
      _scene = inputPane.scene.value
      focusInputPane()
      handleMainMenu()
    }
  }

  private def initGameView(): GameView = {
    val pokemonLeftView: GamePokemonView = new GamePokemonView(
      pokemonLeft,
      pokemonLeftPane,
      pokemonLeftHpBar
    )
    val pokemonRightView: GamePokemonView = new GamePokemonView(
      pokemonRight,
      pokemonRightPane,
      pokemonRightHpBar
    )
    new GameView(
      // background
      battleBg,
      battleDialogLeft,
      battleDialogRight,

      // pokemon
      pokemonLeftView,
      pokemonRightView,

      // left dialog
      stateDialogTxt,

      // right dialog
      moveTypeImg,
      moveTypeTxt,
      moveCat,
      powerTxtLabel,
      powerTxt,
      accuracyTxtLabel,
      accuracyTxt
    )
  }

  private def updatePokemonViews(): Unit = {
    _gameView.pokemonViews(
      _game.player.activePokemon.pName,
      _game.bot.activePokemon.pName
    )
    _gameView.pokemonHpBars(
      _game.player.activePokemon.pokemonHpPercentage,
      _game.bot.activePokemon.pokemonHpPercentage
    )
  }

  private def initDialogController(): Unit = {
    val leftDialogBtns = Array(leftDialogBtn1, leftDialogBtn2, leftDialogBtn3, leftDialogBtn4)
    val rightDialogBtns = Array(rightDialogBtn1, rightDialogBtn2, rightDialogBtn3, rightDialogBtn4)

    DialogController.initialize(
      _game,
      _gameView,
      leftDialogBtns,
      rightDialogBtns,
      setMoveBtns
    )
  }

  private def focusInputPane(): Unit = {
    _scene.onKeyPressed = (event: KeyEvent) => DialogController.handleKeyPress(event, hookKeyPress)
    inputPane.requestFocus()
  }

  private def hookKeyPress(): Unit = {
    if (DialogController.isInAttackMenu) showStats()
  }

  private def handleMainMenu(): Unit = {
    updatePokemonViews()
    DialogController.resetToMainMenu()
    _gameView.setStateDialog(s"What will ${_game.player.activePokemon.pName} do?")
  }

  private def showStats(): Unit = {
    val currentSelection = DialogController.leftBtnState.currentSelection
    val moveName = _game.player.activePokemon.moves(currentSelection).moveName

    _game.player.activePokemon.moves.find(_.moveName == moveName).foreach { move =>
      _gameView.updateMoveStats(
        move.movePower,
        move.accuracy.toString,
        move.moveCategoryName,
        move.moveTypeName
      )
    }
  }

  private def setMoveBtns(): Unit = {
    showStats()
    val moves = _game.player.activePokemon.moves
    val dialogBtns = moves
      .zipWithIndex
      .map { case (move, index) =>
        new DialogBtn(move.moveName, () => controlTurn(index))
      }
    DialogController.setDialogBtns(dialogBtns.toArray)
  }

  private def controlTurn(moveIndex: Int): Unit = {
    _game.player.moveIndex(moveIndex)
    val results = _game.performTurn()
    showResultsInDialog(results)
  }

  /**
    * Parse results by new line and show them in dialog
    *
    * @param results
    */
  private def showResultsInDialog(results: Seq[String]): Unit = {
    DialogController.clearMoveBtns()
    _gameView.clearRightDialogPane()

    def showNextResult(currentIndex: Int): Unit = {
      if (currentIndex < results.length) {
        _gameView.setStateDialog(results(currentIndex))
        _scene.onKeyPressed = (_: KeyEvent) => showNextResult(currentIndex + 1)
      } else {
        handleTurnEnd()
      }
    }
    showNextResult(0)
  }

  private def handleTurnEnd(): Unit = {
    if (_game.isGameOver) handleGameOver() else handleMainMenu()
    focusInputPane()
  }

  private def handleGameOver(): Unit = _game.winner match {
    case Some(trainer) => println(s"Game Over! ${trainer.name} wins!")
    case None => println("Game Over! It's a tie!")
  }

  initialize()
}
