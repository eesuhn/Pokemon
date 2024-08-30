package pokemon.view

import pokemon.model.Layout
import pokemon.util.ResourceUtil
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.image.ImageView
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.Pane
import scalafxml.core.macros.sfxml

@sfxml
class TutorialController(
  val landingBg: ImageView,
  val inputPane: Pane
) {

  private var _scene: Scene = null
  private var _isKeyReleased: Boolean = true

  private def initialize(): Unit = {
    landingBg.image = ResourceUtil.resouceImage("misc/tutorial-bg.png")

    Platform.runLater {
      _scene = inputPane.scene.value
      focusInputPane()
    }
  }

  private def focusInputPane(): Unit = {
    _scene.onKeyPressed = (event: KeyEvent) => handleKeyPress(event)
    _scene.onKeyReleased = (_) => handleKeyRelease()
    inputPane.requestFocus()
  }

  private def handleKeyPress(event: KeyEvent): Unit = {
    if (_isKeyReleased) {
      _isKeyReleased = false

      event.code match {
        case KeyCode.Escape => {
          _scene.onKeyPressed = null
          _scene.onKeyReleased = null
          ResourceUtil.playSound("misc/button-a.mp3")
          Layout.landingLayout()
        }
        case _ =>
      }
    }
  }

  private def handleKeyRelease(): Unit = {
    _isKeyReleased = true
  }

  initialize()
}
