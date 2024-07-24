package pokemon.view

import scalafx.scene.control.Label
import scalafx.scene.input.KeyEvent
import scalafx.scene.input.KeyCode

object DialogController {

  private var _buttons: Array[Label] = _

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
    setButtonTexts("Attack", "Bag", "Pokémon", "Run")
    updateSelectedButton()
  }

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

  private def setButtonTexts(
    text1: String,
    text2: String,
    text3: String,
    text4: String
  ): Unit = {

    this._buttons(0).text = text1
    this._buttons(1).text = text2
    this._buttons(2).text = text3
    this._buttons(3).text = text4
  }

  private def executeCurrentSelection(): Unit = this._currentSelection match {
    case 0 => handleDialogBtn1()
    case 1 => handleDialogBtn2()
    case 2 => handleDialogBtn3()
    case 3 => handleDialogBtn4()
  }

  private def handleDialogBtn1(): Unit = {
    println("Dialog button 1 clicked")
  }

  private def handleDialogBtn2(): Unit = {
    println("Dialog button 2 clicked")
  }

  private def handleDialogBtn3(): Unit = {
    println("Dialog button 3 clicked")
  }

  private def handleDialogBtn4(): Unit = {
    println("Dialog button 4 clicked")
  }
}
