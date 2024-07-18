package pokemon.model

import org.scalatest.funsuite.AnyFunSuite

class PokemonTest extends AnyFunSuite {

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

  /**
    * Test 5 attacks of Growl on Charmander
    */
  test("5 status attacks: Growl against Charmander") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    var expectedCharmanderAttack = charmander.attack

    for (_ <- 1 to 5) {
      squirtle.statusAttack(Growl, charmander)
      expectedCharmanderAttack = (expectedCharmanderAttack * (2.0 / 3.0)).toInt
    }

    assert(charmander.attack == expectedCharmanderAttack)
  }
}
