package pokemon.view

import pokemon.util.ResourceUtil
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.AnchorPane
import scalafxml.core.macros.sfxml

@sfxml
class GameController(
  val battleBg: ImageView,
  val battleDialogOne: ImageView,
  val battleDialogTwo: ImageView,
  val pokemonLeftPane: AnchorPane,
  val pokemonRightPane: AnchorPane,
  val pokemonLeft: ImageView,
  val pokemonRight: ImageView
) {

  battleBg.image = ResourceUtil.resouceImage("misc/battle-bg.gif")
  battleDialogOne.image = ResourceUtil.resouceImage("misc/battle-dialog-one.png")
  battleDialogTwo.image = ResourceUtil.resouceImage("misc/battle-dialog-two.png")

  setupPokemon(pokemonLeft, "pokes/Mewtwo-back.gif", pokemonLeftPane)
  setupPokemon(pokemonRight, "pokes/Snorlax-front.gif", pokemonRightPane)

  private def setupPokemon(imageView: ImageView, imagePath: String, anchorPane: AnchorPane): Unit = {
    val image = ResourceUtil.resouceImage(imagePath)
    imageView.image = image
    imageView.preserveRatio = true
    imageView.smooth = true

    Platform.runLater {
      positionPokemon(imageView, anchorPane)
    }

    imageView.image.onChange { (_, _, newImage) =>
      if (newImage != null) {
        positionPokemon(imageView, anchorPane)
      }
    }
  }

  /**
    * Position Pokemon image within the AnchorPane
    *
    * - Center horizontally
    * - Anchor to bottom
    *
    * @param imageView
    * @param anchorPane
    */
  private def positionPokemon(imageView: ImageView, anchorPane: AnchorPane): Unit = {
    val newImage = imageView.image.value

    if (newImage != null) {
      val imageWidth = newImage.width.value
      val imageHeight = newImage.height.value
      val paneWidth = anchorPane.width.value
      val paneHeight = anchorPane.height.value

      // Scale factor to fit image within the pane
      // val scale = Math.min(paneWidth / imageWidth, paneHeight / imageHeight)

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
