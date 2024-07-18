package pokemon.model

import scala.util.Random

sealed trait Stat
case object Attack extends Stat
case object Defense extends Stat

abstract class Move {
  val moveName: String
  val accuracy: Int
  val moveType: Type

  /**
    * Calculate if the move hits based on accuracy
    *
    * @return
    */
  def calculateAccuracy(): Boolean = {
    val random = Random
    random.nextInt(100) < this.accuracy
  }
}

/**
  * StatusMove is a move that affects the target's stats
  * based on the move's status and stage
  */
abstract class StatusMove extends Move {
  val status: List[Stat]
  val stage: Int
  val self: Boolean

  def applyEffect(pokemon: Pokemon): Unit = {
    val modifier = calculateStage()

    status.foreach {
      case Attack => pokemon.attack = (pokemon.attack * modifier).toInt
      case Defense => pokemon.defense = (pokemon.defense * modifier).toInt
    }
  }

  def calculateStage(): Double = {
    if (this.stage < 0) 2.0 / (2.0 - this.stage)
    else if (this.stage > 0) (2.0 + this.stage) / 2.0
    else throw new Exception("Adjustment cannot be 0")
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
    * @param target
    * @return 1.0 if both or neither strong/weak, 2.0 if strong, 0.5 if weak
    */
  def calculateModifier(target: Pokemon): Double = {
    val (strong, weak) = target
      .pTypes
      .foldLeft((false, false)) { case ((s, w), t) => (
        s || this.moveType.attackStrongAgainst.contains(t),
        w || this.moveType.attackWeakAgainst.contains(t))
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
    * @param attacker
    * @param target
    * @return
    */
  def calculatePhysicalDamage(attacker: Pokemon, target: Pokemon): Double = {
    val modifier = calculateModifier(target)
    val damage: Double = (
      (2 * attacker.level / 5 + 2) * attacker.attack * basePower / target.defense / 50 + 2
    )
    damage * modifier
  }
}

/**
  * Growl lowers the target's attack by 1 stage
  */
object Growl extends StatusMove {
  val moveName = "Growl"
  val accuracy = 100
  val moveType = Normal
  val status = List(
    Attack
  )
  val stage = -1
  val self = false
}

/**
  * Leer lowers the target's defense by 1 stage
  */
object Leer extends StatusMove {
  val moveName = "Leer"
  val accuracy = 100
  val moveType = Normal
  val status = List(
    Defense
  )
  val stage = -1
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
