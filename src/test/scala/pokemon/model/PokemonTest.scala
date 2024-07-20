package pokemon.model

import org.scalatest.funsuite.AnyFunSuite

class PokemonTest extends AnyFunSuite {

  test("Attack: Scratch against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val damage = Scratch.calculatePhysicalDamage(charmander, squirtle)
    val expectedHP = squirtle.baseHP - damage.toInt

    charmander.physicalAttack(Scratch, squirtle)

    assert(squirtle.currentHP == expectedHP)
  }

  test("Attack: Ember against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val damage = Ember.calculatePhysicalDamage(charmander, squirtle)
    val expectedHP = squirtle.baseHP - damage.toInt

    charmander.physicalAttack(Ember, squirtle)

    assert(squirtle.currentHP == expectedHP)
  }

  test("Attack: 5 Scratch against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val damage = Scratch.calculatePhysicalDamage(charmander, squirtle)
    val expectedHP = Math.max(squirtle.baseHP - (damage * 5).toInt, 0)

    for (_ <- 1 to 5) {
      charmander.physicalAttack(Scratch, squirtle)
    }

    assert(squirtle.currentHP == expectedHP)
  }
}
