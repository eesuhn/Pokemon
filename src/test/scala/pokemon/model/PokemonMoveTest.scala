package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{Map => MutableMap}

class PokemonMoveTest extends AnyFunSuite {

  test("Compare move type with Pokemon") {
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

  test("Each Pokemon move efficiency") {
    val pokemons = PokemonRegistry.pokemons
    val inefficientMovesByPokemon = MutableMap.empty[String, ListBuffer[(String, String, String)]]

    pokemons.foreach { pokemonClass =>
      val pokemon = pokemonClass.getDeclaredConstructor().newInstance().asInstanceOf[Pokemon]
      val moves = pokemon.moves

      for (i <- moves.indices; j <- i + 1 until moves.length) {
        val move1 = moves(i)
        val move2 = moves(j)

        if (isLessEfficient(move1, move2)) {
          inefficientMovesByPokemon.getOrElseUpdate(pokemon.pName, ListBuffer.empty) += ((move1.moveName, move2.moveName, "<"))
        } else if (isLessEfficient(move2, move1)) {
          inefficientMovesByPokemon.getOrElseUpdate(pokemon.pName, ListBuffer.empty) += ((move2.moveName, move1.moveName, ">"))
        }
      }
    }

    if (inefficientMovesByPokemon.nonEmpty) {
      val errorMessages = inefficientMovesByPokemon.map { case (pokemonName, moves) =>
        s"""
        |$pokemonName:
        |${moves.map { case (move1, move2, comparison) =>
          s"  $move1 $comparison $move2"
        }.mkString("\n")}""".stripMargin
      }.mkString("\n")

      fail(
        s"""
        |Found potentially inefficient move comparisons:
        |$errorMessages""".stripMargin
      )
    }
  }

  private def isLessEfficient(move1: Move, move2: Move): Boolean = {
    (move1, move2) match {
      case (m1: SpecialMove, m2: SpecialMove) => compareSpecialMoves(m1, m2)
      case (m1: PhysicalMove, m2: PhysicalMove) => comparePhysicalMoves(m1, m2)
      case (m1: StatusMove, m2: StatusMove) => compareStatusMoves(m1, m2)
      case _ => false
    }
  }

  private def comparePhysicalMoves(move1: PhysicalMove, move2: PhysicalMove): Boolean = {
    val effectivePower1 = move1.basePower * (move1.accuracy / 100.0)
    val effectivePower2 = move2.basePower * (move2.accuracy / 100.0)

    effectivePower1 < effectivePower2
  }

  private def compareStatusMoves(move1: StatusMove, move2: StatusMove): Boolean = {
    def evaluateEffects(move: StatusMove): Int = {
      move.effects.map { effect =>
        val value = effect.stage.abs
        if (move.targetSelf) value else -value
      }.sum
    }

    val effectValue1 = evaluateEffects(move1)
    val effectValue2 = evaluateEffects(move2)

    if (move1.targetSelf == move2.targetSelf) {
      (effectValue1 < effectValue2) || (effectValue1 == effectValue2 && move1.accuracy < move2.accuracy)
    } else {
      false
    }
  }

  private def compareSpecialMoves(move1: SpecialMove, move2: SpecialMove): Boolean = {
    val physicalComparison = comparePhysicalMoves(move1, move2)
    val statusComparison = compareStatusMoves(move1, move2)
    physicalComparison && statusComparison
  }
}
