package pokemon.model

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

abstract class Trainer {
  val name: String
  var deck: ArrayBuffer[Pokemon] = ArrayBuffer.empty[Pokemon]
  var activePokemon: Pokemon = _

  def chooseMove(): Move

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
    deck.find(_.currentHP > 0).map { pokemon =>
      switchActivePokemon(pokemon)
      pokemon
    }
  }

  def hasActivePokemon: Boolean = activePokemon != null && activePokemon.currentHP > 0

  def isDefeated: Boolean = deck.forall(_.currentHP == 0)
}

class Player extends Trainer {
  val name: String = "Player"
  private var _moveIndex: Int = -1

  def moveIndex(index: Int): Unit = _moveIndex = index

  // DEBUG: Defined list of Pokemon
  // override def generateDeck(): Unit = {
  //   val pokemons = List(
  //     new Snorlax(),
  //     new Blaziken(),
  //     new Breloom(),
  //     new Squirtle
  //   )
  //   addPokemons(pokemons)
  // }

  override def chooseMove(): Move = activePokemon.moves(_moveIndex)
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

  override def chooseMove(): Move = {
    randomMove()
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
}
