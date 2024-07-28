package pokemon.view

import pokemon.util.ResourceUtil
import scalafx.application.Platform
import scalafx.scene.control.{Label, ProgressBar}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.AnchorPane

class GameView(
  // background
  val battleBg: ImageView,
  val battleDialogLeft: ImageView,
  val battleDialogRight: ImageView,

  // pokemon
  val pokemonLeft: GamePokemonView,
  val pokemonRight: GamePokemonView,

  // left dialog
  val stateDialogTxt: Label,

  // right dialog
  val moveTypeImg: ImageView,
  val moveTypeTxt: Label,
  val moveCat: ImageView,
  val powerTxtLabel: Label,
  val powerTxt: Label,
  val accuracyTxtLabel: Label,
  val accuracyTxt: Label
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

  def pokemonHpBars(leftHp: Double, rightHp: Double): Unit = {
    pokemonLeft.hpBar.progress = leftHp
    pokemonLeft.hpBar.style = s"-fx-accent: ${hpBarColor(leftHp)}"
    pokemonRight.hpBar.progress = rightHp
    pokemonRight.hpBar.style = s"-fx-accent: ${hpBarColor(rightHp)}"
  }

  def setStateDialog(text: String): Unit = stateDialogTxt.text = text

  def updateMoveStats(power: String, accuracy: String, category: String, typeOfMove: String): Unit = {
    powerTxt(power)
    accuracyTxt(accuracy)
    setMoveCatImg(category)
    setMoveType(typeOfMove)
  }

  def clearLeftDialogPane(): Unit = stateDialogTxt.text = ""

  def clearRightDialogPane(): Unit = updateMoveStats("", "", "", "")

  private def hpBarColor(hp: Double): String = {
    if (hp > 0.7) "#3cda38"
    else if (hp > 0.3) "#f4b848"
    else "#de6248"
  }

  private def powerTxt(text: String): Unit = {
    powerTxt.text = text
    powerTxtLabel.text = if (text.nonEmpty) "Power" else ""
  }

  private def accuracyTxt(text: String): Unit = {
    accuracyTxt.text = text
    accuracyTxtLabel.text = if (text.nonEmpty) "Accuracy" else ""
  }

  /**
    * Set move category image
    *
    * @param category
    */
  private def setMoveCatImg(category: String): Unit = {
    moveCat.image = if (category.nonEmpty) ResourceUtil.resouceImage(s"misc/${category}-move.png") else null
  }

  /**
    * Set move type image and text
    *
    * @param typeOfMove
    */
  private def setMoveType(typeOfMove: String): Unit = {
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
}

case class GamePokemonView(
  pokemonImg: ImageView,
  anchorPane: AnchorPane,
  hpBar: ProgressBar
) {

  /**
    * Source from `pokes` directory with `.gif` extension
    *
    * @param imagePath
    */
  def setup(imagePath: String): Unit = {
    val image = ResourceUtil.resouceImage(s"pokes/${imagePath}.gif")
    pokemonImg.image = image
    pokemonImg.preserveRatio = true
    pokemonImg.smooth = true

    Platform.runLater(positionPokemon())

    pokemonImg.image.onChange { (_, _, newImage) =>
      if (newImage != null) positionPokemon()
    }
  }

  /**
    * Position Pokemon image within the AnchorPane
    *
    * - Center horizontally
    * - Anchor to bottom
    */
  private def positionPokemon(): Unit = {
    Option(pokemonImg.image.value).foreach { newImage =>
      val imageWidth = newImage.getWidth()
      val imageHeight = newImage.getHeight()
      val paneWidth = anchorPane.width.value

      pokemonImg.fitWidth = imageWidth
      pokemonImg.fitHeight = imageHeight

      val leftAnchor = (paneWidth - pokemonImg.fitWidth.value) / 2
      AnchorPane.setLeftAnchor(pokemonImg, leftAnchor)

      positionHpBar()
    }
  }

  def positionHpBar(): Unit = {
    Option(pokemonImg.image.value).foreach { _ =>
      // Anchor based on Pokemon image height
      // val imageHeight = pokemonImg.fitHeight.value
      // val paneHeight = anchorPane.height.value

      // val topAnchor = paneHeight - imageHeight - hpBar.getHeight - 30
      // AnchorPane.setTopAnchor(hpBar, topAnchor)

      val leftAnchor = (anchorPane.width.value - hpBar.getWidth) / 2
      AnchorPane.setLeftAnchor(hpBar, leftAnchor)
    }
  }
}
