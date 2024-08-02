package pokemon.model

import scalafx.scene.input.KeyCode.S

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
  val name: String = "Normal"
  val strongAgainst: List[Type] = List()
  val weakAgainst: List[Type] = List(
    Rock,
    Steel
  )
}

object Fire extends Type {
  val name: String = "Fire"
  val strongAgainst: List[Type] = List(
    Grass,
    Ice,
    Bug,
    Steel
  )
  val weakAgainst: List[Type] = List(
    Fire,
    Water,
    Rock,
    Dragon
  )
}

object Water extends Type {
  val name: String = "Water"
  val strongAgainst: List[Type] = List(
    Fire,
    Rock
  )
  val weakAgainst: List[Type] = List(
    Water,
    Grass,
    Dragon
  )
}

object Electric extends Type {
  val name: String = "Electric"
  val strongAgainst: List[Type] = List(
    Water,
    Steel
  )
  val weakAgainst: List[Type] = List(
    Electric,
    Grass,
    Dragon
  )
}

object Grass extends Type {
  val name: String = "Grass"
  val strongAgainst: List[Type] = List(
    Water,
    Rock
  )
  val weakAgainst: List[Type] = List(
    Fire,
    Grass,
    Poison,
    Bug,
    Steel
  )
}

object Ice extends Type {
  val name: String = "Ice"
  val strongAgainst: List[Type] = List(
    Grass,
    Dragon
  )
  val weakAgainst: List[Type] = List(
    Fire,
    Water,
    Ice,
    Steel
  )
}

object Fighting extends Type {
  val name: String = "Fighting"
  val strongAgainst: List[Type] = List(
    Normal,
    Ice,
    Rock,
    Steel
  )
  val weakAgainst: List[Type] = List(
    Poison,
    Psychic,
    Bug
  )
}

object Poison extends Type {
  val name: String = "Poison"
  val strongAgainst: List[Type] = List(
    Grass
  )
  val weakAgainst: List[Type] = List(
    Poison,
    Rock,
    Steel
  )
}

object Psychic extends Type {
  val name: String = "Psychic"
  val strongAgainst: List[Type] = List(
    Fighting,
    Poison
  )
  val weakAgainst: List[Type] = List(
    Psychic,
    Steel
  )
}

object Bug extends Type {
  val name: String = "Bug"
  val strongAgainst: List[Type] = List(
    Grass,
    Psychic
  )
  val weakAgainst: List[Type] = List(
    Fire,
    Fighting,
    Poison,
    Steel
  )
}

object Rock extends Type {
  val name: String = "Rock"
  val strongAgainst: List[Type] = List(
    Fire,
    Ice,
    Bug
  )
  val weakAgainst: List[Type] = List(
    Fighting,
    Steel
  )
}

object Dragon extends Type {
  val name: String = "Dragon"
  val strongAgainst: List[Type] = List(
    Dragon
  )
  val weakAgainst: List[Type] = List(
    Steel
  )
}

object Steel extends Type {
  val name: String = "Steel"
  val strongAgainst: List[Type] = List(
    Ice,
    Rock
  )
  val weakAgainst: List[Type] = List(
    Fire,
    Water,
    Electric,
    Steel
  )
}
