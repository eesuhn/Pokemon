package pokemon.view

import pokemon.util.ResourceUtil
import scalafx.scene.image.ImageView

class GameView(
  battleBg: ImageView,
  battleDialogOne: ImageView,
  battleDialogTwo: ImageView
) {

  def setup(): Unit = {
    battleBg.image = ResourceUtil.resouceImage("misc/battle-bg.gif")
    battleDialogOne.image = ResourceUtil.resouceImage("misc/battle-dialog-one-left.png")
    battleDialogTwo.image = ResourceUtil.resouceImage("misc/battle-dialog-one-right.png")
  }
}
