package pokemon.model

import org.scalatest.funsuite.AnyFunSuite

class PokemonTest extends AnyFunSuite {

  /**
    * Test the modifier value for Scratch against Squirtle
    */
  test("Scratch against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()
    val modifier = charmander.calculateModifier(Scratch, squirtle)
    assert(modifier == 1.0)
  }

  /**
    * Test the modifier value for Ember against Squirtle
    */
  test("Ember against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()
    val modifier = charmander.calculateModifier(Ember, squirtle)
    assert(modifier == 0.5)
  }

  /**
    * Test the modifier value for Water Gun against Charmander
    */
  test("Water Gun against Charmander") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()
    val modifier = squirtle.calculateModifier(WaterGun, charmander)
    assert(modifier == 2.0)
  }
}
