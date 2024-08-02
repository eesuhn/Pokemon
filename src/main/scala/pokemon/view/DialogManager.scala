package pokemon.view

import pokemon.model.Battle
import scalafx.scene.control.Label
import scalafx.scene.input.{KeyCode, KeyEvent}

class DialogManager(
  val battle: Battle,
  val battleComponent: BattleComponent,
  val leftDialogBtns: Array[Label],
  val rightDialogBtns: Array[Label],
  val menuBtns: Array[DialogBtn]
) {

  private var _isInAttackMenu = false
  private var _isInPokemonMenu = false
  private var _leftBtnState: DialogBtnState = _
  private var _rightBtnState: DialogBtnState = _

  // Handle key press delay
  private var _lastPressedKey: Option[KeyCode] = None
  private var _isKeyReleased: Boolean = true
  private var _lastKeyPressTime: Long = 0
  private val _keyPressDelay: Long = 120

  private val _selectedBtnStyle = """
    -fx-text-fill: #f84620;
  """
  private val _defaultBtnStyle = """
    -fx-text-fill: black;
  """

  def isInAttackMenu: Boolean = _isInAttackMenu
  def isInPokemonMenu: Boolean = _isInPokemonMenu
  def leftBtnState: DialogBtnState = _leftBtnState
  def rightBtnState: DialogBtnState = _rightBtnState

  def isInAttackMenu(value: Boolean): Unit = _isInAttackMenu = value
  def isInPokemonMenu(value: Boolean): Unit = _isInPokemonMenu = value

  private def initialize(): Unit = {
    _leftBtnState = DialogBtnState(leftDialogBtns, Array.empty)
    _rightBtnState = DialogBtnState(rightDialogBtns, menuBtns, activeButtonCount = 4)
    updateBtnsView()
  }

  def handleKeyPress(event: KeyEvent, hookKeyPress: () => Unit): Unit = {
    val currentTime = System.currentTimeMillis()

    // Handle key press delay
    if ((_isKeyReleased || _lastPressedKey != Some(event.code)) &&
        currentTime - _lastKeyPressTime > _keyPressDelay) {
      _isKeyReleased = false
      _lastPressedKey = Some(event.code)
      _lastKeyPressTime = currentTime

      val currentState = if (_isInAttackMenu || _isInPokemonMenu) _leftBtnState else _rightBtnState
      val newSelection = getNewSelection(currentState, event.code)

      updateCurrentState(newSelection)
      hookKeyPress()

      event.code match {
        case KeyCode.Enter | KeyCode.Space => executeCurrent()
        case KeyCode.Escape | KeyCode.BackSpace => {
          if (battle.player.isActivePokemonAlive &&
            (_isInAttackMenu || _isInPokemonMenu)
          ) {
            toMainMenu()
            battleComponent.setStateDialog(s"What will ${battle.player.activePokemon.pName} do?")
          }
        }
        case _ =>
      }
      updateBtnsView()
    }
  }

  def handleKeyRelease(event: KeyEvent): Unit = {
    if (_lastPressedKey == Some(event.code)) {
      _isKeyReleased = true
      _lastPressedKey = None
    }
  }

  /**
    * - Keep navigation horizontally and vertically wrap around
    *
    * @param state
    * @param keyCode
    * @return
    */
  private def getNewSelection(state: DialogBtnState, keyCode: KeyCode): Int = {
    keyCode match {
      case KeyCode.UP | KeyCode.W => moveVertically(state, -2)
      case KeyCode.DOWN | KeyCode.S => moveVertically(state, 2)
      case KeyCode.LEFT | KeyCode.A => moveHorizontally(state, -1)
      case KeyCode.RIGHT | KeyCode.D => moveHorizontally(state, 1)
      case _ => state.currentSelection
    }
  }

  private def moveVertically(state: DialogBtnState, offset: Int): Int = {
    val currentRow = state.currentSelection / 2
    val currentCol = state.currentSelection % 2
    val newRow = (currentRow + offset / 2 + 2) % 2

    if (newRow != currentRow && state.activeButtonCount > currentCol + newRow * 2) {
      newRow * 2 + currentCol
    } else {
      state.currentSelection
    }
  }

  private def moveHorizontally(state: DialogBtnState, offset: Int): Int = {
    val currentRow = state.currentSelection / 2
    val newCol = (state.currentSelection + offset + 2) % 2

    if (newCol < (state.activeButtonCount - currentRow * 2)) {
      currentRow * 2 + newCol
    } else {
      state.currentSelection
    }
  }

  private def updateCurrentState(newSelection: Int): Unit = {
    if (_isInAttackMenu || _isInPokemonMenu) {
      _leftBtnState = _leftBtnState.copy(currentSelection = newSelection)
    } else {
      _rightBtnState = _rightBtnState.copy(currentSelection = newSelection)
    }
  }

  def setLeftDialogBtns(dialogBtns: Array[DialogBtn]): Unit = _leftBtnState = _leftBtnState.copy(
    dialogBtns = dialogBtns, currentSelection = 0, activeButtonCount = dialogBtns.length)

  def setRightDialogBtns(dialogBtns: Array[DialogBtn]): Unit = _rightBtnState = _rightBtnState.copy(
    dialogBtns = dialogBtns, currentSelection = 0, activeButtonCount = dialogBtns.length)

  def toMainMenu(): Unit = {
    clearAll(clearFlags = true)
    setRightDialogBtns(menuBtns)
    updateBtnsView()
  }

  /**
    * Clear all dialog buttons and dialog panes
    *
    * @param clearFlags
    */
  def clearAll(clearFlags: Boolean = false): Unit = {
    setLeftDialogBtns(Array.empty)
    setRightDialogBtns(Array.empty)
    updateBtnsView()
    battleComponent.clearLeftDialogPane()
    battleComponent.clearRightDialogPane()

    if (clearFlags) {
      _isInAttackMenu = false
      _isInPokemonMenu = false
    }
  }

  def updateBtnsView(): Unit = {
    updateButtonTexts()
    updateSelectedButton()
  }

  private def updateButtonTexts(): Unit = {
    updateStateButtonTexts(_leftBtnState)
    updateStateButtonTexts(_rightBtnState)
  }

  private def updateStateButtonTexts(state: DialogBtnState): Unit = {
    state.texts.foreach(_.text = "")
    state.texts.zip(state.dialogBtns).foreach { case (button, dialogBtn) =>
      button.text = dialogBtn.text
    }
  }

  private def updateSelectedButton(): Unit = {
    val currentState = if (_isInAttackMenu || _isInPokemonMenu) _leftBtnState else _rightBtnState
    currentState.texts.zipWithIndex.foreach { case (button, index) =>
      button.style = if (index < currentState.activeButtonCount) {
        if (index == currentState.currentSelection) _selectedBtnStyle else _defaultBtnStyle
      } else ""
      if (index >= currentState.activeButtonCount) button.text = ""
    }
  }

  private def executeCurrent(): Unit = {
    if (_isInAttackMenu || _isInPokemonMenu) {
      _leftBtnState.dialogBtns(_leftBtnState.currentSelection).execute()
    } else {
      _rightBtnState.dialogBtns(_rightBtnState.currentSelection).execute()
    }
  }

  initialize()
}

case class DialogBtn(
  text: String,
  action: () => Unit
) {
  def execute(): Unit = action()
}

case class DialogBtnState(
  texts: Array[Label],
  dialogBtns: Array[DialogBtn],
  currentSelection: Int = 0,
  activeButtonCount: Int = 0
)
