package pokemon.model

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{Map => MutableMap}

class EffTest extends AnyFunSuite {

  private var _okayWeightage = 0
  private var _nearLimitWeightage = 0
  private var _outsideRangeWeightage = 0

  test("Rank moves and check rarity weightage") {
    val pokemonInstances = PokemonRegistry.pokemonInstances
    val moveRankings = MutableMap.empty[String, ListBuffer[(String, Double)]]
    val weightageResults = MutableMap.empty[String, (Double, String)]

    pokemonInstances.values.foreach { pokemon =>
      val moveset = pokemon.moves
      val scores = moveset.map(move => (move.moveName, move.moveEfficiency()))
      val sortedScores = scores.sortBy(-_._2)
      moveRankings.getOrElseUpdate(pokemon.pName, ListBuffer.empty) ++= sortedScores

      val status = getWeightageStatus(pokemon)
      val totalWeightage = pokemon.score
      weightageResults(pokemon.pName) = (totalWeightage, status)
    }

    printResults(pokemonInstances, moveRankings, weightageResults)
  }

  private def getWeightageStatus(pokemon: Pokemon): String = {
    if (pokemon.outOfBounds) {
      _outsideRangeWeightage += 1
      s"${Colors.RED}"
    } else if (pokemon.nearBounds) {
      _nearLimitWeightage += 1
      s"${Colors.YELLOW}"
    } else {
      _okayWeightage += 1
      s"${Colors.GREEN}"
    }
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
          |${Colors.PURPLE}######${Colors.NC} ${rarityName} ${Colors.PURPLE}######${Colors.NC}
          |${pokemonGroup.map { case (pokemonName, (totalWeightage, status)) =>
            val moves = moveRankings(pokemonName)
            f"""
              |${status}$pokemonName${Colors.NC} ($totalWeightage%.2f):
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
