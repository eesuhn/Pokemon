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

  test("Psycho Cut against Toxicroak") {
    val mewtwo = new Mewtwo()
    val toxicroak = new Toxicroak()

    val damage = PsychoCut.calculatePhysicalDamage(mewtwo, toxicroak)
    val expectedHP = toxicroak.baseHP - damage.toInt

    val expectedDamage = (
      ((2 * mewtwo.level / 5 + 2)
      * mewtwo.attack.value
      * PsychoCut.basePower
      / toxicroak.defense.value
      / 50
      + 2
      ) * 4
    )

    Console.withOut(System.out) {
      println(s"\tDamage: $damage")
      println(s"\tExpected Damage: $expectedDamage")
    }

    mewtwo.attack(PsychoCut, toxicroak)

    assert(toxicroak.currentHP == expectedHP)
  }

  test("X-Scissor against Toxicroak") {
    val scyther = new Scyther()
    val toxicroak = new Toxicroak()

    val damage = XScissor.calculatePhysicalDamage(scyther, toxicroak)
    val expectedHP = toxicroak.baseHP - damage.toInt

    val expectedDamage = (
      ((2 * scyther.level / 5 + 2)
      * scyther.attack.value
      * XScissor.basePower
      / toxicroak.defense.value
      / 50
      + 2
      ) * 0.25
    )

    Console.withOut(System.out) {
      println(s"\tDamage: $damage")
      println(s"\tExpected Damage: $expectedDamage")
    }

    scyther.attack(XScissor, toxicroak)

    assert(toxicroak.currentHP == expectedHP)
  }
}
