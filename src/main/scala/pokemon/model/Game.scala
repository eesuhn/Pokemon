package pokemon.model

class Game {
  private var _player: Player = _
  private var _bot: Bot = _
  private var _battle: Battle = _

  def player: Player = this._player
  def bot: Bot = this._bot

  def start(): Unit = {
    this._player = new Player()
    this._bot = new Bot()
    this._battle = new Battle(this._player, this._bot)

    this._player.generateDeck()
    this._bot.generateDeck()
  }

  /**
    * Linked to `performTurn` from `Battle`
    *
    * @return
    */
  def performTurn(): List[String] = this._battle.performTurn()

  def isGameOver: Boolean = this._player.isDefeated || this._bot.isDefeated

  def winner: Option[Trainer] = {
    if (this._player.isDefeated) Some(this._bot)
    else if (this._bot.isDefeated) Some(this._player)
    else None
  }
}
