package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer

class PokemonTest extends AnyFunSuite {

  test("Initialize all Pokemon subclasses") {
    val pokemons = PokemonRegistry.pokemons

    assert(pokemons.nonEmpty)

    // Restrict to only non-abstract classes
    // val instantiableClasses = pokemons.filter(c => !Modifier.isAbstract(c.getModifiers))

    val failedInitializations = ListBuffer.empty[(Class[_], Throwable)]

    pokemons.foreach { subclass =>
      try {
        val pokemon = subclass.getDeclaredConstructor().newInstance()
        println(pokemon.pName)
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
}
