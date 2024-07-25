package pokemon.view

import scalafx.scene.control.Label
import scalafx.scene.input.{KeyCode, KeyEvent}

object DialogController {
  private case class DialogState(buttons: Array[Label], dialogBtns: Array[DialogBtn], currentSelection: Int = 0)

  private var _state: DialogState = _
  private var _setAttackDialogButtons: () => Unit = _
  private var _isInAttackMenu = false
  private val _selectedButtonStyle = """
    -fx-text-fill: #f84620;
  """

  def initialize(dialogBtns: Array[Label], setAttackDialogButtons: () => Unit): Unit = {
    this._setAttackDialogButtons = setAttackDialogButtons
    this._state = DialogState(dialogBtns, menuBtns())
    updateView()
  }

  def handleKeyPress(event: KeyEvent): Unit = {
    // Handle navigation
    val newSelection = event.code match {
      case KeyCode.UP | KeyCode.DOWN => (this._state.currentSelection + 2) % 4
      case KeyCode.LEFT | KeyCode.RIGHT =>
        if (this._state.currentSelection % 2 == 0) this._state.currentSelection + 1 else this._state.currentSelection - 1
      case _ => this._state.currentSelection
    }

    this._state = this._state.copy(currentSelection = newSelection)

    // Handle confirm and cancel
    event.code match {
      case KeyCode.Enter => executeCurrentSelection()
      case KeyCode.Escape if this._isInAttackMenu => resetToMainMenu()
      case _ =>
    }

    updateView()
  }

  def setDialogBtns(dialogBtns: Array[DialogBtn]): Unit = {
    this._state = this._state.copy(dialogBtns = dialogBtns)
    updateView()
  }

  def resetToMainMenu(): Unit = {
    this._state = this._state.copy(dialogBtns = menuBtns(), currentSelection = 0)
    this._isInAttackMenu = false
    updateView()
  }

  private def updateView(): Unit = {
    updateButtonTexts()
    updateSelectedButton()
  }

  private def updateButtonTexts(): Unit = {
    this._state.buttons.zip(this._state.dialogBtns).foreach { case (button, dialogBtn) =>
      button.text = dialogBtn.text
    }
  }

  private def updateSelectedButton(): Unit = {
    this._state.buttons.zipWithIndex.foreach { case (button, index) =>
      button.style = if (index == this._state.currentSelection) this._selectedButtonStyle else ""
    }
  }

  private def executeCurrentSelection(): Unit = {
    this._state.dialogBtns(this._state.currentSelection).execute()
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
    this._isInAttackMenu = true
    _setAttackDialogButtons()
  }
}

case class DialogBtn(text: String, action: () => Unit) {
  def execute(): Unit = action()
}
