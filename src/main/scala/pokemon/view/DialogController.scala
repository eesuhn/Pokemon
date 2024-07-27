package pokemon.view

import scalafx.scene.control.Label
import scalafx.scene.input.{KeyCode, KeyEvent}

object DialogController {

  private case class DialogBtnState(
    texts: Array[Label],
    dialogBtns: Array[DialogBtn],
    currentSelection: Int = 0,
    activeButtonCount: Int = 4
  )

  private var _leftBtnState: DialogBtnState = _
  private var _rightBtnState: DialogBtnState = _
  private var _setMoveBtns: () => Unit = _
  private var _isInAttackMenu = false
  private val _selectedButtonStyle = """
    -fx-text-fill: #f84620;
  """
  private val _defaultButtonStyle = """
    -fx-text-fill: black;
  """

  def initialize(
    leftDialogBtns: Array[Label],
    rightDialogBtns: Array[Label],
    setMoveBtns: () => Unit
  ): Unit = {

    this._leftBtnState = DialogBtnState(leftDialogBtns, emptyBtns())
    // this._rightBtnState = DialogBtnState(rightDialogBtns, menuBtns())
    this._rightBtnState = DialogBtnState(rightDialogBtns, emptyBtns())
    this._setMoveBtns = setMoveBtns
    updateView()
  }

  def handleKeyPress(event: KeyEvent): Unit = {
    val currentState = if (_isInAttackMenu) _leftBtnState else _rightBtnState
    val newSelection = getNewSelection(currentState, event.code)

    updateCurrentState(newSelection)

    event.code match {
      case KeyCode.Enter => executeCurrentSelection()
      case KeyCode.Escape if _isInAttackMenu => resetToMainMenu()
      case _ =>
    }

    updateView()
  }

  /**
    * Returns the new selection based on the current state and the key code
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

  /**
    * Moves the selection up or down by the given offset
    *
    * - Wrap around the selection if it goes out of bounds
    *
    * @param state
    * @param offset
    * @return
    */
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

  /**
    * Moves the selection left or right by the given offset
    *
    * - Wrap around the selection if it goes out of bounds
    *
    * @param state
    * @param offset
    * @return
    */
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
    this._leftBtnState = this._leftBtnState.copy(dialogBtns = dialogBtns, activeButtonCount = activeButtonCount)
    this._rightBtnState = this._rightBtnState.copy(dialogBtns = emptyBtns())
    updateView()
  }

  def resetToMainMenu(): Unit = {
    this._rightBtnState = this._rightBtnState.copy(dialogBtns = menuBtns(), currentSelection = 0, activeButtonCount = 4)
    this._leftBtnState = this._leftBtnState.copy(dialogBtns = emptyBtns(), currentSelection = 0, activeButtonCount = 0)
    _isInAttackMenu = false
    updateView()
  }

  private def updateView(): Unit = {
    updateButtonTexts()
    updateSelectedButton()
  }

  private def updateButtonTexts(): Unit = {
    resetButtonTexts()
    this._leftBtnState.texts.zip(this._leftBtnState.dialogBtns).foreach { case (button, dialogBtn) =>
      button.text = dialogBtn.text
    }
    this._rightBtnState.texts.zip(this._rightBtnState.dialogBtns).foreach { case (button, dialogBtn) =>
      button.text = dialogBtn.text
    }
  }

  private def updateSelectedButton(): Unit = {
    val currentState = if (_isInAttackMenu) this._leftBtnState else this._rightBtnState
    currentState.texts.zipWithIndex.foreach { case (button, index) =>
      if (index < currentState.activeButtonCount) {
        button.style = if (index == currentState.currentSelection) _selectedButtonStyle else _defaultButtonStyle
      } else {
        button.style = ""
        button.text = ""
      }
    }
  }

  private def executeCurrentSelection(): Unit = {
    if (_isInAttackMenu) {
      this._leftBtnState.dialogBtns(this._leftBtnState.currentSelection).execute()
    } else {
      this._rightBtnState.dialogBtns(this._rightBtnState.currentSelection).execute()
    }
  }

  private def menuBtns(): Array[DialogBtn] = {
    Array(
      DialogBtn("Attack", () => handleAttackBtn()),
      DialogBtn("Bag", () => println("Bag action")),
      DialogBtn("Pokémon", () => println("Pokémon action")),
      DialogBtn("Run", () => println("Run action"))
    )
  }

  private def handleAttackBtn(): Unit = {
    _isInAttackMenu = true
    _setMoveBtns()
  }

  private def resetButtonTexts(): Unit = {
    this._leftBtnState.texts.foreach(_.text = "")
    this._rightBtnState.texts.foreach(_.text = "")
  }

  private def emptyBtns(): Array[DialogBtn] = Array.empty
}

case class DialogBtn(
  text: String,
  action: () => Unit
) {
  def execute(): Unit = action()
}
