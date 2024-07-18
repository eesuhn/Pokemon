package pokemon.model

sealed trait Stat
case object Attack extends Stat
case object Defense extends Stat

abstract class Move {
  val moveName: String
  val accuracy: Int
  val moveType: Type
}

/**
  * StatusMove is a move that affects the target's stats
  * based on the move's status and adjustment
  */
abstract class StatusMove extends Move {
  val status: List[Stat]
  val adjustment: Int
  val self: Boolean

  def applyEffect(pokemon: Pokemon): Unit = {
    val modifier = calculateStage(adjustment)

    status.foreach {
      case Attack => pokemon.attack = (pokemon.attack * modifier).toInt
      case Defense => pokemon.defense = (pokemon.attack * modifier).toInt
    }
  }

  def calculateStage(adjustment: Int): Double = {
    if (adjustment < 0) 2 / (2 - adjustment)
    else if (adjustment > 0) (2 + adjustment) / 2
    else throw new Exception("Adjustment cannot be 0")
  }
}

/**
  * PhysicalMove is a move that deals damage to the target
  * based on the user's attack and the target's defense
  */
abstract class PhysicalMove extends Move {
  val basePower: Int
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
  val adjustment = -1
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
  val adjustment = -1
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
