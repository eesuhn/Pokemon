package pokemon.model

import scala.collection.mutable.ListBuffer

class Battle() {
  private var _player: Player = _
  private var _bot: Bot = _

  def player: Player = _player
  def bot: Bot = _bot

  def start(): Unit = {
    _player = new Player()
    _bot = new Bot()

    _player.generateDeck()
    _bot.generateDeck()
  }

  def isBattleOver: Boolean = _player.isDefeated || _bot.isDefeated

  def winner: Option[Trainer] = {
    if (_player.isDefeated) Some(_bot)
    else if (_bot.isDefeated) Some(_player)
    else None
  }

  /**
    * Perform a turn in the battle
    *
    * - Decide who attacks first based on speed
    * - Check if defender fainted
    *
    * @return
    */
  def performTurn(): List[String] = {
    val results = ListBuffer[String]()

    val playerMove = player.chooseMove()
    val botMove = bot.chooseMove()

    val (firstAttacker, firstMove, secondAttacker, secondMove) = decideFirstBySpeed(
      player, playerMove, bot, botMove)

    // First attack
    results ++= performAttack(firstAttacker, if (firstAttacker == player) bot else player, firstMove)

    // Check if the second Pokemon fainted
    if ((secondAttacker == player && player.hasActivePokemon) ||
        (secondAttacker == bot && bot.hasActivePokemon)) {
      // Second attack
      results ++= performAttack(secondAttacker, if (secondAttacker == player) bot else player, secondMove)
    }

    results ++= handleFaintSwitch(player)
    results ++= handleFaintSwitch(bot)

    results.toList
  }

  private def decideFirstBySpeed(
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
  private def performAttack(attacker: Trainer, defender: Trainer, move: Move): List[String] = {
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
  private def handleFaintSwitch(trainer: Trainer): List[String] = {
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
