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
    assert(charmander.pTypes == List(Fire))
    assert(charmander.moves == List(Leer, Scratch, Ember))
  }

  /**
    * Test the modifier value for Scratch against Squirtle
    */
  test("Modifier: Scratch against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()
    
    val modifier = charmander.calculateModifier(Scratch, squirtle)
    
    assert(modifier == 1.0)
  }

  /**
    * Test the modifier value for Ember against Squirtle
    */
  test("Modifier: Ember against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()
    
    val modifier = charmander.calculateModifier(Ember, squirtle)
    
    assert(modifier == 0.5)
  }

  /**
    * Test the modifier value for Water Gun against Charmander
    */
  test("Modifier: Water Gun against Charmander") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()
    
    val modifier = squirtle.calculateModifier(WaterGun, charmander)
    
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
    assert(charmander.attack.isInstanceOf[Int])
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
    assert(squirtle.defense.isInstanceOf[Int])
    assert(squirtle.attack == 48)
  }

  /**
    * Test the effect of Scratch on Squirtle's HP
    */
  test("HP after attack: Scratch against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val modifier = charmander.calculateModifier(Scratch, squirtle)
    val damage = charmander.calculateDamage(
      Scratch.basePower,
      charmander.attack,
      squirtle.defense,
      charmander.level,
      modifier
    )
    val expectedHP = squirtle.maxHP - damage

    charmander.physicalAttack(Scratch, squirtle)

    assert(squirtle.currentHP == expectedHP)
  }

  /**
    * Test the effect of Ember on Squirtle's HP
    */
  test("HP after attack: Ember against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val modifier = charmander.calculateModifier(Ember, squirtle)
    val damage = charmander.calculateDamage(
      Ember.basePower,
      charmander.attack,
      squirtle.defense,
      charmander.level,
      modifier
    )
    val expectedHP = squirtle.maxHP - damage.toInt

    charmander.physicalAttack(Ember, squirtle)

    assert(squirtle.currentHP == expectedHP)
  }

  /**
    * Test 5 attacks of Scratch on Squirtle
    */
  test("HP after 5 attacks: Scratch against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val modifier = charmander.calculateModifier(Scratch, squirtle)
    val damage = charmander.calculateDamage(
      Scratch.basePower,
      charmander.attack,
      squirtle.defense,
      charmander.level,
      modifier
    )
    val expectedHP = squirtle.maxHP - (damage * 5).toInt

    for (_ <- 1 to 5) {
      charmander.physicalAttack(Scratch, squirtle)
    }

    assert(squirtle.currentHP == expectedHP)
  }

  /**
    * Test 5 attacks of Growl on Charmander
    */
  test("Stats after 5 attacks: Growl against Charmander") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    // for (_ <- 1 to 5) {
      squirtle.statusAttack(Growl, charmander)
    // }
    
    Console.withOut(System.out) {
      println(s"Charmander's attack: ${charmander.attack}")
    }
  }
}
