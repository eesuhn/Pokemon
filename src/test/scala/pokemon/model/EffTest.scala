package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{Map => MutableMap}

class EffTest extends AnyFunSuite {

  // Weightage for different aspects of a move
  private val _power_weight = 0.5
  private val _status_weight = 0.5

  // Weightage for different stat effects
  private val _stat_effect_weight = Map(
    "AttackEffect" -> 1.0,
    "DefenseEffect" -> 1.0,
    "AccuracyEffect" -> 0.8,
    "SpeedEffect" -> 0.6,
    "CriticalHitEffect" -> 0.8
  )

  // Maximum base power for normalization
  private val _max_base_power = 300.0

  test("Rank moves based on efficiency") {
    val pokemons = PokemonRegistry.pokemons
    val moveRankings = MutableMap.empty[String, ListBuffer[(String, Double)]]

    pokemons.foreach { pokemonClass =>
      val pokemon = pokemonClass.getDeclaredConstructor().newInstance().asInstanceOf[Pokemon]
      val moveset = pokemon.moves

      val scores = moveset.map(move => (move.moveName, calculateMoveEfficiency(move)))
      val sortedScores = scores.sortBy(-_._2)

      moveRankings.getOrElseUpdate(pokemon.pName, ListBuffer.empty) ++= sortedScores
    }

    printResults(moveRankings)
  }

  private def calculateMoveEfficiency(move: Move): Double = {
    val powerScore = calculatePowerScore(move)
    val statusScore = calculateStatusScore(move)
    (powerScore * _power_weight) + (statusScore * _status_weight)
  }

  private def calculatePowerScore(move: Move): Double = move match {
    case m: SpecialMove => (m.basePower / _max_base_power) * (m.accuracy / 100.0)
    case m: PhysicalMove => (m.basePower / _max_base_power) * (m.accuracy / 100.0)
    case _ => 0.0
  }

  private def calculateStatusScore(move: Move): Double = {
    val effects = move match {
      case m: SpecialMove => m.effects
      case m: StatusMove => m.effects
      case _ => List.empty
    }

    val targetSelf = move match {
      case m: SpecialMove => m.targetSelf
      case m: StatusMove => m.targetSelf
      case _ => false
    }

    effects.map { effect =>
      val effectType = effect.getClass.getSimpleName
      val weight = _stat_effect_weight.getOrElse(effectType, 0.5)
      val stageValue = effect.stage
      if (targetSelf) stageValue * weight else -stageValue * weight
    }.sum / 6.0  // Normalize by maximum possible stage change
  }

  private def printResults(moveRankings: MutableMap[String, ListBuffer[(String, Double)]]): Unit = {
    if (moveRankings.nonEmpty) {
      val msg = moveRankings.map { case (pokemonName, moves) =>
        s"""
          |${Colors.YELLOW}$pokemonName:${Colors.NC}
          |${moves.zipWithIndex.map { case ((moveName, score), index) =>
          f"  ${index + 1}. $moveName%-20s($score%.2f)"
        }.mkString("\n")}""".stripMargin
      }.mkString("\n")

      println(
        s"""
          |${Colors.PURPLE}Move rankings for each Pokemon:${Colors.NC}
          |$msg""".stripMargin
      )
    }
  }
}
