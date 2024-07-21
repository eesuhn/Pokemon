package pokemon.model

import org.scalatest.funsuite.AnyFunSuite

class PokemonTest extends AnyFunSuite {

  test("Initialize all Pokemon subclasses") {
    val pokemonSubclasses = Pokedex.getSubclasses

    assert(pokemonSubclasses.length != 0)

    pokemonSubclasses.foreach { subclass =>
      val pokemon = subclass.getDeclaredConstructor().newInstance()
      assert(pokemon != null)
    }
  }
}
