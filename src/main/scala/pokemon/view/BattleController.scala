package pokemon.view

import pokemon.model.Battle
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control.{Label, ProgressBar}
import scalafx.scene.image.ImageView
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.{AnchorPane, Pane}
import scalafxml.core.macros.sfxml

@sfxml
class BattleController(
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

  private val _battle: Battle = new Battle()
  private val _battleComponent: BattleComponent = initBattleComponent()
  private val _dialogManager: DialogManager = initDialogManager()
  private var _scene: Scene = null

  // Handle key press delay
  private var isKeyReleased: Boolean = true
  private var lastKeyPressTime: Long = 0
  private val keyPressDelay: Long = 200

  def initialize(): Unit = {
    _battle.start()
    _battleComponent.setup()
    _dialogManager.setup()
    updatePokemonViews()

    Platform.runLater {
      _scene = inputPane.scene.value
      focusInputPane()
      handleMainMenu()
    }
  }

  private def initDialogManager(): DialogManager = {
    val leftDialogBtns = Array(
      leftDialogBtn1,
      leftDialogBtn2,
      leftDialogBtn3,
      leftDialogBtn4
    )
    val rightDialogBtns = Array(
      rightDialogBtn1,
      rightDialogBtn2,
      rightDialogBtn3,
      rightDialogBtn4
    )
    new DialogManager(
      _battle,
      _battleComponent,
      leftDialogBtns,
      rightDialogBtns,
      setMoveBtns
    )
  }

  private def initBattleComponent(): BattleComponent = {
    val pokemonLeftView: BattlePokemonView = new BattlePokemonView(
      pokemonLeft,
      pokemonLeftPane,
      pokemonLeftHpBar
    )
    val pokemonRightView: BattlePokemonView = new BattlePokemonView(
      pokemonRight,
      pokemonRightPane,
      pokemonRightHpBar
    )
    new BattleComponent(
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
    _battleComponent.pokemonViews(
      _battle.player.activePokemon.pName,
      _battle.bot.activePokemon.pName
    )
    _battleComponent.pokemonHpBars(
      _battle.player.activePokemon.pokemonHpPercentage,
      _battle.bot.activePokemon.pokemonHpPercentage
    )
  }

  private def focusInputPane(): Unit = {
    _scene.onKeyPressed = (event: KeyEvent) => _dialogManager.handleKeyPress(event, hookKeyPress)
    _scene.onKeyReleased = (event: KeyEvent) => _dialogManager.handleKeyRelease(event)
    inputPane.requestFocus()
  }

  private def hookKeyPress(): Unit = {
    if (_dialogManager.isInAttackMenu) showStats()
  }

  private def handleMainMenu(): Unit = {
    updatePokemonViews()
    _dialogManager.resetToMainMenu()
    _battleComponent.setStateDialog(s"What will ${_battle.player.activePokemon.pName} do?")
  }

  private def showStats(): Unit = {
    val currentSelection = _dialogManager.leftBtnState.currentSelection
    val moveName = _battle.player.activePokemon.moves(currentSelection).moveName

    _battle.player.activePokemon.moves.find(_.moveName == moveName).foreach { move =>
      _battleComponent.updateMoveStats(
        move.movePower,
        move.accuracy.toString,
        move.moveCategoryName,
        move.moveTypeName
      )
    }
  }

  private def setMoveBtns(): Unit = {
    showStats()
    val moves = _battle.player.activePokemon.moves
    val dialogBtns = moves
      .zipWithIndex
      .map { case (move, index) =>
        new DialogBtn(move.moveName, () => controlTurn(index))
      }
    _dialogManager.setDialogBtns(dialogBtns.toArray)
  }

  private def controlTurn(moveIndex: Int): Unit = {
    _battle.player.moveIndex(moveIndex)
    val results = _battle.performTurn()
    showResultsInDialog(results)
  }

  /**
    * Parse results by new line and show them in dialog
    *
    * @param results
    */
  private def showResultsInDialog(results: Seq[String]): Unit = {
    _dialogManager.clearMoveBtns()
    _battleComponent.clearRightDialogPane()

    def showNextResult(currentIndex: Int): Unit = {
      if (currentIndex < results.length) {
        _battleComponent.setStateDialog(results(currentIndex))
        setupKeyHandlers(currentIndex)
      } else {
        handleTurnEnd()
      }
    }

    def setupKeyHandlers(currentIndex: Int): Unit = {
      _scene.onKeyPressed = (event: KeyEvent) => {
        val currentTime = System.currentTimeMillis()
        if (isKeyReleased && currentTime - lastKeyPressTime > keyPressDelay) {
          isKeyReleased = false
          lastKeyPressTime = currentTime
          showNextResult(currentIndex + 1)
        }
      }
      _scene.onKeyReleased = (_: KeyEvent) => isKeyReleased = true
    }

    showNextResult(0)
  }

  private def handleTurnEnd(): Unit = {
    if (_battle.isBattleOver) handleBattleOver() else handleMainMenu()
    focusInputPane()
  }

  private def handleBattleOver(): Unit = _battle.winner match {
    case Some(trainer) => println(s"Battle Over! ${trainer.name} wins!")
    case None => println("Battle Over! It's a tie!")
  }

  initialize()
}
