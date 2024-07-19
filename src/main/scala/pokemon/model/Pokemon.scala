package pokemon.model

import scala.util.Random

abstract class Pokemon {
  val pName: String
  var attack: Attack
  var defense: Defense
  var accuracy: Accuracy = Accuracy(100)
  private var _level: Int = 1
  private var _baseHP: Int = initHP
  private var _currentHP: Int = initHP
  private var _pTypes: List[Type] = List()
  private var _moves: List[Move] = List()

  protected def initHP: Int

  def level: Int = _level
  def baseHP: Int = _baseHP
  def currentHP: Int = _currentHP
  def pTypes: List[Type] = _pTypes
  def moves: List[Move] = _moves

  /**
    * Set the types for the Pokemon, at most 2 types can be set
    *
    * @param types
    */
  protected def pTypes(types: List[Type]): Unit = {
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
  protected def moves(moves: List[Move]): Unit = {
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
    this._currentHP = Math.max(this.currentHP - damage, 0)
  }

  def statusAttack(statusMove: StatusMove, target: Pokemon): Unit = {
    if (statusMove.self) statusMove.applyEffects(this)
    else statusMove.applyEffects(target)
  }

  def physicalAttack(physicalMove: PhysicalMove, target: Pokemon): Unit = {
    val damage: Double = physicalMove.calculatePhysicalDamage(this, target)
    target.takeDamage(damage.toInt)
  }
}

class Charmander extends Pokemon {
  val pName = "Charmander"
  var attack = Attack(52)
  var defense = Defense(43)
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
  var attack = Attack(48)
  var defense = Defense(65)
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
  var attack = Attack(49)
  var defense = Defense(49)
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
