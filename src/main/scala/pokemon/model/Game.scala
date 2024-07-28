package pokemon.model

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
    * Linked to `performTurn` from `Battle`
    *
    * @return
    */
  def performTurn(): List[String] = _battle.performTurn()

  def isGameOver: Boolean = _player.isDefeated || _bot.isDefeated

  /**
    * Checks if any of the Trainers is defeated, and returns the winner
    *
    * @return
    */
  def winner: Option[Trainer] = {
    if (_player.isDefeated) Some(_bot)
    else if (_bot.isDefeated) Some(_player)
    else None
  }
}
