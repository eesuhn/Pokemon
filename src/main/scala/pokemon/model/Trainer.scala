package pokemon.model

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

abstract class Trainer {
  val name: String
  var deck: ArrayBuffer[Pokemon] = ArrayBuffer.empty[Pokemon]
  var activePokemon: Pokemon = _

  private val _deckSize: Int = 5

  def generateDeck(): Unit = {
    val pokemons = PokemonRegistry.pokemons
      .map(pokemon => pokemon.getDeclaredConstructor().newInstance())
      .toList
    val weightedPokemons = weightPokemonsByRarity(pokemons)
    val deckSize = Math.min(_deckSize, weightedPokemons.length)
    val selectedPokemons = selectPokemonsWeighted(weightedPokemons, deckSize)
    addPokemons(selectedPokemons)
  }


  /**
    * Map Pokemon to their rarity Gacha chance
    *
    * @param pokemons
    * @return
    */
  private def weightPokemonsByRarity(pokemons: List[Pokemon]): List[(Pokemon, Double)] = {
    val rarities = RarityRegistry.rarities
    val totalGachaChance = rarities.map(_.gachaChance).sum
    pokemons.map(pokemon => (pokemon, pokemon.rarity.gachaChance / totalGachaChance))
  }

  /**
    * Selects a list of Pokemon based on Gacha chance
    *
    * @param weightedPokemons
    * @param count
    * @return
    */
  private def selectPokemonsWeighted(
    weightedPokemons: List[(Pokemon, Double)],
    count: Int
  ): List[Pokemon] = {

    val selected = ArrayBuffer.empty[Pokemon]
    var remainingPokemons = weightedPokemons
    val random = new Random()

    while (selected.size < count && remainingPokemons.nonEmpty) {
      val totalWeight = remainingPokemons.map(_._2).sum
      val randomValue = random.nextDouble() * totalWeight
      var accumulatedWeight = 0.0

      val (selectedPokemon, selectedIndex) = remainingPokemons
        .zipWithIndex
        .find { case ((_, weight), _) =>
          accumulatedWeight += weight
          randomValue < accumulatedWeight
        }
        .map { case ((pokemon, _), index) => (pokemon, index) }
        .getOrElse((null, -1))

      if (selectedPokemon != null) {
        selected += selectedPokemon
        remainingPokemons = remainingPokemons.patch(selectedIndex, Nil, 1)
      }
    }
    selected.toList
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
    * Switches the active Pokemon to the next best Pokemon
    *
    * - Based on move efficiency
    *
    * @param playerActivePokemon
    * @return
    */
  def switchToNextPokemon(playerActivePokemon: Pokemon): Option[Pokemon] = {
    val alivePokemons = deck.filter(_.health.value > 0)

    if (!alivePokemons.isEmpty) {
      val bestPokemon = alivePokemons.maxBy(_.moveEffMap(playerActivePokemon).values.sum)
      switchActivePokemon(bestPokemon)
      return Some(bestPokemon)
    }
    None
  }

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
