package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer
import pokemon.MainApp

class MoveTest extends AnyFunSuite {

  test("Access Move objects") {
    val moves = MoveRegistry.moves
    assert(moves.nonEmpty)
    println(s"No. of Move: ${moves.size}")

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

  test("Check Move SFX") {
    val moves = MoveRegistry.moves
    assert(moves.nonEmpty)

    val missingSFX = ListBuffer.empty[String]
    moves.foreach { move =>
      val sfxFileName = move.moveName.toLowerCase.replace(' ', '-') + ".mp3"
      val sfxPath = s"sfx/moves/$sfxFileName"

      if (MainApp.getClass.getResourceAsStream(sfxPath) == null) {
        missingSFX += sfxFileName
      }
    }

    if (missingSFX.nonEmpty) {
      val failureMessages = missingSFX.map { fileName =>
        s"Missing SFX: $fileName"
      }.mkString("\n")

      fail(
        s"""
          |${missingSFX.size} out of ${moves.size} Move SFX are missing:
          |$failureMessages
          |""".stripMargin
      )
    } else {
      succeed
    }
  }
}
