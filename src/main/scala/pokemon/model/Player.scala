package pokemon.model

class Player extends Trainer {
  val name = "Player"
  private var _selectedMoveIndex: Int = -1

  def setSelectedMoveIndex(index: Int): Unit = _selectedMoveIndex = index

  override def chooseMove(): (Int, Move) = {
    val move = activePokemon.moves(_selectedMoveIndex)
    (_selectedMoveIndex, move)
  }
}
