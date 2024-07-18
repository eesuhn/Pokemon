package pokemon.model

import org.scalatest.funsuite.AnyFunSuite

class MoveTest extends AnyFunSuite {

  /**
    * Test the modifier value for Scratch against Squirtle
    */
  test("Modifier: Scratch against Squirtle") {
    val squirtle = new Squirtle()
    val modifier = Scratch.calculateModifier(squirtle)
    assert(modifier == 1.0)
  }

  /**
    * Test the modifier value for Ember against Squirtle
    */
  test("Modifier: Ember against Squirtle") {
    val squirtle = new Squirtle()
    val modifier = Ember.calculateModifier(squirtle)
    assert(modifier == 0.5)
  }

  /**
    * Test the modifier value for Water Gun against Charmander
    */
  test("Modifier: Water Gun against Charmander") {
    val charmander = new Charmander()
    val modifier = WaterGun.calculateModifier(charmander)
    assert(modifier == 2.0)
  }
}
