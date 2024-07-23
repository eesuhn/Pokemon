package pokemon.view

import pokemon.util.ResourceUtil
import scalafx.scene.image.ImageView
import scalafxml.core.macros.sfxml

@sfxml
class GameController(
  val battleBg: ImageView,
  ) {

  battleBg.image = ResourceUtil.resouceImage("misc/battle-bg.gif")
}
