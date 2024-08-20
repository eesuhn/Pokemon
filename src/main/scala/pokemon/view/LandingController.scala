package pokemon.view

import pokemon.util.ResourceUtil
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.image.ImageView
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.Pane
import scalafxml.core.macros.sfxml
import pokemon.model.Layout

@sfxml
class LandingController(
  val landingBg: ImageView,
  val inputPane: Pane
) {

  private var _scene: Scene = null
  private var _isKeyReleased: Boolean = true

  private def initialize(): Unit = {
    landingBg.image = ResourceUtil.resouceImage("misc/landing-bg.gif")

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
        case KeyCode.Enter | KeyCode.Space => Layout.battleLayout()
        case _ =>
      }
    }
  }

  private def handleKeyRelease(): Unit = {
    _isKeyReleased = true
  }

  initialize()
}
