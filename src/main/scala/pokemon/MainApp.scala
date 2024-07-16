package pokemon

import pokemon.model.Game

object MainApp extends App {
  val game = new Game
  game.start("Ash", "Gary")

  println(s"${game.player1.playerName}'s Pokemon:")
  game
    .player1
    .deck
    .foreach(pokemon => println(s"\t${pokemon.pName}"))
}
