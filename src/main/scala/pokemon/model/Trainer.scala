package pokemon.model

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

abstract class Trainer {
  val name: String
  var deck: ArrayBuffer[Pokemon] = ArrayBuffer.empty[Pokemon]
  var activePokemon: Pokemon = _

  def generateDeck(): Unit = {
    val pokemons = PokemonRegistry.pokemons
      .map(pokemon => pokemon.getDeclaredConstructor().newInstance())
      .toList
    val randomPokemons = Random.shuffle(pokemons).take(5)

    addPokemons(randomPokemons)
  }

  /**
    * Adds a list of Pokemon to the deck, not more than 5
    *
    * @param pokemons
    *
    * @throws Exception if `pokemon == 0` || `deck.size + pokemons.size > 5`
    */
  protected def addPokemons(pokemons: List[Pokemon]): Unit = {
    if (pokemons.size == 0) throw new Exception("Cannot add 0 Pokemon")
    if (deck.size + pokemons.size > 5) throw new Exception(s"$name can only have 5 Pokemon")
    deck ++= pokemons
    activePokemon = deck.head
  }

  /**
    * Switches the active Pokemon to the given Pokemon
    *
    * @param pokemon
    *
    * @throws Exception if `deck` does not contain the given Pokemon
    */
  def switchActivePokemon(pokemon: Pokemon): Unit = {
    if (!deck.contains(pokemon)) throw new Exception(s"$name does not have this Pokemon")
    activePokemon = pokemon
  }

  def switchToNextAlivePokemon(): Option[Pokemon] = {
    deck.find(_.health.value > 0).map { pokemon =>
      switchActivePokemon(pokemon)
      pokemon
    }
  }

  def isActivePokemonAlive: Boolean = activePokemon != null && activePokemon.health.value > 0

  def moreThanOnePokemonAlive: Boolean = deck.count(_.health.value > 0) > 1

  def isDefeated: Boolean = deck.forall(_.health.value == 0)

  def alivePokemons: List[Pokemon] = deck.filter(_.health.value > 0).toList
}

class Player extends Trainer {
  val name: String = "Player"

  // DEBUG: Defined list of Pokemon
  // override def generateDeck(): Unit = {
  //   val pokemons = List(
  //     new Pikachu()
  //   )
  //   addPokemons(pokemons)
  // }
}

class Bot extends Trainer {
  val name: String = "Bot"

  // DEBUG: Defined list of Pokemon
  // override def generateDeck(): Unit = {
  //   val pokemons = List(
  //     new Pikachu()
  //   )
  //   addPokemons(pokemons)
  // }

  def chooseMove(target: Pokemon): Move = weightedRandomMove(target)

  /**
    * Randomly selects a move from the active Pokemon's move list
    *
    * @return
    */
  private def randomMove(): Move = {
    val moveIndex = Random.nextInt(activePokemon.moves.length)
    activePokemon.moves(moveIndex)
  }

  /**
    * Selects move based on:
    *
    * - Move efficiency
    * - Move's type advantage (modifier)
    *
    * @param target
    * @return
    */
  private def weightedRandomMove(target: Pokemon): Move = {
    val moveEffMap = this.activePokemon.moveEffMap(target)
    val totalWeight = moveEffMap.values.sum
    val randomValue = Random.nextDouble() * totalWeight

    // DEBUG: Print move weights
    // moveEffMap.foreach { case (move, weight) =>
    //   val msg = f"""
    //     |${move.moveName}%-20s$weight
    //     |""".stripMargin
    //   println(msg)
    // }
    // println("# # # # # #")

    var accumulatedWeight = 0.0
    for ((move, weight) <- moveEffMap) {
      accumulatedWeight += weight
      if (accumulatedWeight >= randomValue) {
        return move
      }
    }

    // Fallback to random move if something goes wrong
    randomMove()
  }
}
