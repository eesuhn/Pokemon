package pokemon.model

abstract class Type {
  val name: String
  val attackStrongAgainst: List[Type]
  val attackWeakAgainst: List[Type]
}

object Normal extends Type {
  val name = "Normal"
  val attackStrongAgainst: List[Type] = List()
  val attackWeakAgainst: List[Type] = List()
}

object Fire extends Type {
  val name = "Fire"
  val attackStrongAgainst: List[Type] = List(
    Grass,
    Ice
  )
  val attackWeakAgainst: List[Type] = List(
    Fire,
    Water
  )
}

object Water extends Type {
  val name = "Water"
  val attackStrongAgainst: List[Type] = List(
    Fire
  )
  val attackWeakAgainst: List[Type] = List(
    Water,
    Grass
  )
}

object Electric extends Type {
  val name = "Electric"
  val attackStrongAgainst: List[Type] = List(
    Water
  )
  val attackWeakAgainst: List[Type] = List(
    Electric,
    Grass
  )
}

object Grass extends Type {
  val name = "Grass"
  val attackStrongAgainst: List[Type] = List(
    Water
  )
  val attackWeakAgainst: List[Type] = List(
    Fire,
    Grass
  )
}

object Ice extends Type {
  val name = "Ice"
  val attackStrongAgainst: List[Type] = List(
    Grass
  )
  val attackWeakAgainst: List[Type] = List(
    Fire,
    Water,
    Ice
  )
}

object Fighting extends Type {
  val name = "Fighting"
  val attackStrongAgainst: List[Type] = List(
    Normal,
    Ice
  )
  val attackWeakAgainst: List[Type] = List()
}
