package pokemon.model

import org.scalatest.funsuite.AnyFunSuite

class PokemonTest extends AnyFunSuite {

  test("Initialize Bulbasaur") {
    val bulbasaur = new Bulbasaur()

    assert(bulbasaur.pName == "Bulbasaur")
    assert(bulbasaur.attack.value == 49)
    assert(bulbasaur.defense.value == 49)
    assert(bulbasaur.speed.value == 45)
    assert(bulbasaur.baseHP == 45)
    assert(bulbasaur.currentHP == 45)
    assert(bulbasaur.pTypes == List(Grass, Poison))
    assert(bulbasaur.moves == List(Growl, Tackle, VineWhip))
  }
}
