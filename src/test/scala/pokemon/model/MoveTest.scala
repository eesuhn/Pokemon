package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{Map => MutableMap}

class MoveTest extends AnyFunSuite {

  val NC = "\u001B[0m"
  val RED = "\u001B[31m"
  val GREEN = "\u001B[32m"
  val YELLOW = "\u001B[33m"
  val BLUE = "\u001B[34m"
  val PURPLE = "\u001B[35m"

  test("Check if all Pokemon moves are of the Pokemon's type") {
    val pokemons = PokemonRegistry.pokemons
    val invalidMoves = MutableMap.empty[String, MutableMap[List[String], List[(String, String)]]]

    pokemons.foreach { pokemonClass =>
      val pokemon = pokemonClass.getDeclaredConstructor().newInstance().asInstanceOf[Pokemon]
      val pokemonTypes = pokemon.pTypes

      pokemon.moves.foreach { move =>
        if (!pokemonTypes.contains(move.moveType) &&
            move.moveType != Normal &&
            move.moveName != "Ancient Power" &&
            move.moveName != "Bulk Up"
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
          s"""
            |${YELLOW}$pokemonName (${pokemonTypes.mkString(" ")})${NC}
            |${moves.map { case (moveName, moveType) =>
              s"  $moveName (${moveType.toLowerCase})"
            }.mkString("\n")}""".stripMargin
        }
      }.mkString

      println(
        s"""
          |${PURPLE}Found ${invalidMoves.values.flatMap(_.values).map(_.size).sum} moves that are not of the Pokemon's type:${NC}
          |$msg""".stripMargin
      )
    }
  }

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

  test("Compare moves within each Pokemon moveset") {
    val pokemons = PokemonRegistry.pokemons
    val fMoves = MutableMap.empty[String, ListBuffer[(String, String, String)]]

    pokemons.foreach { pokemonClass =>
      val pokemon = pokemonClass.getDeclaredConstructor().newInstance().asInstanceOf[Pokemon]
      val moveset = pokemon.moves

      for (i <- moveset.indices; j <- i + 1 until moveset.length) {
        val move1 = moveset(i)
        val move2 = moveset(j)

        if (compareMoves(move1, move2)) {
          fMoves.
            getOrElseUpdate(pokemon.pName, ListBuffer.empty) += ((move1.moveName, move2.moveName, "<"))
        } else if (compareMoves(move2, move1)) {
          fMoves.
            getOrElseUpdate(pokemon.pName, ListBuffer.empty) += ((move2.moveName, move1.moveName, ">"))
        }
      }
    }

    if (fMoves.nonEmpty) {
      val msg = fMoves.map { case (pokemonName, moves) =>
        s"""
          |${YELLOW}$pokemonName:${NC}
          |${moves.map { case (move1, move2, comp) =>
            s"  $move1 $comp $move2"
          }.mkString("\n")}""".stripMargin
      }.mkString

      println(
        s"""
          |${PURPLE}Found ${fMoves.values.map(_.size).sum} moves that are less efficient:${NC}
          |$msg""".stripMargin
      )
    }
  }

  private def compareMoves(move1: Move, move2: Move): Boolean = {
    (move1, move2) match {
      case (m1: PhysicalMove, m2: PhysicalMove) => comparePhysicalMoves(m1, m2)
      case _ => false
    }
  }

  private def comparePhysicalMoves(move1: PhysicalMove, move2: PhysicalMove): Boolean = {
    val val1 = move1.basePower * (move1.accuracy / 100.0)
    val val2 = move2.basePower * (move2.accuracy / 100.0)
    val1 < val2
  }
}

