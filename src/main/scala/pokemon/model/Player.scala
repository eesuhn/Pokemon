package pokemon.model

class Player extends Trainer {
  val name: String = "Player"
  private var _moveIndex: Int = -1

  def moveIndex(index: Int): Unit = this._moveIndex = index

  override def chooseMove(): Move = activePokemon.moves(this._moveIndex)
}
