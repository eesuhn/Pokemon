package pokemon.model

import org.scalatest.funsuite.AnyFunSuite

class MoveTest extends AnyFunSuite {

  test("Damage: Ember against Squirtle") {
    val charmander = new Charmander()
    val squirtle = new Squirtle()

    val damage = Ember.calculatePhysicalDamage(charmander, squirtle)

    Console.withOut(System.out) {
      println(s"Ember damage: $damage")
    }
  }
}
