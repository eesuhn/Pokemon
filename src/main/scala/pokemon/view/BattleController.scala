package pokemon.view

import pokemon.model.Battle
import pokemon.util.ResourceUtil
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

  // left pokemon
  val pokemonLeftStatBg: ImageView,
  val pokemonLeftName: Label,
  val pokemonLeftImg: ImageView,
  val pokemonLeftPane: AnchorPane,
  val pokemonLeftHpBar: ProgressBar,

  // right pokemon
  val pokemonRightStatBg: ImageView,
  val pokemonRightName: Label,
  val pokemonRightImg: ImageView,
  val pokemonRightPane: AnchorPane,
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
  private var _isKeyReleased: Boolean = true
  private var _lastKeyPressTime: Long = 0
  private val _keyPressDelay: Long = 120

  def initialize(): Unit = {
    ResourceUtil.playSound("misc/battle-theme.mp3", loop = true)
    _battle.start()
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
      menuBtns()
    )
  }

  private def initBattleComponent(): BattleComponent = {
    val background: BackgroundView = new BackgroundView(
      battleBg,
      battleDialogLeft,
      battleDialogRight,
      pokemonLeftStatBg,
      pokemonRightStatBg
    )
    val pokemonLeftView: PokemonView = new PokemonView(
      pokemonLeftName,
      pokemonLeftImg,
      pokemonLeftPane,
      pokemonLeftHpBar
    )
    val pokemonRightView: PokemonView = new PokemonView(
      pokemonRightName,
      pokemonRightImg,
      pokemonRightPane,
      pokemonRightHpBar
    )
    new BattleComponent(
      // background
      background,

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
    if (_dialogManager.isInAttackMenu) showMoveStats()
  }

  private def handleMainMenu(): Unit = {
    updatePokemonViews()
    _dialogManager.toMainMenu()
    _battleComponent.setStateDialog(s"What will ${_battle.player.activePokemon.pName} do?")
    focusInputPane()
  }

  private def showMoveStats(): Unit = {
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
    showMoveStats()
    val moves = _battle.player.activePokemon.moves
    val dialogBtns = moves
      .zipWithIndex
      .map { case (move, index) =>
        new DialogBtn(move.moveName, () => {
          val results = _battle.performTurn(Left(move))
          showResultsInDialog(results)
        })
      }
    _dialogManager.setLeftDialogBtns(dialogBtns.toArray)
    _dialogManager.updateBtnsView()
  }

  /**
    * Parse results by new line and show them in dialog
    *
    * @param results
    */
    private def showResultsInDialog(results: Seq[String]): Unit = {
    _dialogManager.clearAll()

    def showNextResult(currentIndex: Int): Unit = {
      if (currentIndex < results.length) {
        val result = results(currentIndex)
        _battleComponent.setStateDialog(result)
        updatePokemonViews()

        // Play sound effect for move
        if (result.contains("used")) {
          val moveName = result.split(" used ")(1).split("!")(0)
          playMoveSound(moveName)
        }

        setupKeyHandlers(currentIndex)
      } else {
        handleTurnEnd()
      }
    }

    def setupKeyHandlers(currentIndex: Int): Unit = {
      _scene.onKeyPressed = (event: KeyEvent) => {
        val currentTime = System.currentTimeMillis()
        if (_isKeyReleased && currentTime - _lastKeyPressTime > _keyPressDelay) {
          _isKeyReleased = false
          _lastKeyPressTime = currentTime
          showNextResult(currentIndex + 1)
        }
      }
      _scene.onKeyReleased = (_: KeyEvent) => _isKeyReleased = true
    }

    showNextResult(0)
  }

  private def playMoveSound(moveName: String): Unit = {
    val formattedMoveName = moveName.toLowerCase.replace(" ", "-")
    ResourceUtil.playSound(s"moves/$formattedMoveName.mp3")
  }

  private def handleTurnEnd(): Unit = {
    if (_battle.isBattleOver) handleBattleOver() else handleMainMenu()
  }

  private def handleBattleOver(): Unit = {
    _battle.winner match {
      case Some(trainer) => showResultsInDialog(Seq(s"Battle Over! ${trainer.name} wins!"))
      case None => showResultsInDialog(Seq("Battle Over! It's a tie!"))
    }

    // Disable input
    _scene.onKeyPressed = null
    _scene.onKeyReleased = null
  }

  private def setPokemonSwitchBtns(): Unit = {
    val availablePokemon = _battle.player.deck.filter(p => p.currentHP > 0 && p != _battle.player.activePokemon)

    if (availablePokemon.isEmpty) {
      showResultsInDialog(Seq("No available Pokemon to switch!"))
    } else {
      val pokemonBtns = availablePokemon.map { pokemon =>
        DialogBtn(s"${pokemon.pName}", () => {
          val results = _battle.performTurn(Right(pokemon))
          showResultsInDialog(results)
        })
      }.toArray

      _dialogManager.setLeftDialogBtns(pokemonBtns)
      _dialogManager.updateBtnsView()
    }
  }

  private def menuBtns(): Array[DialogBtn] = Array(
    DialogBtn("Attack", () => handleAttackBtn()),
    DialogBtn("Bag", () => handleBagBtn()),
    DialogBtn("Pokémon", () => handlePokemonBtn()),
    DialogBtn("Run", () => handleRunBtn())
  )

  private def handleAttackBtn(): Unit = {
    _dialogManager.clearAll()
    _dialogManager.isInAttackMenu(true)
    setMoveBtns()
  }

  private def handleBagBtn(): Unit = {
    _dialogManager.clearAll()
    showResultsInDialog(Seq("You don't have any items!"))
  }

  private def handlePokemonBtn(): Unit = {
    _dialogManager.clearAll()
    _dialogManager.isInPokemonMenu(true)
    setPokemonSwitchBtns()
  }

  private def handleRunBtn(): Unit = {
    _dialogManager.clearAll()
    showResultsInDialog(Seq("You couldn't get away!"))
  }

  initialize()
}
