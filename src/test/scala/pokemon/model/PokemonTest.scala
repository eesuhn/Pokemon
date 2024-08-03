package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.{Map => MutableMap}

class PokemonTest extends AnyFunSuite {

  val NC = "\u001B[0m"
  val RED = "\u001B[31m"
  val GREEN = "\u001B[32m"
  val YELLOW = "\u001B[33m"
  val BLUE = "\u001B[34m"
  val PURPLE = "\u001B[35m"

  test("Count each Pokemon type") {
    val pokemons = PokemonRegistry.pokemons
    val count = MutableMap.empty[String, Int]

    pokemons.foreach { pokemonClass =>
      val pokemon = pokemonClass.getDeclaredConstructor().newInstance().asInstanceOf[Pokemon]
      pokemon.pTypes.foreach { pType =>
        count(pType.name) = count.getOrElse(pType.name, 0) + 1
      }
    }

    val msg = count.toSeq.sortBy(-_._2).map { case (pType, num) =>
      s"${YELLOW}$pType:${NC} $num"
    }.mkString("\n")

    println(
      s"""
        |${PURPLE}Pokemon type count:${NC}
        |$msg""".stripMargin
    )
  }
}
