package pokemon.model

import scala.collection.mutable.{ArrayBuffer, ListBuffer, Queue}

class Battle() {
  private var _player: Player = _
  private var _bot: Bot = _
  private var _playerJustSwitched: Boolean = false
  private var _botJustSwitched: Boolean = false
  private var _playerJustFaintSwitched: Boolean = false
  private val _trainerQueueBasedOnMove: Queue[Trainer] = Queue.empty
  private var _playerPrevPokemonHp: Double = 0

  def player: Player = _player
  def bot: Bot = _bot
  def playerJustSwitched: Boolean = _playerJustSwitched
  def botJustSwitched: Boolean = _botJustSwitched
  def playerJustFaintSwitched: Boolean = _playerJustFaintSwitched
  def playerPrevPokemonHp: Double = _playerPrevPokemonHp

  def playerJustSwitched(flag: Boolean): Unit = _playerJustSwitched = flag
  def botJustSwitched(flag: Boolean): Unit = _botJustSwitched = flag
  def playerJustFaintSwitched(flag: Boolean): Unit = _playerJustFaintSwitched = flag

  def start(): Unit = {
    _player = new Player()
    _bot = new Bot()

    _player.generateDeck()
    _bot.generateDeck()

    // DEBUG: Print both deck
    // val deckMsg = f"""
    //   |Player's deck:
    //   |${_player.deck.zipWithIndex.map { case (p, i) =>
    //     f"  ${i + 1}. ${p.pName}" }.mkString("\n")}
    //   |
    //   |Bot's deck:
    //   |${_bot.deck.zipWithIndex.map { case (p, i) =>
    //     f"  ${i + 1}. ${p.pName}" }.mkString("\n")}
    //   |""".stripMargin
    // println(deckMsg)
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
    * - Handle switching
    * - Decide who attacks first based on speed
    * - Check if defender fainted
    *
    * @return
    */
  def performTurn(playerAction: Either[Move, Pokemon]): List[String] = {
    val results = ListBuffer[String]()
    _playerJustSwitched = false
    _botJustSwitched = false
    _playerJustFaintSwitched = false

    val botMove = _bot.chooseMove(_player.activePokemon)
    val botPokemonBeforeTurn = _bot.activePokemon
    val playerPokemonBeforeTurn = _player.activePokemon

    playerAction match {
      // Handle switching
      case Right(pokemon) =>
        results ++= switchPokemon(_player, pokemon)
        results ++= performAttack(_bot, _player, botMove)

      // Handle attacking
      case Left(playerMove) =>
        val (firstAttacker, firstMove, secondAttacker, secondMove) = decideFirstBySpeed(
          _player, playerMove, _bot, botMove)

        results ++= performAttack(firstAttacker, secondAttacker, firstMove)

        if (secondAttacker.isActivePokemonAlive) {
          results ++= performAttack(secondAttacker, firstAttacker, secondMove)
        }
    }

    results ++= handleBotFaintSwitch(_bot)

    _playerJustSwitched = _player.activePokemon != playerPokemonBeforeTurn
    _botJustSwitched = _bot.activePokemon != botPokemonBeforeTurn
    _playerJustFaintSwitched = !_player.isActivePokemonAlive && _player.activePokemon != playerPokemonBeforeTurn

    results.toList
  }

  def availablePlayerPokemon(): ArrayBuffer[Pokemon] = _player.deck.filter(p => p.health.value > 0 && p != _player.activePokemon)

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
      _trainerQueueBasedOnMove.enqueue(attacker)
      s"${attackerPokemon.pName} used ${move.moveName}!"
    }

    messages += attackMessage

    if (attackResult) {
      messages ++= effectMessages

      if (defenderPokemon.health.value == 0) {
        messages += s"${defenderPokemon.pName} fainted!"
      }
    }

    messages.toList
  }

  def switchPokemon(trainer: Trainer, pokemon: Pokemon): List[String] = {
    val messages = ListBuffer[String]()

    messages += s"${trainer.name} withdrew ${trainer.activePokemon.pName}!"
    trainer.switchActivePokemon(pokemon)
    messages += s"${trainer.name} sent out ${pokemon.pName}!"

    // Save the previous Pokemon's HP for the player if switch is done
    _playerPrevPokemonHp = pokemon.pokemonHpPercentage

    messages.toList
  }

  /**
    * Handle bot switch when the active Pokemon fainted
    *
    * - Consider type advantage
    *
    * @param bot
    * @return
    */
  private def handleBotFaintSwitch(bot: Bot): List[String] = {
    if (!bot.isActivePokemonAlive) {
      bot.switchToNextPokemon(_player.activePokemon) match {
        case Some(pokemon) => List(s"${bot.name}'s ${pokemon.pName} was sent out!")
        case None => List()
      }
    } else {
      List()
    }
  }

  /**
    * Get the next trainer in the queue based on the move sequence
    *
    * - Forward the queue if there are more than 1 trainers
    * - Clear the queue if there is only 1 trainer
    *
    * @return
    */
  def headTrainerQueue(): Trainer = {
    val current = _trainerQueueBasedOnMove.head
    if (_trainerQueueBasedOnMove.size > 1) _trainerQueueBasedOnMove.dequeue()
    else _trainerQueueBasedOnMove.clear()
    current
  }

  def isPlayer(trainer: Trainer): Boolean = trainer == _player
}
