package pokemon.model

import scala.util.Random

abstract class Move {
  val moveName: String
  val accuracy: Int
  val moveType: Type

  def calculateMoveAccuracy(): Boolean = {
    val random = Random
    random.nextInt(100) <= this.accuracy
  }
}

/**
  * StatusMove is a move that affects the target's stats
  * based on the move's status and stage
  */
abstract class StatusMove extends Move {
  val effects: List[StatEffect]
  val self: Boolean

  /**
    * Apply effects of the move to the target Pokemon
    *
    * @param pokemon
    */
  def applyEffects(pokemon: Pokemon): Unit = {
    this.effects.foreach(_.applyEffect(pokemon))
  }
}

/**
  * PhysicalMove is a move that deals damage to the target
  * based on the user's attack and the target's defense
  */
abstract class PhysicalMove extends Move {
  val basePower: Int

  /**
    * Calculate modifier for the move based on target's type
    *
    * - 1.0 if both or neither strong/weak
    * - 2.0 if strong
    * - 0.5 if weak
    *
    * @param target
    * @return
    */
  private def calculateModifier(target: Pokemon): Double = {
    val (strong, weak) = target
      .pTypes
      .foldLeft((false, false)) { case ((s, w), t) => (
        s || this.moveType.strongAgainst.contains(t),
        w || this.moveType.weakAgainst.contains(t))
      }

    if (strong && weak) 1.0
    else if (strong) 2.0
    else if (weak) 0.5
    else 1.0
  }

  /**
    * Calculate damage for the move
    *
    * Damage = (2 * Level / 5 + 2) * Attack * Power / Defense / 50 + 2
    *
    * @param attacker
    * @param target
    * @return
    */
  def calculatePhysicalDamage(attacker: Pokemon, target: Pokemon): Double = {
    val modifier = calculateModifier(target)
    val damage: Double = (
      (2 * attacker.level / 5 + 2) * attacker.attack.value * basePower / target.defense.value / 50 + 2
    )
    damage * modifier
  }
}

object Growl extends StatusMove {
  val moveName = "Growl"
  val accuracy = 100
  val moveType = Normal
  val effects = List(
    AttackEffect(-1)
  )
  val self = false
}

object Leer extends StatusMove {
  val moveName = "Leer"
  val accuracy = 100
  val moveType = Normal
  val effects = List(
    DefenseEffect(-1)
  )
  val self = false
}

object Tackle extends PhysicalMove {
  val moveName = "Tackle"
  val accuracy = 100
  val moveType = Normal
  val basePower = 40
}

object Scratch extends PhysicalMove {
  val moveName = "Scratch"
  val accuracy = 100
  val moveType = Normal
  val basePower = 40
}

object Pound extends PhysicalMove {
  val moveName = "Pound"
  val accuracy = 100
  val moveType = Normal
  val basePower = 40
}

object Cut extends PhysicalMove {
  val moveName = "Cut"
  val accuracy = 95
  val moveType = Normal
  val basePower = 50
}

object Ember extends PhysicalMove {
  val moveName = "Ember"
  val accuracy = 100
  val moveType = Fire
  val basePower = 40
}

object WaterGun extends PhysicalMove {
  val moveName = "Water Gun"
  val accuracy = 100
  val moveType = Water
  val basePower = 40
}

object Spark extends PhysicalMove {
  val moveName = "Spark"
  val accuracy = 100
  val moveType = Electric
  val basePower = 65
}

object VineWhip extends PhysicalMove {
  val moveName = "Vine Whip"
  val accuracy = 100
  val moveType = Grass
  val basePower = 45
}

object IcePunch extends PhysicalMove {
  val moveName = "Ice Punch"
  val accuracy = 100
  val moveType = Ice
  val basePower = 75
}

object DoubleKick extends PhysicalMove {
  val moveName = "Double Kick"
  val accuracy = 100
  val moveType = Normal
  val basePower = 30
}
