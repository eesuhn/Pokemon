package pokemon.model

import org.scalatest.funsuite.AnyFunSuite

class PokemonTest extends AnyFunSuite {

  /**
    * Test the effect of Scratch on Squirtle's HP
    */
  test("Attack: Scratch against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val damage = Scratch.calculatePhysicalDamage(charmander, squirtle)
    val expectedHP = squirtle.maxHP - damage.toInt

    charmander.physicalAttack(Scratch, squirtle)

    assert(squirtle.currentHP == expectedHP)
  }

  /**
    * Test the effect of Ember on Squirtle's HP
    */
  test("Attack: Ember against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val damage = Ember.calculatePhysicalDamage(charmander, squirtle)
    val expectedHP = squirtle.maxHP - damage.toInt

    charmander.physicalAttack(Ember, squirtle)

    assert(squirtle.currentHP == expectedHP)
  }

  /**
    * Test 5 attacks of Scratch on Squirtle
    */
  test("5 physical attacks: Scratch against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val damage = Scratch.calculatePhysicalDamage(charmander, squirtle)
    val expectedHP = squirtle.maxHP - (damage * 5).toInt

    for (_ <- 1 to 5) {
      charmander.physicalAttack(Scratch, squirtle)
    }

    assert(squirtle.currentHP == expectedHP)
  }
}
