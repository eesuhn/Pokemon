package pokemon.model

import org.scalatest.funsuite.AnyFunSuite

class MoveTest extends AnyFunSuite {

  def printDamage(move: String, damage: Double): Unit = {
    Console.withOut(System.out) {
      println(s"\t$move: $damage")
    }
  }

  test("Damage: Ember against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val damage = Ember.calculatePhysicalDamage(charmander, squirtle)

    assert(damage == 2)
    printDamage("Ember", damage)
  }

  test("Damage: Vine Whip against Squirtle") {
    val bulbasaur = new Bulbasaur()
    val squirtle = new Squirtle()

    val damage = VineWhip.calculatePhysicalDamage(bulbasaur, squirtle)

    assert(damage == 8)
    printDamage("Vine Whip", damage)
  }

  test("Damage: Tackle against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val damage = Tackle.calculatePhysicalDamage(charmander, squirtle)

    assert(damage == 4)
    printDamage("Tackle", damage)
  }
}
