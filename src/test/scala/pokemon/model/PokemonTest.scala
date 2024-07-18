package pokemon.model

import org.scalatest.funsuite.AnyFunSuite

class PokemonTest extends AnyFunSuite {

  /**
    * Test the creation of a Pokemon
    */
  test("Create a Pokemon") {
    val charmander = new Charmander()
    
    assert(charmander.pName == "Charmander")
    assert(charmander.maxHP == 39)
    assert(charmander.currentHP == 39)
    assert(charmander.attack == 52)
    assert(charmander.defense == 43)
    assert(charmander.level == 1)
  }

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

  /**
    * Test the effect of Growl on Charmander's attack
    */
  test("Growl against Charmander") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val charmanderAttack = charmander.attack
    val expectedCharmanderAttack = charmanderAttack * (2 / 3)

    squirtle.statusAttack(Growl, charmander)

    assert(charmander.attack == expectedCharmanderAttack)
    assert(charmander.defense == 43)
  }

  /**
    * Test the effect of Leer on Squirtle's defense
    */
  test("Leer against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val squirtleDefense = squirtle.defense
    val expectedSquirtleDefense = squirtleDefense * (2 / 3)

    charmander.statusAttack(Leer, squirtle)

    assert(squirtle.attack == 48)
    assert(squirtle.defense == expectedSquirtleDefense)
  }
}
