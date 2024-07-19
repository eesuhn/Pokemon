package pokemon.model

import scalafx.scene.input.KeyCode.B

/**
  * Represents the type for move or Pokemon
  *
  * Move[Main]: Move's type determines how much damage it deals to a Pokemon
  *
  * - strongAgainst: List of types that the move is strong against
  * - weakAgainst: List of types that the move is weak against
  *
  * Pokemon: Pokemon's type determines how much damage it takes from a move
  */
abstract class Type {
  val name: String
  val strongAgainst: List[Type]
  val weakAgainst: List[Type]
}

object Normal extends Type {
  val name = "Normal"
  val strongAgainst: List[Type] = List()
  val weakAgainst: List[Type] = List(
    Rock
  )
}

object Fire extends Type {
  val name = "Fire"
  val strongAgainst: List[Type] = List(
    Grass,
    Ice,
    Bug
  )
  val weakAgainst: List[Type] = List(
    Fire,
    Water,
    Rock
  )
}

object Water extends Type {
  val name = "Water"
  val strongAgainst: List[Type] = List(
    Fire,
    Rock
  )
  val weakAgainst: List[Type] = List(
    Water,
    Grass
  )
}

object Electric extends Type {
  val name = "Electric"
  val strongAgainst: List[Type] = List(
    Water
  )
  val weakAgainst: List[Type] = List(
    Electric,
    Grass
  )
}

object Grass extends Type {
  val name = "Grass"
  val strongAgainst: List[Type] = List(
    Water,
    Rock
  )
  val weakAgainst: List[Type] = List(
    Fire,
    Grass,
    Poison,
    Bug
  )
}

object Ice extends Type {
  val name = "Ice"
  val strongAgainst: List[Type] = List(
    Grass
  )
  val weakAgainst: List[Type] = List(
    Fire,
    Water,
    Ice
  )
}

object Fighting extends Type {
  val name = "Fighting"
  val strongAgainst: List[Type] = List(
    Normal,
    Ice,
    Rock
  )
  val weakAgainst: List[Type] = List(
    Poison,
    Psychic,
    Bug
  )
}

object Poison extends Type {
  val name = "Poison"
  val strongAgainst: List[Type] = List(
    Grass
  )
  val weakAgainst: List[Type] = List(
    Poison,
    Rock
  )
}

object Psychic extends Type {
  val name = "Psychic"
  val strongAgainst: List[Type] = List(
    Fighting,
    Poison
  )
  val weakAgainst: List[Type] = List(
    Psychic
  )
}

object Bug extends Type {
  val name = "Bug"
  val strongAgainst: List[Type] = List(
    Grass,
    Psychic
  )
  val weakAgainst: List[Type] = List(
    Fire,
    Fighting,
    Poison
  )
}

object Rock extends Type {
  val name = "Rock"
  val strongAgainst: List[Type] = List(
    Fire,
    Ice,
    Bug
  )
  val weakAgainst: List[Type] = List(
    Fighting
  )
}
