package pokemon.model

import scala.util.Random

abstract class Pokemon {
  val pName: String
  val attack: Attack
  val defense: Defense
  val accuracy: Accuracy = Accuracy(100)
  val speed: Speed
  private var _level: Int = 5
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

  private def statusAttack(statusMove: StatusMove, target: Pokemon): Unit = {
    if (statusMove.self) statusMove.applyEffects(this)
    else statusMove.applyEffects(target)
  }

  private def physicalAttack(physicalMove: PhysicalMove, target: Pokemon): Unit = {
    val damage: Double = physicalMove.calculatePhysicalDamage(this, target)
    target.takeDamage(damage.toInt)
  }

  /**
    * Attack the target Pokemon with the move
    * 
    * - PhysicalMove: Calculate damage based on the user's attack and the target's defense
    * - StatusMove: Apply effects of the move to the target Pokemon
    *
    * @param move
    * @param target
    */
  def attack(move: Move, target: Pokemon): Unit = {
    if (move.isInstanceOf[PhysicalMove]) physicalAttack(move.asInstanceOf[PhysicalMove], target)
    if (move.isInstanceOf[StatusMove]) statusAttack(move.asInstanceOf[StatusMove], target)
  }
}

class Charmander extends Pokemon {
  val pName = "Charmander"
  val attack = Attack(52)
  val defense = Defense(43)
  val speed = Speed(65)
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
  val attack = Attack(48)
  val defense = Defense(65)
  val speed = Speed(43)
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
  val attack = Attack(49)
  val defense = Defense(49)
  val speed = Speed(45)
  override def initHP: Int = 45
  pTypes(List(
    Grass,
    Poison
  ))
  moves(List(
    Growl,
    Tackle,
    VineWhip
  ))
}

class Geodude extends Pokemon {
  val pName = "Geodude"
  val attack = Attack(80)
  val defense = Defense(100)
  val speed = Speed(20)
  override def initHP: Int = 40
  pTypes(List(
    Rock
  ))
  moves(List(
    Tackle,
    RockTomb
  ))
}
