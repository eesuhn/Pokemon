package pokemon.model

abstract class Move {
  val moveName: String
  val accuracy: Int
}

/**
  * StatusMove is a move that affects the target's stats
  * based on the move's status and adjustment
  */
abstract class StatusMove extends Move {
  val status: String
  val adjustment: Int
  val self: Boolean
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
object Growl extends StatusMove with Normal {
  val moveName = "Growl"
  val accuracy = 100
  val status = "attack"
  val adjustment = -1
  val self = false
}

/**
  * Leer lowers the target's defense by 1 stage
  */
object Leer extends StatusMove with Normal {
  val moveName = "Leer"
  val accuracy = 100
  val status = "defense"
  val adjustment = -1
  val self = false
}

object Tackle extends PhysicalMove with Normal {
  val moveName = "Tackle"
  val accuracy = 100
  val basePower = 40
}

object Scratch extends PhysicalMove with Normal {
  val moveName = "Scratch"
  val accuracy = 100
  val basePower = 40
}

object Ember extends PhysicalMove with Fire {
  val moveName = "Ember"
  val accuracy = 100
  val basePower = 40
}

object WaterGun extends PhysicalMove with Water {
  val moveName = "Water Gun"
  val accuracy = 100
  val basePower = 40
}

object VineWhip extends PhysicalMove with Grass {
  val moveName = "Vine Whip"
  val accuracy = 100
  val basePower = 45
}
