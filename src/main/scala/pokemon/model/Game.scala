package pokemon.model

import scala.util.Random
import scala.collection.mutable.ListBuffer

class Game {
  private var _player: Trainer = _
  private var _bot: Trainer = _

  def player: Trainer = this._player
  def bot: Trainer = this._bot

  def start(): Unit = {
    this._player = new Player()
    this._bot = new Bot()

    this._player.generateDeck()
    this._bot.generateDeck()
  }

  def isGameOver: Boolean = {
    this._player.deck.forall(_.currentHP == 0) || this._bot.deck.forall(_.currentHP == 0)
  }

  def winner: Option[Trainer] = {
    if (this._player.deck.forall(_.currentHP == 0)) Some(this._bot)
    else if (this._bot.deck.forall(_.currentHP == 0)) Some(this._player)
    else None
  }

  def performTurn(): List[String] = {
    val res = ListBuffer[String]()

    val (playerMoveIndex, playerMove) = this._player.chooseMove()
    val (botMoveIndex, botMove) = this._bot.chooseMove()

    val (firstAttacker, firstMove, secondAttacker, secondMove) = decideFirst(
      this._player, playerMove, this._bot, botMove)

    // First attack
    res += performAttack(firstAttacker, if (firstAttacker == this._player) this._bot else this._player, firstMove)

    // Check if the second Pokemon fainted
    if ((secondAttacker == this._player && this._player.hasActivePokemon) ||
        (secondAttacker == this._bot && this._bot.hasActivePokemon)) {
      // Second attack
      res += performAttack(secondAttacker, if (secondAttacker == this._player) this._bot else this._player, secondMove)
    }

    // Handle fainting and switching
    if (!this._player.hasActivePokemon) {
      if (this._player.switchToNextAlivePokemon()) {
        res += s"${this._player.name}'s ${this._player.activePokemon.pName} was sent out!"
      }
    }
    if (!this._bot.hasActivePokemon) {
      if (this._bot.switchToNextAlivePokemon()) {
        res += s"${this._bot.name}'s ${this._bot.activePokemon.pName} was sent out!"
      }
    }

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

    if (!attackResult) return s"${attackerPokemon.pName}'s attack missed!"

    if (defenderPokemon.currentHP == 0) {
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
}
