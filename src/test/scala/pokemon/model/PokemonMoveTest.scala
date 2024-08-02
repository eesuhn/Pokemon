package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer

class PokemonMoveTest extends AnyFunSuite {

  test("Check if Pokemon have moves not of their type") {
    val pokemons = PokemonRegistry.pokemons
    val invalidMoves = ListBuffer.empty[(String, List[String], String, String)]

    pokemons.foreach { pokemonClass =>
      val pokemon = pokemonClass.getDeclaredConstructor().newInstance().asInstanceOf[Pokemon]
      val pokemonTypes = pokemon.pTypes

      pokemon.moves.foreach { move =>
        if (!pokemonTypes.contains(move.moveType) &&
          move.moveType != Normal &&
          move.moveName != "Ancient Power" &&
          move.moveName != "Bulk Up"
        ) {
          invalidMoves += ((pokemon.pName, pokemon.pTypeNames, move.moveName, move.moveType.name))
        }
      }
    }

    if (invalidMoves.nonEmpty) {
      val errorMessages = invalidMoves.map { case (pokemonName, pokemonTypeNames, moveName, moveTypeName) =>
        s"""
        |$pokemonName (${pokemonTypeNames.mkString(" ")})
        |  $moveName (${moveTypeName.toLowerCase})""".stripMargin
      }.mkString

      fail(
        s"""
        |Found ${invalidMoves.size} moves that are not of the Pokemon's type:
        |$errorMessages""".stripMargin
      )
    }
  }
}
