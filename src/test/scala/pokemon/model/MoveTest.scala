package pokemon.model

import org.scalatest.funsuite.AnyFunSuite

class MoveTest extends AnyFunSuite {

  test("Ember against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val damage = Ember.calculatePhysicalDamage(charmander, squirtle)
    val expectedHP = squirtle.baseHP - damage.toInt
    val expectedCharmanderAttack = charmander.attack.value
    val expectedCharmanderDefense = charmander.defense.value

    charmander.attack(Ember, squirtle)

    assert(squirtle.currentHP == expectedHP)
    assert(charmander.attack.value == expectedCharmanderAttack)
    assert(charmander.defense.value == expectedCharmanderDefense)
  }

  test("Rock Tomb against Charmander") {
    val geodude = new Geodude()
    val charmander = new Charmander()
    val testCount = 1

    val expectedCharmanderSpeed = (charmander.speed.value * (2.0 / (2.0 + testCount))).toInt

    val damage = RockTomb.calculatePhysicalDamage(geodude, charmander)
    val expectedHP = charmander.baseHP - damage.toInt

    geodude.attack(RockTomb, charmander)

    assert(charmander.currentHP == expectedHP)
    assert(charmander.speed.value == expectedCharmanderSpeed)
  }
}
