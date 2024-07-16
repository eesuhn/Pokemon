package pokemon.model

abstract class Pokemon {
  val pName: String
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

  /**
    * Physical attack on target Pokemon
    *
    * @param move
    * @param target
    */
  def physicalAttack(move: Move, target: Pokemon): Unit = {
    val modifier = calculateModifier(move, target)
    println(s"${pName} used ${move.moveName} on ${target.pName}")
    println(s"\tModifier: ${modifier}")
  }

  /**
    * Calculate modifier for the move based on target's type
    *
    * @param move
    * @param target
    * @return
    */
  def calculateModifier(move: Move, target: Pokemon): Double = {
    (move, target) match {
      case (moveType: Type, targetType: Type) =>
        if (moveType.strongAgainst.contains(targetType.typeName)) 2.0
        else if (moveType.weakAgainst.contains(targetType.typeName)) 0.5
        else 1.0
      case _ => 1.0
    }
  }
}

class Charmander extends Pokemon with Fire {
  val pName = "Charmander"
  val maxHP = 39
  var currentHP = maxHP
  var attack = 52
  var defense = 43
  setMoves(List(
    Growl,
    Scratch,
    Ember
  ))
}

class Squirtle extends Pokemon with Water {
  val pName = "Squirtle"
  val maxHP = 44
  var currentHP = maxHP
  var attack = 48
  var defense = 65
  setMoves(List(
    Tackle,
    WaterGun
  ))
}

class Bulbasaur extends Pokemon with Grass {
  val pName = "Bulbasaur"
  val maxHP = 45
  var currentHP = maxHP
  var attack = 49
  var defense = 49
  setMoves(List(
    Growl,
    Tackle,
    VineWhip
  ))
}
