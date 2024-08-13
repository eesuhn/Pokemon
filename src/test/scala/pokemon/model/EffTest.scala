package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{Map => MutableMap}
import scala.math.BigDecimal.RoundingMode

class EffTest extends AnyFunSuite {

  private val _boundRange = 10.0
  private var _okayWeightage = 0
  private var _nearLimitWeightage = 0
  private var _outsideRangeWeightage = 0

  // Weightage for moveset to normalize with base stats
  private val _move_weightage = 200.0

  test("Rank moves and check rarity weightage") {
    val pokemonInstances = PokemonRegistry.pokemonInstances
    val moveRankings = MutableMap.empty[String, ListBuffer[(String, Double)]]
    val weightageResults = MutableMap.empty[String, (Double, String)]

    pokemonInstances.values.foreach { pokemon =>
      val moveset = pokemon.moves

      val scores = moveset.map(move => (move.moveName, move.moveEfficiency()))
      val sortedScores = scores.sortBy(-_._2)

      moveRankings.getOrElseUpdate(pokemon.pName, ListBuffer.empty) ++= sortedScores

      val totalWeightage = calculateTotalWeightage(pokemon, sortedScores)
      val status = getWeightageStatus(pokemon, totalWeightage)

      weightageResults(pokemon.pName) = (totalWeightage, status)
    }

    printResults(pokemonInstances, moveRankings, weightageResults)
  }

  private def getWeightageStatus(pokemon: Pokemon, totalWeightage: Double): String = {
    val upperLimit = pokemon.rarity.weightageUpperBound
    val lowerLimit = pokemon.rarity.weightageLowerBound

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
    val baseStatsWeightage = pokemon.baseStatScore()
    val movesetWeightage = sortedScores.map(_._2).sum * _move_weightage
    BigDecimal(baseStatsWeightage + movesetWeightage).setScale(2, RoundingMode.HALF_UP).toDouble
  }

  private def printResults(
    pokemonInstances: Map[String, Pokemon],
    moveRankings: MutableMap[String, ListBuffer[(String, Double)]],
    weightageResults: MutableMap[String, (Double, String)]
  ): Unit = {
    if (moveRankings.nonEmpty) {
      val sortedResults = weightageResults.toSeq.sortBy(-_._2._1)

      val groupedResults = sortedResults
        .groupBy { case (pokemonName, _) =>
          pokemonInstances.get(pokemonName).map(_.rarity).getOrElse(Common)
        }
        .toSeq
        .sortBy(_._1.weightageUpperBound)
        .reverse

      val msg = groupedResults.map { case (rarity, pokemonGroup) =>
        val rarityName = rarity.getClass.getSimpleName
          .replaceAll("([A-Z])", " $1")
          .trim
          .toUpperCase
          .replaceAll("\\$$", "")
        s"""
          |${Colors.PURPLE}>>>>>${Colors.NC} ${rarityName} ${Colors.PURPLE}<<<<<${Colors.NC}
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
          |${Colors.RED}NOPE${Colors.NC}%-20s: ${_outsideRangeWeightage}
          |$msg""".stripMargin
      )
    }
  }
}
