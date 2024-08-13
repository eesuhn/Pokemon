package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.{Map => MutableMap}
import scala.collection.mutable.{Set => MutableSet}

class MoveTest extends AnyFunSuite {

  test("Check if all Pokemon moves are of the Pokemon type") {
    val pokemonInstances = PokemonRegistry.pokemonInstances
    val invalidMoves = MutableMap.empty[String, MutableMap[List[String], List[(String, String)]]]

    pokemonInstances.values.foreach { pokemon =>
      val pokemonTypes = pokemon.pTypes
      pokemon.moves.foreach { move =>
        if (!pokemonTypes.contains(move.moveType) &&
            move.moveType != Normal
        ) {
          val pokemonEntry = invalidMoves.getOrElseUpdate(pokemon.pName, MutableMap.empty)
          val typeEntry = pokemonEntry.getOrElseUpdate(pokemon.pTypeNames, List.empty)
          pokemonEntry(pokemon.pTypeNames) = typeEntry :+ (move.moveName, move.moveType.name)
        }
      }
    }

    if (invalidMoves.nonEmpty) {
      val msg = invalidMoves.flatMap { case (pokemonName, typeMap) =>
        typeMap.flatMap { case (pokemonTypes, moves) =>
          f"""
            |${Colors.YELLOW}$pokemonName${Colors.NC} (${pokemonTypes.mkString(" ")}):
            |${moves.zipWithIndex.map { case ((moveName, moveType), index) =>
              f"  ${index + 1}. $moveName%-20s(${moveType.toLowerCase})"
            }.mkString("\n")}
            |""".stripMargin
        }
      }.mkString

      println(
        s"""
          |${Colors.PURPLE}Found ${invalidMoves.values.flatMap(_.values).map(_.size).sum} moves that are not of the Pokemon's type:${Colors.NC}
          |$msg""".stripMargin
      )
    } else {
      println(s"${Colors.GREEN}All moves are of the Pokemon's type.${Colors.NC}")
    }
  }

  test("Find moves not assigned to any Pokemon") {
    val pokemonInstances = PokemonRegistry.pokemonInstances
    val allMoves = MoveRegistry.moves.toSet
    val assignedMoves = MutableSet.empty[Move]

    pokemonInstances.values.foreach { pokemon =>
      assignedMoves ++= pokemon.moves
    }

    val unassignedMoves = allMoves -- assignedMoves

    if (unassignedMoves.nonEmpty) {
      println(s"${Colors.PURPLE}Found ${unassignedMoves.size} moves not assigned to any Pokemon:${Colors.NC}")
      unassignedMoves.foreach { move =>
        println(s"  ${move.moveName} (${move.moveType.name.toLowerCase})")
      }
    } else {
      println(s"${Colors.GREEN}All moves are assigned to at least one Pokemon.${Colors.NC}")
    }
  }

  test("List all moves and their usage") {
    val pokemonInstances = PokemonRegistry.pokemonInstances
    val moveUsage = MutableMap[String, Int]().withDefaultValue(0)
    val sortedMoves = moveUsage.toSeq.sortWith(_._2 > _._2)

    pokemonInstances.values.foreach { pokemon =>
      pokemon.moves.foreach { move =>
        moveUsage(move.moveName) += 1
      }
    }

    println(s"${Colors.PURPLE}Move usage across all Pokemons:${Colors.NC}")
    sortedMoves.foreach { case (moveName, count) =>
      println(f"$moveName%-20s: $count")
    }
  }
}
