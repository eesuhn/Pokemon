package pokemon.model

import org.scalatest.funsuite.AnyFunSuite

class StatEffectTest extends AnyFunSuite {

  test("Stat: Growl against Charmander") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val charmanderAttack = charmander.attack.value
    val expectedCharmanderAttack = (charmanderAttack * (2.0 / 3.0)).toInt

    squirtle.statusAttack(Growl, charmander)

    assert(charmander.attack.value == expectedCharmanderAttack)
    assert(charmander.defense.value == 43)
  }

  test("Stat: Leer against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val squirtleDefense = squirtle.defense.value
    val expectedSquirtleDefense = (squirtleDefense * (2.0 / 3.0)).toInt

    charmander.statusAttack(Leer, squirtle)

    assert(squirtle.defense.value == expectedSquirtleDefense)
    assert(squirtle.attack.value == 48)
  }

  test("Stat: 5 Leer against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()
    val testCount = 5

    var expectedSquirtleDefense = squirtle.defense.value
    expectedSquirtleDefense = (expectedSquirtleDefense * (2.0 / (2.0 + testCount))).toInt

    for (_ <- 1 to testCount) {
      charmander.statusAttack(Leer, squirtle)
    }

    assert(squirtle.defense.value == expectedSquirtleDefense)
  }
}
