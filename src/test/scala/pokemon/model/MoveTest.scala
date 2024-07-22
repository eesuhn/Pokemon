package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer

class MoveTest extends AnyFunSuite {

  test("Check all Move objects") {
    val moves = MoveRegistry.moves

    assert(moves.nonEmpty)

    val failedInitializations = ListBuffer.empty[(Move, Throwable)]

    moves.foreach { move =>
      try {
        assert(move != null)
      } catch {
        case e: Throwable =>
          failedInitializations += ((move, e))
      }
    }

    if (failedInitializations.nonEmpty) {
      val failureMessages = failedInitializations.map { case (move, exception) =>
        s"""
          |Failed to instantiate ${move.getClass.getSimpleName}:
          |  Exception: ${exception.getClass.getName}
          |""".stripMargin
      }.mkString

      fail(
        s"""
          |${failedInitializations.size} out of ${moves.size} Move objects failed to initialize:
          |$failureMessages
          |""".stripMargin
      )
    }
  }

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

    mewtwo.attack(PsychoCut, toxicroak)

    assert(toxicroak.currentHP == expectedHP)
  }

  test("X-Scissor against Toxicroak") {
    val scyther = new Scyther()
    val toxicroak = new Toxicroak()

    val damage = XScissor.calculatePhysicalDamage(scyther, toxicroak)
    val expectedHP = toxicroak.baseHP - damage.toInt

    scyther.attack(XScissor, toxicroak)

    assert(toxicroak.currentHP == expectedHP)
  }

  test ("Smokescreen against Pikachu") {
    val breloom = new Breloom()
    val pikachu = new Pikachu()
    val testCount = 5

    val calculatedPikachuAccuracy = (pikachu.accuracy.value * (2.0 / (2.0 + testCount))).toInt
    val expectedPikachuAccuracy = Math.max(calculatedPikachuAccuracy, 60)

    for (_ <- 1 to testCount) {
      breloom.attack(Smokescreen, pikachu)
    }

    assert(pikachu.accuracy.value == expectedPikachuAccuracy)
  }
}
