package pokemon.view

import pokemon.util.ResourceUtil
import scalafx.scene.image.ImageView
import scalafxml.core.macros.sfxml

@sfxml
class GameController(
  val battleBg: ImageView,
  val battleDialogOne: ImageView,
  val battleDialogTwo: ImageView,
  ) {

  battleBg.image = ResourceUtil.resouceImage("misc/battle-bg.gif")
  battleDialogOne.image = ResourceUtil.resouceImage("misc/battle-dialog-one.png")
  battleDialogTwo.image = ResourceUtil.resouceImage("misc/battle-dialog-two.png")
}
