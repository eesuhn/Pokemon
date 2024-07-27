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

  def setupGameView(): Unit = {
    battleBg.image = ResourceUtil.resouceImage("misc/battle-bg.gif")
    battleDialogLeft.image = ResourceUtil.resouceImage("misc/battle-dialog-left.png")
    battleDialogRight.image = ResourceUtil.resouceImage("misc/battle-dialog-right.png")
  }

  def updatePokemonViews(leftPokemon: String, rightPokemon: String): Unit = {
    pokemonLeft.setup(s"${leftPokemon}-back")
    pokemonRight.setup(s"${rightPokemon}-front")
  }

  def updateStateDialogTxt(text: String): Unit = stateDialogTxt.text = text

  def updatePowerTxt(text: String): Unit = {
    powerTxt.text = text
    powerTxtLabel.text = "Power"
  }

  def updateAccuracyTxt(text: String): Unit = {
    accuracyTxt.text = text
    accuracyTxtLabel.text = "Accuracy"
  }

  def updateMoveCat(category: String): Unit = moveCat.image = ResourceUtil.resouceImage(s"misc/${category}-move.png")

  def updateMoveType(typeOfMove: String): Unit = {
    moveTypeImg.image = ResourceUtil.resouceImage(s"misc/${typeOfMove}-type.png")
    moveTypeTxt.text = typeOfMove.toUpperCase()
    setMoveTypeTextColor(typeOfMove)
  }

  private def setMoveTypeTextColor(typeOfMove: String): Unit = {
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

  /**
    * Clear the left dialog pane
    * 
    * - Dialog text
    */
  def clearLeftDialogPane(): Unit = {
    stateDialogTxt.text = ""
  }

  /**
    * Clear the right dialog pane
    * 
    * - Move category image
    * - Move type image
    * - Move type text
    * - Move type text color
    * - Power text
    * - Accuracy text
    */
  def clearRightDialogPane(): Unit = {
    powerTxt.text = ""
    powerTxtLabel.text = ""
    accuracyTxt.text = ""
    accuracyTxtLabel.text = ""
    moveCat.image = null
    moveTypeImg.image = null
    moveTypeTxt.text = ""
    moveTypeTxt.style = s"-fx-text-fill: black;"
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
