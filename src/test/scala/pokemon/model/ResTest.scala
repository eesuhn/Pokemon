package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer
import pokemon.MainApp

class ResTest extends AnyFunSuite {

  test("Check if all Move SFX are present") {
    val moves = MoveRegistry.moves
    assert(moves.nonEmpty)
    println(s"${Colors.PURPLE}moves.size: ${moves.size}${Colors.NC}")

    val failedAcc = ListBuffer.empty[(Move, Throwable)]
    val missingSFX = ListBuffer.empty[String]

    moves.foreach { move =>
      try {
        assert(move != null)

        val fileName = move.moveName.toLowerCase.replace(' ', '-') + ".mp3"
        val path = s"sfx/moves/$fileName"
        if (MainApp.getClass.getResourceAsStream(path) == null) {
          missingSFX += fileName
        }
      } catch {
        case e: Throwable =>
          failedAcc += ((move, e))
      }
    }

    if (failedAcc.nonEmpty) {
      val msg = failedAcc.map { case (move, e) =>
        s"""
          |Failed to access ${move.getClass.getSimpleName}:
          |  Exception: ${e.getClass.getName}""".stripMargin
      }.mkString

      fail(
        s"""
          |${failedAcc.size} out of ${moves.size} Move objects failed to access:
          |$msg""".stripMargin
      )
    }

    if (missingSFX.nonEmpty) {
      val msg = missingSFX.map { fileName =>
        s"Missing SFX: $fileName"
      }.mkString("\n")

      fail(
        s"""
          |${missingSFX.size} out of ${moves.size} Move SFX are missing:
          |$msg""".stripMargin
      )
    } else {
      println(s"${Colors.GREEN}All Move SFX are present.${Colors.NC}")
    }
  }

  test("Check if all Pokemon assets are present") {
    val pokemons = PokemonRegistry.pokemons
    assert(pokemons.nonEmpty)
    println(s"${Colors.PURPLE}pokemons.size: ${pokemons.size}${Colors.NC}")

    val failedInit = ListBuffer.empty[(Class[_], Throwable)]
    val missingAsset = ListBuffer.empty[String]

    pokemons.foreach { subclass =>
      try {
        val pokemon = subclass.getDeclaredConstructor().newInstance()
        assert(pokemon != null)

        val frontPath = s"pokes/${pokemon.pName}-front.gif"
        val backPath = s"pokes/${pokemon.pName}-back.gif"
        val staticPath = s"pokes-static/${pokemon.pName.toLowerCase}.png"

        List(frontPath, backPath, staticPath).foreach { path =>
          if (MainApp.getClass.getResourceAsStream(path) == null) {
            missingAsset += (path)
          }
        }
      } catch {
        case e: Throwable =>
          failedInit += ((subclass, e))
      }
    }

    if (failedInit.nonEmpty) {
      val msg = failedInit.map { case (clazz, e) =>
        s"""
          |Failed to instantiate ${clazz.getSimpleName}:
          |  Exception: ${e.getClass.getName}""".stripMargin
      }.mkString

      fail(
        s"""
          |${failedInit.size} out of ${pokemons.size} Pokemon subclasses failed to initialize:
          |$msg""".stripMargin
      )
    }

    if (missingAsset.nonEmpty) {
      val msg = missingAsset.map { path =>
        s"Missing asset: $path"
      }.mkString("\n")

      fail(
        s"""
          |${missingAsset.size} out of ${pokemons.size * 3} Pokemon assets are missing:
          |$msg""".stripMargin
      )
    } else {
      println(s"${Colors.GREEN}All Pokemon assets are present.${Colors.NC}")
    }
  }
}
