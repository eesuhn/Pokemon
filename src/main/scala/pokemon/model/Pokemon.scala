package pokemon.model

import scala.util.Random

abstract class Pokemon {
  val pName: String
  var attack: Int
  var defense: Int
  var level: Int = 1
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
    * Physical attack on target Pokemon
    *
    * @param physicalMove
    * @param target
    */
  def physicalAttack(physicalMove: PhysicalMove, target: Pokemon): Unit = {
    // if (!calculateAccuracy(physicalMove)) {
    //   println(s"${pName}'s attack missed")
    //   return
    // }

    val modifier: Double = calculateModifier(physicalMove, target)
    println(s"${pName} used ${physicalMove.moveName} on ${target.pName}")

    val damage: Double = calculateDamage(
      physicalMove.basePower,
      this.attack,
      target.defense,
      this.level,
      modifier
    )

    target.takeDamage(damage.toInt)
    println(s"${target.pName} took ${damage.toInt} damage")
  }

  /**
    * Status attack on target Pokemon
    *
    * @param statusMove
    * @param target
    */
  def statusAttack(statusMove: StatusMove, target: Pokemon): Unit = {
    if (statusMove.self) statusMove.applyEffect(this)
    else statusMove.applyEffect(target)
  }

  /**
    * Calculate modifier for the move based on target's type
    *
    * @param move
    * @param target
    * @return 1.0 if both or neither strong/weak, 2.0 if strong, 0.5 if weak
    */
  def calculateModifier(move: Move, target: Pokemon): Double = {
    val (strong, weak) = target
      .pTypes
      .foldLeft((false, false)) { case ((s, w), t) => (
        s || move.moveType.attackStrongAgainst.contains(t),
        w || move.moveType.attackWeakAgainst.contains(t))
      }
    
    if (strong && weak) 1.0
    else if (strong) 2.0
    else if (weak) 0.5
    else 1.0
  }

  /**
    * Calculate damage for the move
    *
    * Damage = (2 * L / 5 + 2) * A * P / D / 50 + 2
    *
    * @param basePower
    * @param attack
    * @param defense
    * @param level
    * @param modifier
    * @return
    */
  def calculateDamage(
    basePower: Int,
    attack: Int,
    defense: Int,
    level: Int,
    modifier: Double
  ): Double = {
    val damage: Double = (
      (2 * level / 5 + 2) * attack * basePower / defense / 50 + 2
    )
    damage * modifier
  }

  /**
    * Calculate if the move hits based on accuracy
    *
    * @param move
    * @return
    */
  def calculateAccuracy(move: Move): Boolean = {
    val random = Random
    random.nextInt(100) < move.accuracy
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
