package pokemon.view

import pokemon.util.ResourceUtil
import scalafx.application.Platform
import scalafx.scene.image.ImageView
import scalafx.scene.layout.AnchorPane

class GameView(
  val battleBg: ImageView,
  val battleDialogLeft: ImageView,
  val battleDialogRight: ImageView,
  val pokemonLeft: GamePokemonView,
  val pokemonRight: GamePokemonView
) {

  def setupGameView(): Unit = {
    battleBg.image = ResourceUtil.resouceImage("misc/battle-bg.gif")
    battleDialogLeft.image = ResourceUtil.resouceImage("misc/battle-dialog-left.png")
    battleDialogRight.image = ResourceUtil.resouceImage("misc/battle-dialog-right.png")
  }

  def updatePokemonViews(leftPokemon: String, rightPokemon: String): Unit = {
    pokemonLeft.setup(s"${leftPokemon}-back")
    pokemonRight.setup(s"${rightPokemon}-front")
  }
}

case class GamePokemonView(
  imageView: ImageView,
  anchorPane: AnchorPane
) {

  /**
    * Source from `pokes` directory with `.gif` extension
    *
    * @param imagePath
    */
  def setup(imagePath: String): Unit = {
    val image = ResourceUtil.resouceImage(s"pokes/${imagePath}.gif")
    imageView.image = image
    imageView.preserveRatio = true
    imageView.smooth = true

    Platform.runLater {
      positionPokemon()
    }

    imageView.image.onChange { (_, _, newImage) =>
      if (newImage != null) {
        positionPokemon()
      }
    }
  }

  /**
    * Position Pokemon image within the AnchorPane
    *
    * - Center horizontally
    * - Anchor to bottom
    */
  private def positionPokemon(): Unit = {
    val newImage = imageView.image.value

    if (newImage != null) {
      val imageWidth = newImage.getWidth()
      val imageHeight = newImage.getHeight()
      val paneWidth = anchorPane.width.value

      imageView.fitWidth = imageWidth
      imageView.fitHeight = imageHeight

      // Center horizontally
      val leftAnchor = (paneWidth - imageView.fitWidth.value) / 2
      AnchorPane.setLeftAnchor(imageView, leftAnchor)

      // Anchor to bottom
      AnchorPane.setBottomAnchor(imageView, 0.0)
    }
  }
}
