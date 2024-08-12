package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{Map => MutableMap}
import scala.math.BigDecimal.RoundingMode

class EffTest extends AnyFunSuite {

  private val rarityWeightageLimits = Map(
    1 -> 200.0,  // Common
    2 -> 300.0,  // Uncommon
    3 -> 400.0,  // Rare
    4 -> 500.0,  // Super Rare
    5 -> 650.0   // Ultra Rare
  )

  private val _boundRange = 10.0
  private var _okayWeightage = 0
  private var _nearLimitWeightage = 0
  private var _outsideRangeWeightage = 0

  private val baseStatWeights = Map(
    "health" -> 0.8,
    "attack" -> 1.0,
    "defense" -> 0.8,
    "speed" -> 0.6
  )

  // Weightage for moveset to normalize with base stats
  private val _move_weightage = 200.0

  test("Rank moves and check rarity weightage") {
    val pokemons = PokemonRegistry.pokemons
    val moveRankings = MutableMap.empty[String, ListBuffer[(String, Double)]]
    val weightageResults = MutableMap.empty[String, (Double, String)]

    pokemons.foreach { pokemonClass =>
      val pokemon = pokemonClass.getDeclaredConstructor().newInstance().asInstanceOf[Pokemon]
      val moveset = pokemon.moves

      val scores = moveset.map(move => (move.moveName, move.moveEfficiency()))
      val sortedScores = scores.sortBy(-_._2)

      moveRankings.getOrElseUpdate(pokemon.pName, ListBuffer.empty) ++= sortedScores

      val totalWeightage = calculateTotalWeightage(pokemon, sortedScores)
      val status = getWeightageStatus(pokemon, totalWeightage)

      weightageResults(pokemon.pName) = (totalWeightage, status)
    }

    printResults(pokemons, moveRankings, weightageResults)
  }

  private def getWeightageStatus(pokemon: Pokemon, totalWeightage: Double): String = {
    val upperLimit = rarityWeightageLimits(pokemon.rarity.value)
    val lowerLimit = if (pokemon.rarity.value > 1) rarityWeightageLimits(pokemon.rarity.value - 1) else 0.0

    if (totalWeightage > upperLimit || totalWeightage < lowerLimit) {
      _outsideRangeWeightage += 1
      s"${Colors.RED}"
    } else if (totalWeightage > upperLimit - _boundRange || totalWeightage < lowerLimit + _boundRange) {
      _nearLimitWeightage += 1
      s"${Colors.YELLOW}"
    } else {
      _okayWeightage += 1
      s"${Colors.GREEN}"
    }
  }

  private def calculateTotalWeightage(pokemon: Pokemon, sortedScores: List[(String, Double)]): Double = {
    val baseStatsWeightage = calculateBaseStatsWeightage(pokemon)
    val movesetWeightage = sortedScores.map(_._2).sum * _move_weightage
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
    pokemons: Seq[Class[_ <: Pokemon]],
    moveRankings: MutableMap[String, ListBuffer[(String, Double)]],
    weightageResults: MutableMap[String, (Double, String)]
  ): Unit = {
    if (moveRankings.nonEmpty) {
      val sortedResults = weightageResults.toSeq.sortBy(-_._2._1)

      val groupedResults = sortedResults
        .groupBy { case (pokemonName, _) =>
          pokemons.find(_.getDeclaredConstructor().newInstance().asInstanceOf[Pokemon].pName == pokemonName)
            .map(_.getDeclaredConstructor().newInstance().asInstanceOf[Pokemon].rarity.value)
            .getOrElse(0)
        }
        .toSeq
        .sortBy(_._1)
        .reverse

      val msg = groupedResults.map { case (rarity, pokemonGroup) =>
        s"""
          |${Colors.PURPLE}>>>>> RARITY $rarity <<<<<${Colors.NC}
          |${pokemonGroup.map { case (pokemonName, (totalWeightage, status)) =>
            val moves = moveRankings(pokemonName)
            s"""
              |${status}$pokemonName${Colors.NC} ($totalWeightage):
              |${moves.zipWithIndex.map { case ((moveName, score), index) =>
              val scoreColor = if (score <= 0.0) Colors.RED else Colors.NC
              f"  ${index + 1}. $moveName%-20s(${scoreColor}$score%.2f${Colors.NC})"
            }.mkString("\n")}""".stripMargin
          }.mkString("\n")}""".stripMargin
      }.mkString("\n")

      println(
        f"""
          |${Colors.PURPLE}Move rankings for each Pokemon:${Colors.NC}
          |${Colors.GREEN}OKAY${Colors.NC}%-20s: ${_okayWeightage}
          |${Colors.YELLOW}NEAR${Colors.NC}%-20s: ${_nearLimitWeightage}
          |${Colors.RED}EXCD${Colors.NC}%-20s: ${_outsideRangeWeightage}
          |$msg""".stripMargin
      )
    }
  }
}
