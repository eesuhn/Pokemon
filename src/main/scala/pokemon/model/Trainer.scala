package pokemon.model

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

abstract class Trainer {
  val name: String
  var deck: ArrayBuffer[Pokemon] = ArrayBuffer.empty[Pokemon]
  var activePokemon: Pokemon = _

  def chooseMove(): Move

  def generateDeck(): Unit = {
    val allPokemons = PokemonRegistry.pokemons
    val rand = new Random()

    for (_ <- 1 to 3) {
      val randomPokemonClass = allPokemons(rand.nextInt(allPokemons.length))
      val pokemon = randomPokemonClass.getDeclaredConstructor().newInstance()
      addPokemon(pokemon)
    }
  }

  def addPokemon(pokemon: Pokemon): Unit = {
    if (this.deck.size >= 3) throw new Exception(s"$name can only have 3 Pokemon")
    this.deck += pokemon
    if (this.deck.size == 1) this.activePokemon = pokemon
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
