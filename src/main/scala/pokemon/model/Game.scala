package pokemon.model

class Game {
  private var _player: Trainer = _
  private var _bot: Trainer = _
  private var _battle: Battle = _

  def player: Trainer = this._player
  def bot: Trainer = this._bot

  def start(): Unit = {
    this._player = new Player()
    this._bot = new Bot()

    this._player.generateDeck()
    this._bot.generateDeck()

    this._battle = new Battle(this._player, this._bot)
  }

  def isGameOver: Boolean = this._player.isDefeated || this._bot.isDefeated

  def winner: Option[Trainer] = {
    if (this._player.isDefeated) Some(this._bot)
    else if (this._bot.isDefeated) Some(this._player)
    else None
  }

  def performTurn(): List[String] = this._battle.performTurn()
}
