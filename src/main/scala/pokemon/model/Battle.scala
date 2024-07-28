package pokemon.model

import scala.collection.mutable.ListBuffer

class Battle(
  val player: Trainer,
  val bot: Trainer
) {

  def decideFirstBySpeed(
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

  /**
    * Perform an attack on the defender
    *
    * Returns a list of messages to be displayed to the user
    *
    * @param attacker
    * @param defender
    * @param move
    * @return
    */
  def performAttack(attacker: Trainer, defender: Trainer, move: Move): List[String] = {
    val messages = ListBuffer[String]()
    val attackerPokemon = attacker.activePokemon
    val defenderPokemon = defender.activePokemon

    val (attackResult, effectMessages) = attackerPokemon.attack(move, defenderPokemon)

    val attackMessage = if (!attackResult) {
      s"${attackerPokemon.pName} used ${move.moveName}! But it missed!"
    } else {
      s"${attackerPokemon.pName} used ${move.moveName}!"
    }

    messages += attackMessage

    if (attackResult) {
      messages ++= effectMessages

      if (defenderPokemon.currentHP == 0) {
        messages += s"${defenderPokemon.pName} fainted!"
      }
    }

    messages.toList
  }

  /**
    * Switch to the next alive Pokemon if the current Pokemon fainted
    *
    * @param trainer
    * @return
    */
  def handleFaintSwitch(trainer: Trainer): List[String] = {
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
