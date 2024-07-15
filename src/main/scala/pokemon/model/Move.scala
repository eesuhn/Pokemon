package pokemon.model

abstract class Move {
  val name: String
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

object Growl extends StatusMove {
  val name = "Growl"
  val accuracy = 100
  val status = "attack"
  val adjustment = -1
  val self = false
}

object Leer extends StatusMove {
  val name = "Leer"
  val accuracy = 100
  val status = "defense"
  val adjustment = -1
  val self = false
}

object Tackle extends PhysicalMove {
  val name = "Tackle"
  val accuracy = 100
  val damage = 40
}

object Scratch extends PhysicalMove {
  val name = "Scratch"
  val accuracy = 100
  val damage = 40
}
