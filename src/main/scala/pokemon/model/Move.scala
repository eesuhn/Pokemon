package pokemon.model

abstract class Move {
  val moveName: String
  val accuracy: Int
}

abstract class StatusMove extends Move {
  val status: String
  val adjustment: Int
  val self: Boolean
}

abstract class PhysicalMove extends Move {
  val damage: Int
}

object Growl extends StatusMove with Normal {
  val moveName = "Growl"
  val accuracy = 100
  val status = "attack"
  val adjustment = -1
  val self = false
}

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
  val damage = 40
}

object Scratch extends PhysicalMove with Normal {
  val moveName = "Scratch"
  val accuracy = 100
  val damage = 40
}

object Ember extends PhysicalMove with Fire {
  val moveName = "Ember"
  val accuracy = 100
  val damage = 40
}

object WaterGun extends PhysicalMove with Water {
  val moveName = "Water Gun"
  val accuracy = 100
  val damage = 40
}

object VineWhip extends PhysicalMove with Grass {
  val moveName = "Vine Whip"
  val accuracy = 100
  val damage = 45
}
