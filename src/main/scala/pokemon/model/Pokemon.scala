package pokemon.model

abstract class Pokemon {
  val name: String
  val maxHP: Int
  var currentHP: Int
  var attack: Int
  var defense: Int
  private var _moves: List[Move] = List()

  def moves: List[Move] = _moves

  /**
    * Set the moves for the Pokemon, only 4 moves can be set
    *
    * @param moves
    */
  def setMoves(moves: List[Move]): Unit = {
    if (moves.length > 4) {
      throw new Exception("Pokemon can only learn 4 moves")
    }
    _moves = moves
  }

  /**
    * Take damage from an attack, HP cannot go below 0
    *
    * @param damage
    */
  def takeDamage(damage: Int): Unit = {
    currentHP -= damage
    currentHP = Math.max(currentHP, 0)
  }
}

class Charmander extends Pokemon {
  val name = "Charmander"
  val maxHP = 39
  var currentHP = maxHP
  var attack = 52
  var defense = 43
  setMoves(List(Growl, Scratch))
}
