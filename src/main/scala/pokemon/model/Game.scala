package pokemon.model

import scala.util.Random

class Game {
  var player1: Player = _
  var player2: Player = _
  var currentPlayer: Player = _

  def start(p1Name: String, p2Name: String): Unit = {
    this.player1 = new Player(p1Name)
    this.player2 = new Player(p2Name)

    decideFirstPlayer()

    this.player1.generateDeck()
    this.player2.generateDeck()
  }

  def decideFirstPlayer(): Unit = {
    val rand = Random
    this.currentPlayer = if (rand.nextBoolean()) this.player1 else this.player2
  }
}
