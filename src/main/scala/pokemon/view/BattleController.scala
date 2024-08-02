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
  val pokemonLeftTypeImg1: ImageView,
  val pokemonLeftTypeImg2: ImageView,
  val pokemonLeftImg: ImageView,
  val pokemonLeftPane: AnchorPane,
  val pokemonLeftHpBar: ProgressBar,

  // right pokemon
  val pokemonRightStatBg: ImageView,
  val pokemonRightName: Label,
  val pokemonRightTypeImg1: ImageView,
  val pokemonRightTypeImg2: ImageView,
  val pokemonRightImg: ImageView,
  val pokemonRightPane: AnchorPane,
  val pokemonRightHpBar: ProgressBar,

  // input
  val inputPane: Pane,

  // left dialog
  // state
  val stateDialogTxt: Label,

  // left buttons
  val leftDialogBtn1: Label,
  val leftDialogBtn2: Label,
  val leftDialogBtn3: Label,
  val leftDialogBtn4: Label,

  // right dialog
  // move stats
  val moveTypeImg: ImageView,
  val moveTypeTxt: Label,
  val moveCat: ImageView,
  val powerTxtLabel: Label,
  val powerTxt: Label,
  val accuracyTxtLabel: Label,
  val accuracyTxt: Label,

  // pokemon current stats
  val pokemonCurrentImg: ImageView,
  val pokemonCurrentTypeImg1: ImageView,
  val pokemonCurrentTypeImg2: ImageView,
  val pokemonCurrentHpTxt: Label,
  val pokemonCurrentHp: Label,
  val pokemonCurrentAttackTxt: Label,
  val pokemonCurrentAttack: Label,
  val pokemonCurrentDefenseTxt: Label,
  val pokemonCurrentDefense: Label,

  // right buttons
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
      menuBtns
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
      pokemonLeftTypeImg1,
      pokemonLeftTypeImg2,
      pokemonLeftImg,
      pokemonLeftPane,
      pokemonLeftHpBar
    )
    val pokemonRightView: PokemonView = new PokemonView(
      pokemonRightName,
      pokemonRightTypeImg1,
      pokemonRightTypeImg2,
      pokemonRightImg,
      pokemonRightPane,
      pokemonRightHpBar
    )
    val pokemonCurrentStats: PokemonStatsView = new PokemonStatsView(
      pokemonCurrentImg,
      pokemonCurrentTypeImg1,
      pokemonCurrentTypeImg2,
      pokemonCurrentHpTxt,
      pokemonCurrentHp,
      pokemonCurrentAttackTxt,
      pokemonCurrentAttack,
      pokemonCurrentDefenseTxt,
      pokemonCurrentDefense
    )
    new BattleComponent(
      // background
      background,

      // pokemon
      pokemonLeftView,
      pokemonRightView,

      // left dialog
      // state
      stateDialogTxt,

      // right dialog
      // move stats
      moveTypeImg,
      moveTypeTxt,
      moveCat,
      powerTxtLabel,
      powerTxt,
      accuracyTxtLabel,
      accuracyTxt,

      // pokemon current stats
      pokemonCurrentStats
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
    _battleComponent.leftPokemonTypes(
      _battle.player.activePokemon.pTypeNames.head,
      if (_battle.player.activePokemon.pTypeNames.length > 1) _battle.player.activePokemon.pTypeNames(1) else ""
    )
    _battleComponent.rightPokemonTypes(
      _battle.bot.activePokemon.pTypeNames.head,
      if (_battle.bot.activePokemon.pTypeNames.length > 1) _battle.bot.activePokemon.pTypeNames(1) else ""
    )
  }

  /**
    * Focus on updated input pane and event handlers
    */
  private def focusInputPane(): Unit = {
    _scene.onKeyPressed = (event: KeyEvent) => _dialogManager.handleKeyPress(event, hookKeyPress)
    _scene.onKeyReleased = (event: KeyEvent) => _dialogManager.handleKeyRelease(event)
    inputPane.requestFocus()
  }

  private def hookKeyPress(): Unit = {
    // In attack menu
    if (_dialogManager.isInAttackMenu) showMoveStats()

    // In switching menu
    if (_dialogManager.isInPokemonMenu) {
      val currentSelection = _dialogManager.leftBtnState.currentSelection
      val pokemonName = _battle.availablePlayerPokemon()(currentSelection).pName
      showCurrentPokemonStats(pokemonName)
    }
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
    updatePokemonViews()

    def showNextResult(currentIndex: Int): Unit = {
      if (currentIndex < results.length) {
        val result = results(currentIndex)
        _battleComponent.setStateDialog(result)

        // Move SFX if it didn't miss
        if (!result.contains("missed") && result.contains("used")) {
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
    // Battle is over
    if (_battle.isBattleOver) handleBattleOver()

    // Check if Player current Pokemon is fainted
    else if (!_battle.player.isActivePokemonAlive) playerFaintSwitch()

    // Check if Player just switched Pokemon after fainted
    else if (_battle.playerJustSwitchedAfterFaint) {
      _battle.opponentJustSwitched(false)
      handleMainMenu()
    }

    // Prompt Player to switch Pokemon after bot switched
    else if (_battle.opponentJustSwitched && _battle.player.moreThanOnePokemonAlive) {
      promptPlayerSwitch()
      _battle.opponentJustSwitched(false)
    }

    // Just go back to main menu
    else handleMainMenu()
  }

  /**
    * Prompt to switch if player has more than one Pokemon alive
    *
    * Otherwise switch to the only Pokemon alive
    */
  private def playerFaintSwitch(): Unit = {
    if (_battle.player.moreThanOnePokemonAlive) {
      handlePokemonSwitchPrompt()
      focusInputPane()
    } else {
      val results = _battle.switchPokemon(_battle.player, _battle.player.alivePokemons.head)
      showResultsInDialog(results)
    }
  }

  private def promptPlayerSwitch(): Unit = {
    _dialogManager.clearAll(clearFlags = true)
    _battleComponent.setStateDialog("Do you want to switch Pokemon?")
    val switchPromptBtns = Array(
      DialogBtn("Yes", () => handlePokemonSwitchPrompt()),
      DialogBtn("No", () => handleMainMenu())
    )

    _dialogManager.setRightDialogBtns(switchPromptBtns)
    _dialogManager.updateBtnsView()
    focusInputPane()
  }

  private def handlePokemonSwitchPrompt(): Unit = {
    _dialogManager.clearAll(clearFlags = true)
    _dialogManager.isInPokemonMenu(true)
    setPokemonSwitchBtns(switchWithoutTurn = true)
  }

  private def handleBattleOver(): Unit = {
    _battle.winner match {
      case Some(trainer) => _battleComponent.setStateDialog(s"Battle over! ${trainer.name} wins!")
      case None => _battleComponent.setStateDialog("Battle over! It's a tie!")
    }

    _scene.onKeyPressed = null
    _scene.onKeyReleased = null

    ResourceUtil.stopSound("misc/battle-theme.mp3")
    ResourceUtil.playSound("misc/ending-theme.mp3")

    Platform.runLater {
      _scene.onKeyPressed = (event: KeyEvent) => {
        _battleComponent.setStateDialog("Press any key to exit game...")
        _scene.onKeyPressed = (_: KeyEvent) => Platform.exit()
      }
    }
  }

  /**
    * @param switchWithoutTurn If true, switch Pokemon without performing turn
    */
  private def setPokemonSwitchBtns(switchWithoutTurn: Boolean = false): Unit = {
    val availablePokemon = _battle.availablePlayerPokemon()
    if (availablePokemon.isEmpty) {
      showResultsInDialog(Seq("No available Pokemon to switch!"))
    } else {
      val pokemonBtns = availablePokemon.map { pokemon =>
        DialogBtn(s"${pokemon.pName}", () => {
          showCurrentPokemonStats(pokemon.pName)

          val results = if (switchWithoutTurn) {
            _battle.switchPokemon(_battle.player, pokemon)
          } else {
            _battle.performTurn(Right(pokemon))
          }
          showResultsInDialog(results)
        })
      }.toArray

      _dialogManager.setLeftDialogBtns(pokemonBtns)
      _dialogManager.updateBtnsView()

      showCurrentPokemonStats(availablePokemon.head.pName)
    }
  }

  private def showCurrentPokemonStats(pokemonName: String): Unit = {
    _battle.player.deck.find(_.pName == pokemonName).foreach { pokemon =>
      val pTypes = pokemon.pTypeNames

      _battleComponent.updatePokemonCurrentStats(
        pokemonName.toLowerCase,
        pTypes.head,
        if (pTypes.length > 1) pTypes(1) else "",
        pokemon.currentHP.toString,
        pokemon.attack.value.toString,
        pokemon.defense.value.toString
      )
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
