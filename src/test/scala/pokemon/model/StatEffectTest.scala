package pokemon.model

import org.scalatest.funsuite.AnyFunSuite

class StatEffectTest extends AnyFunSuite {

  /**
    * Test the effect of Growl on Charmander's attack
    */
  test("Stats: Growl against Charmander") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val charmanderAttack = charmander.attack.value
    val expectedCharmanderAttack = (charmanderAttack * (2.0 / 3.0)).toInt

    squirtle.statusAttack(Growl, charmander)

    assert(charmander.attack.value == expectedCharmanderAttack)
    assert(charmander.defense.value == 43)
  }

  /**
    * Test the effect of Leer on Squirtle's defense
    */
  test("Stats: Leer against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val squirtleDefense = squirtle.defense.value
    val expectedSquirtleDefense = (squirtleDefense * (2.0 / 3.0)).toInt

    charmander.statusAttack(Leer, squirtle)

    assert(squirtle.defense.value == expectedSquirtleDefense)
    assert(squirtle.attack.value == 48)
  }

  /**
    * Test 5 attacks of Leer on Squirtle
    */
  test("5 status attacks: Leer against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    var expectedSquirtleDefense = squirtle.defense.value
    expectedSquirtleDefense = (expectedSquirtleDefense * (2.0 / 7.0)).toInt

    for (_ <- 1 to 5) {
      charmander.statusAttack(Leer, squirtle)
    }

    assert(squirtle.defense.value == expectedSquirtleDefense)
  }
}
