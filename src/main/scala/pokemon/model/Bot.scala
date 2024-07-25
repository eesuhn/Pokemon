package pokemon.model

import scala.util.Random

class Bot extends Trainer {
  val name: String = "Bot"

  override def chooseMove(): Move = {
    val moveIndex = Random.nextInt(activePokemon.moves.length)
    activePokemon.moves(moveIndex)
  }
}
