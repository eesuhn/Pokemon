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

    pokemonInstances.values.foreach { pokemon =>
      pokemon.moves.foreach { move =>
        moveUsage(move.moveName) += 1
      }
    }

    val groupedMoves = moveUsage.groupBy(_._2).toSeq.sortBy(_._1).reverse

    println(s"${Colors.PURPLE}Move usage across all Pokemons:${Colors.NC}")
    groupedMoves.foreach { case (count, moves) =>
      val msg = f"""
        |${Colors.YELLOW}$count:${Colors.NC}
        |${moves.keys.toSeq.sorted.mkString(s"${Colors.YELLOW}, ${Colors.NC}")}
        |""".stripMargin
      println(msg)
    }
  }

  test("Count of moves based on type") {
    val moves = MoveRegistry.moves
    val moveTypeUsage = MutableMap[Type, Int]().withDefaultValue(0)

    moves.foreach { move =>
      moveTypeUsage(move.moveType) += 1
    }

    val groupedMoves = moveTypeUsage.toSeq.sortBy(_._2).reverse

    println(s"${Colors.PURPLE}Move count based on type:${Colors.NC}")
    val msg = f"""
      |${groupedMoves.map { case (moveType, count) =>
        f"${moveType.name}%-10s$count"
      }.mkString("\n")}
      |""".stripMargin
    println(msg)
  }
}
