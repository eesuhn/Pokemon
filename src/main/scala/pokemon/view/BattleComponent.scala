package pokemon.view

import pokemon.util.ResourceUtil
import scalafx.application.Platform
import scalafx.scene.control.{Label, ProgressBar}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.AnchorPane

class BattleComponent(
  // background
  val background: BackgroundView,

  // pokemon
  val pokemonLeft: PokemonView,
  val pokemonRight: PokemonView,

  // left dialog
  // state
  val stateDialogTxt: Label,

  // right dialog
  // move stats
  val moveTypeImg: ImageView,
  val moveTypeTxt: Label,
  val moveCat: ImageView,
  val powerTxtLabel: Label,
  val powerTxt: Label,
  val accuracyTxtLabel: Label,
  val accuracyTxt: Label,

  // pokemon current stats
  val pokemonCurrentStats: PokemonStatsView
) {

  def pokemonViews(leftPokemon: String, rightPokemon: String): Unit = {
    pokemonLeft.setup(s"${leftPokemon}-back")
    pokemonRight.setup(s"${rightPokemon}-front")
  }

  def pokemonHpBars(leftHp: Double, rightHp: Double): Unit = {
    pokemonLeft.pokemonHpBar(leftHp)
    pokemonRight.pokemonHpBar(rightHp)
  }

  def setStateDialog(text: String): Unit = stateDialogTxt.text = text

  def updateMoveStats(power: String, accuracy: String, category: String, typeOfMove: String): Unit = {
    powerTxt(power)
    accuracyTxt(accuracy)
    setMoveCatImg(category)
    setMoveType(typeOfMove)
  }

  def clearLeftDialogPane(): Unit = stateDialogTxt.text = ""

  def clearRightDialogPane(): Unit = {
    updateMoveStats("", "", "", "")
    updatePokemonCurrentStats("", "", "", "", "", "")
  }

  def updatePokemonCurrentStats(
    pokemonName: String,
    type1: String,
    type2: String,
    hp: String,
    attack: String,
    defense: String
  ): Unit = {

    pokemonCurrentStats.updatePokemonCurrentStats(
      pokemonName,
      type1,
      type2,
      hp,
      attack,
      defense
    )
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

case class PokemonView(
  pokemonName: Label,
  pokemonImg: ImageView,
  anchorPane: AnchorPane,
  hpBar: ProgressBar
) {

  /**
    * Source from `pokes` directory with `.gif` extension
    *
    * @param target
    */
  def setup(target: String): Unit = {
    val pokemonNameText = target.split("-").head
    pokemonName.text = pokemonNameText.capitalize

    val image = ResourceUtil.resouceImage(s"pokes/${target}.gif")
    pokemonImg.image = image
    pokemonImg.preserveRatio = true
    pokemonImg.smooth = true

    Platform.runLater(position())

    pokemonImg.image.onChange { (_, _, newImage) =>
      if (newImage != null) position()
    }
  }

  /**
    * Position Pokemon image within the AnchorPane
    *
    * - Center horizontally
    * - Anchor to bottom
    */
  private def position(): Unit = {
    Option(pokemonImg.image.value).foreach { newImage =>
      val imageWidth = newImage.getWidth()
      val imageHeight = newImage.getHeight()
      val paneWidth = anchorPane.width.value

      pokemonImg.fitWidth = imageWidth
      pokemonImg.fitHeight = imageHeight

      val leftAnchor = (paneWidth - pokemonImg.fitWidth.value) / 2
      AnchorPane.setLeftAnchor(pokemonImg, leftAnchor)
    }
  }

  def pokemonHpBar(hp: Double): Unit = {
    hpBar.progress = hp
    hpBar.style = s"-fx-accent: ${hpBarColor(hp)}"
  }

  private def hpBarColor(hp: Double): String = {
    if (hp > 0.7) "#3cda38"
    else if (hp > 0.3) "#f4b848"
    else "#de6248"
  }
}

case class BackgroundView(
  battleBg: ImageView,
  battleDialogLeft: ImageView,
  battleDialogRight: ImageView,
  pokemonLeftStatBg: ImageView,
  pokemonRightStatBg: ImageView
) {

  private def initialize(): Unit = {
    battleBg.image = ResourceUtil.resouceImage("misc/battle-bg.gif")
    battleDialogLeft.image = ResourceUtil.resouceImage("misc/battle-dialog-left.png")
    battleDialogRight.image = ResourceUtil.resouceImage("misc/battle-dialog-right.png")
    pokemonLeftStatBg.image = ResourceUtil.resouceImage("misc/stat-bg-left.png")
    pokemonRightStatBg.image = ResourceUtil.resouceImage("misc/stat-bg-right.png")
  }

  initialize()
}

case class PokemonStatsView(
  currentImg: ImageView,
  currentTypeImg1: ImageView,
  currentTypeImg2: ImageView,
  currentHpTxt: Label,
  currentHp: Label,
  currentAttackTxt: Label,
  currentAttack: Label,
  currentDefenseTxt: Label,
  currentDefense: Label
) {

  def updatePokemonCurrentStats(
    pokemonName: String,
    type1: String,
    type2: String,
    hp: String,
    attack: String,
    defense: String
  ): Unit = {
    currentImg.image = if (pokemonName.nonEmpty) ResourceUtil.resouceImage(s"pokes-static/${pokemonName}.png") else null
    currentTypeImg1.image = if (type1.nonEmpty) ResourceUtil.resouceImage(s"misc/${type1}-type.png") else null
    currentTypeImg2.image = if (type2.nonEmpty) ResourceUtil.resouceImage(s"misc/${type2}-type.png") else null

    hpTxt(hp)
    attackTxt(attack)
    defenseTxt(defense)
  }

  private def hpTxt(text: String): Unit = {
    currentHp.text = text
    currentHpTxt.text = if (text.nonEmpty) "HP" else ""
  }

  private def attackTxt(text: String): Unit = {
    currentAttack.text = text
    currentAttackTxt.text = if (text.nonEmpty) "Atk." else ""
  }

  private def defenseTxt(text: String): Unit = {
    currentDefense.text = text
    currentDefenseTxt.text = if (text.nonEmpty) "Def." else ""
  }
}
