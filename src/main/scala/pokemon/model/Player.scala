package pokemon.model

import scala.collection.mutable.ArrayBuffer

class Player(
  val playerName: String
  ) {
  var deck: ArrayBuffer[Pokemon] = ArrayBuffer.empty[Pokemon]
  var activePokemon: Pokemon = _

  /**
    * Generate a deck of Pokemon
    */
  def generateDeck(): Unit = {
    val charmander = new Charmander
    val squirtle = new Squirtle
    val bulbasaur = new Bulbasaur

    addPokemon(charmander)
    addPokemon(squirtle)
    addPokemon(bulbasaur)
  }

  /**
    * Add Pokemon to deck
    *
    * @param pokemon
    */
  def addPokemon(pokemon: Pokemon): Unit = {
    if (this.deck.size > 3) {
      throw new Exception("Player can only have 3 Pokemon")
    }
    this.deck += pokemon
    if (this.deck.size == 1) this.activePokemon = pokemon
  }

  /**
    * Switch active Pokemon
    *
    * @param pokemon
    */
  def switchActivePokemon(pokemon: Pokemon): Unit = {
    if (!this.deck.contains(pokemon)) {
      throw new Exception("Player does not have this Pokemon")
    }
    this.activePokemon = pokemon
  }
}
