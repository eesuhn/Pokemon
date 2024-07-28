package pokemon.model

import scala.collection.mutable.ListBuffer

class Game {
  private var _player: Player = _
  private var _bot: Bot = _
  private var _battle: Battle = _

  def player: Player = _player
  def bot: Bot = _bot

  def start(): Unit = {
    _player = new Player()
    _bot = new Bot()
    _battle = new Battle(_player, _bot)

    _player.generateDeck()
    _bot.generateDeck()
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

    val (firstAttacker, firstMove, secondAttacker, secondMove) = _battle.decideFirstBySpeed(
      player, playerMove, bot, botMove)

    // First attack
    results ++= _battle.performAttack(firstAttacker, if (firstAttacker == player) bot else player, firstMove)

    // Check if the second Pokemon fainted
    if ((secondAttacker == player && player.hasActivePokemon) ||
        (secondAttacker == bot && bot.hasActivePokemon)) {
      // Second attack
      results ++= _battle.performAttack(secondAttacker, if (secondAttacker == player) bot else player, secondMove)
    }

    results ++= _battle.handleFaintSwitch(player)
    results ++= _battle.handleFaintSwitch(bot)

    results.toList
  }

  def isGameOver: Boolean = _player.isDefeated || _bot.isDefeated

  def winner: Option[Trainer] = {
    if (_player.isDefeated) Some(_bot)
    else if (_bot.isDefeated) Some(_player)
    else None
  }
}
