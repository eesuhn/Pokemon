package pokemon.view

import pokemon.model.Game
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.image.ImageView
import scalafx.scene.input.{KeyCode, KeyEvent}
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
  val powerTxtLabel: Label,
  val powerTxt: Label,
  val accuracyTxtLabel: Label,
  val accuracyTxt: Label,
  val moveCat: ImageView,
  val moveTypeImg: ImageView,
  val moveTypeTxt: Label
) {

  private val _game: Game = new Game()
  private val _gameView: GameView = initGameView()
  private var _gameState: String = ""
  private var _scene: Scene = null

  def gameState(state: String): Unit = {
    this._gameState = state

    this._gameState match {
      case "main_menu" => handleMainMenu()
      case _ => throw new Exception(s"Unknown game state: ${this._gameState}")
    }
  }

  private def handleMainMenu(): Unit = {
    updatePokemonViews()
    DialogController.resetToMainMenu()
    this._gameView.stateDialogTxt(s"What will ${this._game.player.activePokemon.pName} do?")
  }

  def initialize(): Unit = {
    this._game.start()
    this._gameView.setup()
    updatePokemonViews()
    initDialogController()

    Platform.runLater {
      this._scene = inputPane.scene.value
      focusInputPane()
      gameState("main_menu")
    }
  }

  /**
    * Handle most of the game elements, besides dialog buttons
    *
    * @return
    */
  private def initGameView(): GameView = {
    val pokemonLeftView: GamePokemonView = new GamePokemonView(pokemonLeft, pokemonLeftPane)
    val pokemonRightView: GamePokemonView = new GamePokemonView(pokemonRight, pokemonRightPane)
    new GameView(
      battleBg,
      battleDialogLeft,
      battleDialogRight,
      pokemonLeftView,
      pokemonRightView,
      stateDialogTxt,
      powerTxtLabel,
      powerTxt,
      accuracyTxtLabel,
      accuracyTxt,
      moveCat,
      moveTypeImg,
      moveTypeTxt
    )
  }

  private def updatePokemonViews(): Unit = {
    this._gameView.pokemonViews(
      this._game.player.activePokemon.pName,
      this._game.bot.activePokemon.pName
    )
  }

  private def initDialogController(): Unit = {
    val leftDialogBtns = Array(leftDialogBtn1, leftDialogBtn2, leftDialogBtn3, leftDialogBtn4)
    val rightDialogBtns = Array(rightDialogBtn1, rightDialogBtn2, rightDialogBtn3, rightDialogBtn4)

    DialogController.initialize(
      this._gameView,
      leftDialogBtns,
      rightDialogBtns,
      setMoveBtns = () => setMoveBtns()
    )
  }

  private def focusInputPane(): Unit = {
    this._scene.onKeyPressed = (event: KeyEvent) => DialogController.handleKeyPress(event)
    inputPane.requestFocus()
  }

  /**
    * Obtain moves from active Pokemon and set them as dialog buttons
    */
  private def setMoveBtns(): Unit = {
    val moves = this._game.player.activePokemon.moves
    val dialogBtns = moves
      .zipWithIndex
      .map { case (move, index) =>
        new DialogBtn(move.moveName, () => controlTurn(index))
      }
    DialogController.setDialogBtns(dialogBtns.toArray)
  }

  private def controlTurn(moveIndex: Int): Unit = {
    this._game.player.moveIndex(moveIndex)
    val results = this._game.performTurn()
    showResultsInDialog(results)
  }

  /**
    * Parse results by new line and show them in dialog
    *
    * @param results
    */
  private def showResultsInDialog(results: Seq[String]): Unit = {
    DialogController.clearMoveBtns()
    var currentIndex = 0

    def showNextResult(): Unit = {
      if (currentIndex < results.length) {
        val result = results(currentIndex)
        Platform.runLater {
          this._gameView.stateDialogTxt(result)
        }
        currentIndex += 1
      } else {
        handleTurnEnd()
        focusInputPane()
      }
    }

    Option(this._scene).foreach { scene =>
      scene.onKeyPressed = (event: KeyEvent) =>
        if (event.code == KeyCode.Enter) showNextResult()
    }

    showNextResult()
  }

  /**
    * Handle turn end by checking if game is over or not
    */
  private def handleTurnEnd(): Unit = {
    if (this._game.isGameOver) {
      handleGameOver()
    } else {
      gameState("main_menu")
    }
  }

  private def handleGameOver(): Unit = this._game.winner match {
    case Some(trainer) => println(s"Game Over! ${trainer.name} wins!")
    case None => println("Game Over! It's a tie!")
  }

  initialize()
}
