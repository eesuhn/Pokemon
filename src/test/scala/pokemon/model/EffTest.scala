package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{Map => MutableMap}
import scala.math.BigDecimal.RoundingMode

class EffTest extends AnyFunSuite {

  private val rarityWeightageLimits = Map(
    1 -> 250.0,  // Common
    2 -> 300.0,  // Uncommon
    3 -> 350.0,  // Rare
    4 -> 400.0,  // Super Rare
    5 -> 500.0   // Ultra Rare
  )

  private val baseStatWeights = Map(
    "health" -> 0.6,
    "attack" -> 1.0,
    "defense" -> 0.8,
    "speed" -> 0.6
  )

  // Maximum base power for normalization
  private val _max_base_power = 300.0

  private val _power_weight = 0.5
  private val _status_weight = 0.5

  private val _stat_effect_weight = Map(
    "AttackEffect" -> 1.0,
    "DefenseEffect" -> 1.0,
    "AccuracyEffect" -> 0.8,
    "SpeedEffect" -> 0.6,
    "CriticalHitEffect" -> 0.8
  )

  test("Rank moves and check rarity weightage") {
    val pokemons = PokemonRegistry.pokemons
    val moveRankings = MutableMap.empty[String, ListBuffer[(String, Double)]]
    val weightageResults = MutableMap.empty[String, (Double, String)]

    pokemons.foreach { pokemonClass =>
      val pokemon = pokemonClass.getDeclaredConstructor().newInstance().asInstanceOf[Pokemon]
      val moveset = pokemon.moves

      val scores = moveset.map(move => (move.moveName, calculateMoveEfficiency(move)))
      val sortedScores = scores.sortBy(-_._2)

      moveRankings.getOrElseUpdate(pokemon.pName, ListBuffer.empty) ++= sortedScores

      val totalWeightage = calculateTotalWeightage(pokemon, sortedScores)
      val upperRarityLimit = rarityWeightageLimits(pokemon.rarity.value)
      val lowerRarityLimit = if (pokemon.rarity.value > 1) rarityWeightageLimits(pokemon.rarity.value - 1) else 0.0

      val status = if (totalWeightage > upperRarityLimit) s"${Colors.RED}EXCEEDED${Colors.NC}"
      else if (totalWeightage < lowerRarityLimit) s"${Colors.YELLOW}UNDER${Colors.NC}"
      else s"${Colors.GREEN}OK${Colors.NC}"

      weightageResults(pokemon.pName) = (totalWeightage, status)
    }

    printResults(moveRankings, weightageResults)
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

  private def calculateTotalWeightage(pokemon: Pokemon, sortedScores: List[(String, Double)]): Double = {
    val baseStatsWeightage = calculateBaseStatsWeightage(pokemon)
    val movesetWeightage = sortedScores.map(_._2).sum * 100
    BigDecimal(baseStatsWeightage + movesetWeightage).setScale(2, RoundingMode.HALF_UP).toDouble
  }

  private def calculateBaseStatsWeightage(pokemon: Pokemon): Double = {
    val healthWeight = pokemon.baseHP * baseStatWeights("health")
    val attackWeight = pokemon.attack.value * baseStatWeights("attack")
    val defenseWeight = pokemon.defense.value * baseStatWeights("defense")
    val speedWeight = pokemon.speed.value * baseStatWeights("speed")
    healthWeight + attackWeight + defenseWeight + speedWeight
  }

  private def printResults(
    moveRankings: MutableMap[String, ListBuffer[(String, Double)]],
    weightageResults: MutableMap[String, (Double, String)]
  ): Unit = {

    if (moveRankings.nonEmpty) {
      val msg = moveRankings.map { case (pokemonName, moves) =>
        val (totalWeightage, status) = weightageResults(pokemonName)
        s"""
          |${Colors.YELLOW}$pokemonName${Colors.NC} (${totalWeightage}, $status):
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
