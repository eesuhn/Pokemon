package pokemon.model

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

abstract class Trainer {
  val name: String
  var deck: ArrayBuffer[Pokemon] = ArrayBuffer.empty[Pokemon]
  var activePokemon: Pokemon = _

  def chooseMove(): Move

  /**
    * Generates a deck of 3 random Pokemon
    */
  def generateDeck(): Unit = {
    val pokemons = PokemonRegistry.pokemons
      .map(pokemon => pokemon.getDeclaredConstructor().newInstance())
      .toList
    val randomPokemons = Random.shuffle(pokemons).take(3)

    addPokemons(randomPokemons)
  }

  /**
    * Hard limit of 3 Pokemons per Trainer
    *
    * @param pokemons
    */
  protected def addPokemons(pokemons: List[Pokemon]): Unit = {
    if (pokemons.size == 0) throw new Exception("Cannot add 0 Pokemon")
    if (this.deck.size + pokemons.size > 3) throw new Exception(s"$name can only have 3 Pokemon")
    this.deck ++= pokemons
    this.activePokemon = this.deck.head
  }

  def switchActivePokemon(pokemon: Pokemon): Unit = {
    if (!this.deck.contains(pokemon)) throw new Exception(s"$name does not have this Pokemon")
    this.activePokemon = pokemon
  }

  def hasActivePokemon: Boolean = activePokemon != null && activePokemon.currentHP > 0

  def switchToNextAlivePokemon(): Option[Pokemon] = {
    deck.find(_.currentHP > 0).map { pokemon =>
      switchActivePokemon(pokemon)
      pokemon
    }
  }

  def isDefeated: Boolean = deck.forall(_.currentHP == 0)
}

class Player extends Trainer {
  val name: String = "Player"
  private var _moveIndex: Int = -1

  def moveIndex(index: Int): Unit = this._moveIndex = index

  // DEBUG: Defined list of Pokemon
  override def generateDeck(): Unit = {
    val pokemons = List(
      new Squirtle(),
      new Bulbasaur()
    )
    addPokemons(pokemons)
  }

  override def chooseMove(): Move = activePokemon.moves(this._moveIndex)
}

class Bot extends Trainer {
  val name: String = "Bot"

  // DEBUG: Defined list of Pokemon
  override def generateDeck(): Unit = {
    val pokemons = List(
      new Pikachu(),
      new Charmander()
    )
    addPokemons(pokemons)
  }

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
