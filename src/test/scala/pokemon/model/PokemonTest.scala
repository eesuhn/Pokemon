package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer
import pokemon.MainApp

class PokemonTest extends AnyFunSuite {

  test("All Pokemon subclasses can be instantiated") {
    val pokemons = PokemonRegistry.pokemons
    assert(pokemons.nonEmpty)
    println(s"No. of Pokemon: ${pokemons.size}")

    // Restrict to only non-abstract classes
    // val instantiableClasses = pokemons.filter(c => !Modifier.isAbstract(c.getModifiers))

    val failedInitializations = ListBuffer.empty[(Class[_], Throwable)]
    pokemons.foreach { subclass =>
      try {
        val pokemon = subclass.getDeclaredConstructor().newInstance()
        assert(pokemon != null)
      } catch {
        case e: Throwable =>
          failedInitializations += ((subclass, e))
      }
    }

    if (failedInitializations.nonEmpty) {
      val failureMessages = failedInitializations.map { case (clazz, exception) =>
        s"""
          |Failed to instantiate ${clazz.getSimpleName}:
          |  Exception: ${exception.getClass.getName}
          |""".stripMargin
      }.mkString

      fail(
        s"""
          |${failedInitializations.size} out of ${pokemons.size} Pokemon subclasses failed to initialize:
          |$failureMessages
          |""".stripMargin
      )
    }
  }

  test("All Pokemon assets are present") {
    val pokemons = PokemonRegistry.pokemons
    assert(pokemons.nonEmpty)

    val failedAssetChecks = ListBuffer.empty[(Class[_], String)]
    pokemons.foreach { subclass =>
      try {
        val pokemon = subclass.getDeclaredConstructor().newInstance()
        assert(pokemon != null)

        val frontImagePath = s"pokes/${pokemon.pName}-front.gif"
        val backImagePath = s"pokes/${pokemon.pName}-back.gif"
        val staticImagePath = s"pokes-static/${pokemon.pName.toLowerCase}.png"

        List(frontImagePath, backImagePath, staticImagePath).foreach { path =>
          if (MainApp.getClass.getResourceAsStream(path) == null) {
            failedAssetChecks += ((subclass, path))
          }
        }
      } catch {
        case e: Throwable =>
          failedAssetChecks += ((subclass, s"Initialization failed: ${e.getMessage}"))
      }
    }

    if (failedAssetChecks.nonEmpty) {
      val failureMessages = failedAssetChecks.map { case (_, path) =>
        s"Missing asset: $path"
      }.mkString("\n")

      fail(
        s"""
          |${failedAssetChecks.size} out of ${pokemons.size * 3} Pokemon assets are missing:
          |$failureMessages
          |""".stripMargin
      )
    }
  }
}
