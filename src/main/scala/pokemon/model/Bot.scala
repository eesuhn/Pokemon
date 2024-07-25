package pokemon.model

import scala.util.Random

class Bot extends Trainer {
  val name = "Bot"

  override def chooseMove(): (Int, Move) = {
    val moveIndex = Random.nextInt(activePokemon.moves.length)
    (moveIndex, activePokemon.moves(moveIndex))
  }
}
