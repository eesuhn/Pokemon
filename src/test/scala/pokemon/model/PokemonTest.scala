package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import java.lang.reflect.Modifier
import scala.collection.mutable.ListBuffer

class PokemonTest extends AnyFunSuite {

  test("Initialize all Pokemon subclasses") {
    val pokemonSubclasses = PokemonRegistry.pokemons

    assert(pokemonSubclasses.nonEmpty)

    // Restrict to only non-abstract classes
    // val instantiableClasses = pokemonSubclasses.filter(c => !Modifier.isAbstract(c.getModifiers))

    val instantiableClasses = pokemonSubclasses
    val failedInitializations = ListBuffer.empty[(Class[_], Throwable)]

    instantiableClasses.foreach { subclass =>
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
          |${failedInitializations.size} out of ${instantiableClasses.size} Pokemon subclasses failed to initialize:
          |$failureMessages
          |""".stripMargin
      )
    }
  }
}
