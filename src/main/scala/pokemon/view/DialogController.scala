package pokemon.view

import scalafx.scene.control.Label
import scalafx.scene.input.{KeyCode, KeyEvent}

object DialogController {

  private case class DialogBtnState(
    texts: Array[Label],
    dialogBtns: Array[DialogBtn],
    currentSelection: Int = 0
  )

  private var _leftBtnState: DialogBtnState = _
  private var _rightBtnState: DialogBtnState = _
  private var _setMoveBtns: () => Unit = _
  private var _isInAttackMenu = false
  private val _selectedButtonStyle = """
    -fx-text-fill: #f84620;
  """

  def initialize(leftDialogBtns: Array[Label], rightDialogBtns: Array[Label], setMoveBtns: () => Unit): Unit = {
    this._leftBtnState = DialogBtnState(leftDialogBtns, emptyBtns())
    this._rightBtnState = DialogBtnState(rightDialogBtns, menuBtns())
    this._setMoveBtns = setMoveBtns
    updateView()
  }

  def handleKeyPress(event: KeyEvent): Unit = {
    val currentState = if (_isInAttackMenu) this._leftBtnState else this._rightBtnState
    val newSelection = event.code match {
      case KeyCode.UP | KeyCode.DOWN => (currentState.currentSelection + 2) % 4
      case KeyCode.LEFT | KeyCode.RIGHT =>
        if (currentState.currentSelection % 2 == 0) currentState.currentSelection + 1 else currentState.currentSelection - 1
      case _ => currentState.currentSelection
    }

    if (_isInAttackMenu) {
      this._leftBtnState = this._leftBtnState.copy(currentSelection = newSelection)
    } else {
      this._rightBtnState = this._rightBtnState.copy(currentSelection = newSelection)
    }

    event.code match {
      case KeyCode.Enter => executeCurrentSelection()
      case KeyCode.Escape if _isInAttackMenu => resetToMainMenu()
      case _ =>
    }

    updateView()
  }

  def setDialogBtns(dialogBtns: Array[DialogBtn]): Unit = {
    this._leftBtnState = this._leftBtnState.copy(dialogBtns = dialogBtns)
    this._rightBtnState = this._rightBtnState.copy(dialogBtns = emptyBtns())
    updateView()
  }

  def resetToMainMenu(): Unit = {
    this._rightBtnState = this._rightBtnState.copy(dialogBtns = menuBtns(), currentSelection = 0)
    this._leftBtnState = this._leftBtnState.copy(dialogBtns = emptyBtns(), currentSelection = 0)
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
      button.style = if (index == currentState.currentSelection) _selectedButtonStyle else ""
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
