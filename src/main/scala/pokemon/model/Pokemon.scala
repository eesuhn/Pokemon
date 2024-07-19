package pokemon.model

import scala.util.Random

abstract class Pokemon {
  val pName: String
  var attack: Int
  var defense: Int
  var pAccuracy: Int = 100  // TODO
  var level: Int = 1  // TODO
  var maxHP: Int = initHP
  var currentHP: Int = initHP
  private var _pTypes: List[Type] = List()
  private var _moves: List[Move] = List()

  def initHP: Int
  def pTypes: List[Type] = _pTypes
  def moves: List[Move] = _moves

  /**
    * Set the types for the Pokemon, at most 2 types can be set
    *
    * @param types
    */
  def pTypes(types: List[Type]): Unit = {
    if (types.length > 2) {
      throw new Exception("Pokemon can have at most 2 types")
    }
    this._pTypes = types
  }

  /**
    * Set the moves for the Pokemon, at most 4 moves can be set
    *
    * @param moves
    */
  def moves(moves: List[Move]): Unit = {
    if (moves.length > 4) {
      throw new Exception("Pokemon can learn at most 4 moves")
    }
    this._moves = moves
  }

  /**
    * Take damage from an attack, HP cannot go below 0
    *
    * @param damage
    */
  def takeDamage(damage: Int): Unit = {
    this.currentHP = Math.max(this.currentHP - damage, 0)
  }

  /**
    * Status attack on target Pokemon
    *
    * @param statusMove
    * @param target
    */
  def statusAttack(statusMove: StatusMove, target: Pokemon): Unit = {
    if (!statusMove.calculateMoveAccuracy()) {
      println(s"${pName}'s attack missed")
      return
    }

    if (statusMove.self) statusMove.applyEffects(this)
    else statusMove.applyEffects(target)
  }

  /**
    * Physical attack on target Pokemon
    *
    * @param physicalMove
    * @param target
    */
  def physicalAttack(physicalMove: PhysicalMove, target: Pokemon): Unit = {
    if (!physicalMove.calculateMoveAccuracy()) {
      println(s"${pName}'s attack missed")
      return
    }

    val damage: Double = physicalMove.calculatePhysicalDamage(this, target)
    target.takeDamage(damage.toInt)
  }
}

class Charmander extends Pokemon {
  val pName = "Charmander"
  var attack = 52
  var defense = 43
  override def initHP: Int = 39
  pTypes(List(
    Fire
  ))
  moves(List(
    Leer,
    Scratch,
    Ember
  ))
}

class Squirtle extends Pokemon {
  val pName = "Squirtle"
  var attack = 48
  var defense = 65
  override def initHP: Int = 44
  pTypes(List(
    Water
  ))
  moves(List(
    Growl,
    Tackle,
    WaterGun
  ))
}

class Bulbasaur extends Pokemon {
  val pName = "Bulbasaur"
  var attack = 49
  var defense = 49
  override def initHP: Int = 45
  pTypes(List(
    Grass
  ))
  moves(List(
    Growl,
    Tackle,
    VineWhip
  ))
}
