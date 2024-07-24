package pokemon.view

import scalafx.scene.control.Label
import scalafx.scene.input.KeyEvent
import scalafx.scene.input.KeyCode

object DialogController {
  private var currentSelection = 0
  private val selectedButtonStyle = """
    -fx-text-fill: #f84620;
  """

  def handleKeyPress(event: KeyEvent, buttons: Array[Label]): Unit = {
    event.code match {
      case KeyCode.UP =>
        currentSelection = (currentSelection + 2) % 4
      case KeyCode.DOWN =>
        currentSelection = (currentSelection + 2) % 4
      case KeyCode.LEFT =>
        currentSelection = if (currentSelection % 2 == 0) currentSelection + 1 else currentSelection - 1
      case KeyCode.RIGHT =>
        currentSelection = if (currentSelection % 2 == 1) currentSelection - 1 else currentSelection + 1
      case KeyCode.Enter =>
        executeCurrentSelection()
      case _ =>
    }
    updateSelectedButton(buttons)
  }

  def updateSelectedButton(buttons: Array[Label]): Unit = {
    buttons.zipWithIndex.foreach { case (button, index) =>
      button.style = if (index == currentSelection) selectedButtonStyle else ""
    }
  }

  def executeCurrentSelection(): Unit = currentSelection match {
    case 0 => handleDialogBtn1()
    case 1 => handleDialogBtn2()
    case 2 => handleDialogBtn3()
    case 3 => handleDialogBtn4()
  }

  def handleDialogBtn1(): Unit = {
    println("Dialog button 1 clicked")
  }

  def handleDialogBtn2(): Unit = {
    println("Dialog button 2 clicked")
  }

  def handleDialogBtn3(): Unit = {
    println("Dialog button 3 clicked")
  }

  def handleDialogBtn4(): Unit = {
    println("Dialog button 4 clicked")
  }
}
