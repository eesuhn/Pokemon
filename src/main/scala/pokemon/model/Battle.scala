package pokemon.model

import scala.collection.mutable.ListBuffer

class Battle(val player: Trainer, val bot: Trainer) {

  def performTurn(): List[String] = {
    val res = ListBuffer[String]()

    val playerMove = player.chooseMove()
    val botMove = bot.chooseMove()

    val (firstAttacker, firstMove, secondAttacker, secondMove) = decideFirst(
      player, playerMove, bot, botMove)

    // First attack
    res += performAttack(firstAttacker, if (firstAttacker == player) bot else player, firstMove)

    // Check if the second Pokemon fainted
    if ((secondAttacker == player && player.hasActivePokemon) ||
        (secondAttacker == bot && bot.hasActivePokemon)) {
      // Second attack
      res += performAttack(secondAttacker, if (secondAttacker == player) bot else player, secondMove)
    }

    res ++= handleFaintingAndSwitching(player)
    res ++= handleFaintingAndSwitching(bot)

    res.toList
  }

  private def decideFirst(
    attacker1: Trainer,
    move1: Move,
    attacker2: Trainer,
    move2: Move
  ): (Trainer, Move, Trainer, Move) = {

    val attacker1Speed = attacker1.activePokemon.speed.value
    val attacker2Speed = attacker2.activePokemon.speed.value

    if (attacker1Speed >= attacker2Speed) (attacker1, move1, attacker2, move2)
    else (attacker2, move2, attacker1, move1)
  }

  private def performAttack(attacker: Trainer, defender: Trainer, move: Move): String = {
    val attackerPokemon = attacker.activePokemon
    val defenderPokemon = defender.activePokemon

    val attackResult = attackerPokemon.attack(move, defenderPokemon)

    if (!attackResult) {
      s"""

        |${attackerPokemon.pName} used ${move.moveName}!
        |${move.moveName} missed!

      """
    } else if (defenderPokemon.currentHP == 0) {
      s"""

        |${attackerPokemon.pName} used ${move.moveName}!
        |${defenderPokemon.pName} fainted!

      """
    } else {
      s"""

        |>>> START <<<
        |
        |${attackerPokemon.pName} used ${move.moveName}!
        |
        |${attackerPokemon.pName}:
        |HP: ${attackerPokemon.currentHP}
        |Attack: ${attackerPokemon.attack.value}
        |Defense: ${attackerPokemon.defense.value}
        |Accuracy: ${attackerPokemon.accuracy.value}
        |Speed: ${attackerPokemon.speed.value}
        |
        |${defenderPokemon.pName}:
        |HP: ${defenderPokemon.currentHP}
        |Attack: ${defenderPokemon.attack.value}
        |Defense: ${defenderPokemon.defense.value}
        |Accuracy: ${defenderPokemon.accuracy.value}
        |Speed: ${defenderPokemon.speed.value}
        |
        |>>> END <<<

      """
    }
  }

  private def handleFaintingAndSwitching(trainer: Trainer): List[String] = {
    if (!trainer.hasActivePokemon) {
      trainer.switchToNextAlivePokemon() match {
        case Some(pokemon) => List(s"${trainer.name}'s ${pokemon.pName} was sent out!")
        case None => List()
      }
    } else {
      List()
    }
  }
}
