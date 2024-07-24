package pokemon.view

import scalafx.scene.image.ImageView
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

  val gameView = new GameView(battleBg, battleDialogOne, battleDialogTwo)
  val pokemonLeftView = new GamePokemonView(pokemonLeft, pokemonLeftPane)
  val pokemonRightView = new GamePokemonView(pokemonRight, pokemonRightPane)

  gameView.setup()
  pokemonLeftView.setup("pokes/Mewtwo-back.gif")
  pokemonRightView.setup("pokes/Snorlax-front.gif")

  def handleDialogBtn1(): Unit = DialogController.handleDialogBtn1()
  def handleDialogBtn2(): Unit = DialogController.handleDialogBtn2()
  def handleDialogBtn3(): Unit = DialogController.handleDialogBtn3()
  def handleDialogBtn4(): Unit = DialogController.handleDialogBtn4()
}
