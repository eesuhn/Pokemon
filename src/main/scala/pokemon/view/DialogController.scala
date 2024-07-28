package pokemon.view

import pokemon.model.Game
import scalafx.scene.control.Label
import scalafx.scene.input.{KeyCode, KeyEvent}

object DialogController {

  case class DialogBtnState(
    texts: Array[Label],
    dialogBtns: Array[DialogBtn],
    currentSelection: Int = 0,
    activeButtonCount: Int = 0
  )

  private var _game: Game = _
  private var _gameView: GameView = _
  private var _isInAttackMenu = false
  private var _leftBtnState: DialogBtnState = _
  private var _rightBtnState: DialogBtnState = _
  private var _setMoveBtns: () => Unit = _

  private val _selectedBtnStyle = """
    -fx-text-fill: #f84620;
  """
  private val _defaultBtnStyle = """
    -fx-text-fill: black;
  """

  def isInAttackMenu: Boolean = _isInAttackMenu
  def leftBtnState: DialogBtnState = _leftBtnState
  def rightBtnState: DialogBtnState = _rightBtnState

  def initialize(
    game: Game,
    gameView: GameView,
    leftDialogBtns: Array[Label],
    rightDialogBtns: Array[Label],
    setMoveBtns: () => Unit
  ): Unit = {

    _game = game
    _gameView = gameView
    _leftBtnState = DialogBtnState(leftDialogBtns, Array.empty)
    _rightBtnState = DialogBtnState(rightDialogBtns, menuBtns(), activeButtonCount = 4)
    _setMoveBtns = setMoveBtns
    updateView()
  }

  def handleKeyPress(event: KeyEvent, hookKeyPress: () => Unit): Unit = {
    val currentState = if (_isInAttackMenu) _leftBtnState else _rightBtnState
    val newSelection = getNewSelection(currentState, event.code)

    updateCurrentState(newSelection)
    hookKeyPress()

    event.code match {
      case KeyCode.Enter => executeCurrent()
      case KeyCode.Escape if _isInAttackMenu => {
        resetToMainMenu()
        _gameView.setStateDialog(s"What will ${_game.player.activePokemon.pName} do?")
      }
      case _ =>
    }
    updateView()
  }

  /**
    * Keyboard navigation
    *
    * `moveVertically` and `moveHorizontally` keep navigation wrap around
    *
    * @param state
    * @param keyCode
    * @return
    */
  private def getNewSelection(state: DialogBtnState, keyCode: KeyCode): Int = {
    keyCode match {
      case KeyCode.UP => moveVertically(state, -2)
      case KeyCode.DOWN => moveVertically(state, 2)
      case KeyCode.LEFT => moveHorizontally(state, -1)
      case KeyCode.RIGHT => moveHorizontally(state, 1)
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
    if (_isInAttackMenu) {
      _leftBtnState = _leftBtnState.copy(currentSelection = newSelection)
    } else {
      _rightBtnState = _rightBtnState.copy(currentSelection = newSelection)
    }
  }

  def setDialogBtns(dialogBtns: Array[DialogBtn]): Unit = {
    val activeButtonCount = dialogBtns.length
    _leftBtnState = _leftBtnState.copy(dialogBtns = dialogBtns, activeButtonCount = activeButtonCount)
    _rightBtnState = _rightBtnState.copy(dialogBtns = Array.empty)
    updateView()
  }

  def resetToMainMenu(): Unit = {
    _gameView.clearRightDialogPane()
    _leftBtnState = _leftBtnState.copy(dialogBtns = Array.empty, currentSelection = 0, activeButtonCount = 0)
    _rightBtnState = _rightBtnState.copy(dialogBtns = menuBtns(), currentSelection = 0, activeButtonCount = 4)
    _isInAttackMenu = false
    updateView()
  }

  private def updateView(): Unit = {
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
    val currentState = if (_isInAttackMenu) _leftBtnState else _rightBtnState
    currentState.texts.zipWithIndex.foreach { case (button, index) =>
      button.style = if (index < currentState.activeButtonCount) {
        if (index == currentState.currentSelection) _selectedBtnStyle else _defaultBtnStyle
      } else ""
      if (index >= currentState.activeButtonCount) button.text = ""
    }
  }

  private def executeCurrent(): Unit = {
    if (_isInAttackMenu) {
      _leftBtnState.dialogBtns(_leftBtnState.currentSelection).execute()
    } else {
      _rightBtnState.dialogBtns(_rightBtnState.currentSelection).execute()
    }
  }

  private def menuBtns(): Array[DialogBtn] = Array(
    DialogBtn("Attack", () => handleAttackBtn()),
    DialogBtn("Bag", () => println("Bag action")),
    DialogBtn("Pokémon", () => println("Pokémon action")),
    DialogBtn("Run", () => println("Run action"))
  )

  private def handleAttackBtn(): Unit = {
    _gameView.clearLeftDialogPane()
    _isInAttackMenu = true
    _setMoveBtns()
  }

  def clearMoveBtns(): Unit = {
    _leftBtnState = _leftBtnState.copy(dialogBtns = Array.empty)
    updateView()
  }
}

case class DialogBtn(
  text: String,
  action: () => Unit
) {
  def execute(): Unit = action()
}
