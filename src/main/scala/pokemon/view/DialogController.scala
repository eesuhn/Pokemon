package pokemon.view

import scalafx.scene.control.Label
import scalafx.scene.input.KeyEvent
import scalafx.scene.input.KeyCode

object DialogController {

  private var _buttons: Array[Label] = _
  private var _dialogBtns: Array[DialogBtn] = _

  private var _currentSelection = 0
  private val _selectedButtonStyle = """
    -fx-text-fill: #f84620;
  """

  def initialize(
    dialogBtn1: Label,
    dialogBtn2: Label,
    dialogBtn3: Label,
    dialogBtn4: Label
  ): Unit = {

    this._buttons = Array(dialogBtn1, dialogBtn2, dialogBtn3, dialogBtn4)
    setDefaultDialogBtns()
    updateSelectedButton()
  }

  /**
    * Handle key press event of arrow keys and Enter
    * 
    * - Navigate to opposite when end is reached
    *
    * @param event
    */
  def handleKeyPress(event: KeyEvent): Unit = {
    event.code match {
      case KeyCode.UP =>
        this._currentSelection = (this._currentSelection + 2) % 4
      case KeyCode.DOWN =>
        this._currentSelection = (this._currentSelection + 2) % 4
      case KeyCode.LEFT =>
        this._currentSelection = if (this._currentSelection % 2 == 0) this._currentSelection + 1 else this._currentSelection - 1
      case KeyCode.RIGHT =>
        this._currentSelection = if (this._currentSelection % 2 == 1) this._currentSelection - 1 else this._currentSelection + 1
      case KeyCode.Enter =>
        executeCurrentSelection()
      case _ =>
    }
    updateSelectedButton()
  }

  private def updateSelectedButton(): Unit = {
    this._buttons.zipWithIndex.foreach { case (button, index) =>
      button.style = if (index == this._currentSelection) _selectedButtonStyle else ""
    }
  }

  private def updateButtonTexts(): Unit = {
    this._buttons.zip(this._dialogBtns).foreach { case (button, dialogBtn) =>
      button.text = dialogBtn.text
    }
  }

  /**
    * Set the dialog buttons for the dialog controller
    *
    * - Must be exactly 4
    *
    * @param dialogBtns
    */
  def setDialogBtns(dialogBtns: Array[DialogBtn]): Unit = {
    if (dialogBtns.length != 4) throw new Exception("Must provide exactly 4 DialogBtn")
    this._dialogBtns = dialogBtns
    updateButtonTexts()
  }

  private def setDefaultDialogBtns(): Unit = {
    this._dialogBtns = Array(
      new DialogBtn("Attack", () => println("Attack action")),
      new DialogBtn("Bag", () => println("Bag action")),
      new DialogBtn("Pokémon", () => println("Pokémon action")),
      new DialogBtn("Run", () => println("Run action"))
    )
    updateButtonTexts()
  }

  private def executeCurrentSelection(): Unit = {
    this._dialogBtns(this._currentSelection).execute()
  }
}

class DialogBtn(
  val text: String,
  val action: () => Unit
) {

  def execute(): Unit = this.action()
}
