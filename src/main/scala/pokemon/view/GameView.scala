package pokemon.view

import pokemon.util.ResourceUtil
import scalafx.scene.image.ImageView
import scalafx.scene.control.Label

class GameView(
  battleBg: ImageView,
  battleDialogOne: ImageView,
  battleDialogTwo: ImageView,
  statusLabel: Label
) {

  def setup(): Unit = {
    battleBg.image = ResourceUtil.resouceImage("misc/battle-bg.gif")
    battleDialogOne.image = ResourceUtil.resouceImage("misc/battle-dialog-one-left.png")
    battleDialogTwo.image = ResourceUtil.resouceImage("misc/battle-dialog-one-right.png")
  }

  def updateStatus(text: String): Unit = {
    statusLabel.text = text
  }
}
