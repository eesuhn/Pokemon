package pokemon.model

class Player extends Trainer {
  val name: String = "Player"
  private var _selectedMoveIndex: Int = -1

  def setSelectedMoveIndex(index: Int): Unit = this._selectedMoveIndex = index

  override def chooseMove(): Move = activePokemon.moves(this._selectedMoveIndex)
}
