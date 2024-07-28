package pokemon.view

import pokemon.util.ResourceUtil
import scalafx.application.Platform
import scalafx.scene.control.Label
import scalafx.scene.image.ImageView
import scalafx.scene.layout.AnchorPane

class GameView(
  val battleBg: ImageView,
  val battleDialogLeft: ImageView,
  val battleDialogRight: ImageView,
  val pokemonLeft: GamePokemonView,
  val pokemonRight: GamePokemonView,
  val stateDialogTxt: Label,
  val powerTxtLabel: Label,
  val powerTxt: Label,
  val accuracyTxtLabel: Label,
  val accuracyTxt: Label,
  val moveCat: ImageView,
  val moveTypeImg: ImageView,
  val moveTypeTxt: Label
) {

  def setup(): Unit = {
    battleBg.image = ResourceUtil.resouceImage("misc/battle-bg.gif")
    battleDialogLeft.image = ResourceUtil.resouceImage("misc/battle-dialog-left.png")
    battleDialogRight.image = ResourceUtil.resouceImage("misc/battle-dialog-right.png")
  }

  def pokemonViews(leftPokemon: String, rightPokemon: String): Unit = {
    pokemonLeft.setup(s"${leftPokemon}-back")
    pokemonRight.setup(s"${rightPokemon}-front")
  }

  def stateDialogTxt(text: String): Unit = stateDialogTxt.text = text

  private def powerTxt(text: String): Unit = {
    powerTxt.text = text
    if (!text.isEmpty()) powerTxtLabel.text = "Power" else powerTxtLabel.text = ""
  }

  private def accuracyTxt(text: String): Unit = {
    accuracyTxt.text = text
    if (!text.isEmpty()) accuracyTxtLabel.text = "Accuracy" else accuracyTxtLabel.text = ""
  }

  private def moveCat(category: String): Unit = {
    if (!category.isEmpty()) {
      moveCat.image = ResourceUtil.resouceImage(s"misc/${category}-move.png")
    } else {
      moveCat.image = null
    }
  }

  private def moveType(typeOfMove: String): Unit = {
    if (!typeOfMove.isEmpty()) {
      moveTypeImg.image = ResourceUtil.resouceImage(s"misc/${typeOfMove}-type.png")
      moveTypeTxt.text = typeOfMove.toUpperCase()
      moveTypeTextColor(typeOfMove)
    } else {
      moveTypeImg.image = null
      moveTypeTxt.text = ""
      moveTypeTextColor("")
    }
  }

  private def moveTypeTextColor(typeOfMove: String): Unit = {
    val color = typeOfMove match {
      case "normal" => "#7a6f63"
      case "fire" => "#a0292c"
      case "water" => "#02416a"
      case "electric" => "#b19007"
      case "grass" => "#095c28"
      case "ice" => "#2c7286"
      case "fighting" => "#9b3d30"
      case "poison" => "#753a61"
      case "psychic" => "#9c4267"
      case "bug" => "#6f7b13"
      case "rock" => "#72613a"
      case _ => "black"
    }
    moveTypeTxt.style = s"-fx-text-fill: ${color};"
  }

  def clearLeftDialogPane(): Unit = {
    stateDialogTxt.text = ""
  }

  def clearRightDialogPane(): Unit = {
    powerTxt("")
    accuracyTxt("")
    moveCat("")
    moveType("")
  }

  /**
    * Set move stats on the right dialog pane
    *
    * - Power, Accuracy, Category, Type of Move
    *
    * @param power
    * @param accuracy
    * @param category
    * @param typeOfMove
    */
  def moveStats(power: String, accuracy: String, category: String, typeOfMove: String): Unit = {
    powerTxt(power)
    accuracyTxt(accuracy)
    moveCat(category)
    moveType(typeOfMove)
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
