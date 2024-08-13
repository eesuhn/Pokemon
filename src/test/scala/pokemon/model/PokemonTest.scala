package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.{Map => MutableMap}

class PokemonTest extends AnyFunSuite {

  test("Count each Pokemon type") {
    val pokemonInstances = PokemonRegistry.pokemonInstances
    val count = MutableMap.empty[String, Int]

    pokemonInstances.values.foreach { pokemon =>
      pokemon.pTypes.foreach { pType =>
        count(pType.name) = count.getOrElse(pType.name, 0) + 1
      }
    }

    val msg = count.toSeq.sortBy(-_._2).map { case (pType, num) =>
      f"${Colors.YELLOW}$pType%-20s:${Colors.NC} $num"
    }.mkString("\n")

    println(
      s"""
        |${Colors.PURPLE}Pokemon type count:${Colors.NC}
        |$msg""".stripMargin
    )
  }
}
