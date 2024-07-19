package pokemon.model

import org.scalatest.funsuite.AnyFunSuite

class StatEffectTest extends AnyFunSuite {

  /**
    * Test the effect of Growl on Charmander's attack
    */
  test("Stats: Growl against Charmander") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val charmanderAttack = charmander.attack
    val expectedCharmanderAttack = (charmanderAttack * (2.0 / 3.0)).toInt

    squirtle.statusAttack(Growl, charmander)

    assert(charmander.attack == expectedCharmanderAttack)
    assert(charmander.defense == 43)
  }

  /**
    * Test the effect of Leer on Squirtle's defense
    */
  test("Stats: Leer against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val squirtleDefense = squirtle.defense
    val expectedSquirtleDefense = (squirtleDefense * (2.0 / 3.0)).toInt

    charmander.statusAttack(Leer, squirtle)

    assert(squirtle.defense == expectedSquirtleDefense)
    assert(squirtle.attack == 48)
  }

  /**
    * Test 5 attacks of Leer on Squirtle
    */
  test("5 status attacks: Leer against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    var expectedSquirtleDefense = squirtle.defense

    for (_ <- 1 to 6) {
      expectedSquirtleDefense = (expectedSquirtleDefense * (2.0 / 3.0)).toInt
      charmander.statusAttack(Leer, squirtle)
    }

    assert(squirtle.defense == expectedSquirtleDefense)
  }
}
