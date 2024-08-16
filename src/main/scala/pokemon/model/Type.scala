package pokemon.model

/**
  * Represents the type for move or Pokemon
  *
  * Move[Main]: Move's type determines how much damage it deals to a Pokemon
  *
  * - strongAgainst: List of types that the move is strong against
  * - weakAgainst: List of types that the move is weak against
  * - noEffectAgainst: List of types that the move has no effect against
  *
  * Pokemon: Pokemon's type determines how much damage it takes from a move
  */
abstract class Type {
  val name: String
  val strongAgainst: List[Type]
  val weakAgainst: List[Type]
  val noEffectAgainst: List[Type] = List()
}

object NoType extends Type {
  val name: String = "NoType"
  val strongAgainst: List[Type] = List()
  val weakAgainst: List[Type] = List()
}

object Normal extends Type {
  val name: String = "Normal"
  val strongAgainst: List[Type] = List()
  val weakAgainst: List[Type] = List(
    Rock,
    Steel
  )
  override val noEffectAgainst: List[Type] = List(
    Ghost
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
    Ground,
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
    Flying
  )
  val weakAgainst: List[Type] = List(
    Electric,
    Grass,
    Dragon
  )
  override val noEffectAgainst: List[Type] = List(
    Ground
  )
}

object Grass extends Type {
  val name: String = "Grass"
  val strongAgainst: List[Type] = List(
    Water,
    Ground,
    Rock
  )
  val weakAgainst: List[Type] = List(
    Fire,
    Grass,
    Poison,
    Flying,
    Bug,
    Dragon,
    Steel
  )
}

object Ice extends Type {
  val name: String = "Ice"
  val strongAgainst: List[Type] = List(
    Grass,
    Ground,
    Flying,
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
    Dark,
    Steel
  )
  val weakAgainst: List[Type] = List(
    Poison,
    Flying,
    Psychic,
    Bug,
    Fairy
  )
  override val noEffectAgainst: List[Type] = List(
    Ghost
  )
}

object Poison extends Type {
  val name: String = "Poison"
  val strongAgainst: List[Type] = List(
    Grass,
    Fairy
  )
  val weakAgainst: List[Type] = List(
    Poison,
    Ground,
    Rock,
    Ghost
  )
}

object Ground extends Type {
  val name: String = "Ground"
  val strongAgainst: List[Type] = List(
    Fire,
    Electric,
    Poison,
    Rock,
    Steel
  )
  val weakAgainst: List[Type] = List(
    Grass,
    Bug
  )
  override val noEffectAgainst: List[Type] = List(
    Flying
  )
}

object Flying extends Type {
  val name: String = "Flying"
  val strongAgainst: List[Type] = List(
    Grass,
    Fighting,
    Bug
  )
  val weakAgainst: List[Type] = List(
    Electric,
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
  override val noEffectAgainst: List[Type] = List(
    Dark
  )
}

object Bug extends Type {
  val name: String = "Bug"
  val strongAgainst: List[Type] = List(
    Grass,
    Psychic,
    Dark
  )
  val weakAgainst: List[Type] = List(
    Fire,
    Fighting,
    Poison,
    Flying,
    Ghost,
    Steel,
    Fairy
  )
}

object Rock extends Type {
  val name: String = "Rock"
  val strongAgainst: List[Type] = List(
    Fire,
    Ice,
    Flying,
    Bug
  )
  val weakAgainst: List[Type] = List(
    Fighting,
    Ground,
    Steel
  )
}

object Ghost extends Type {
  val name: String = "Ghost"
  val strongAgainst: List[Type] = List(
    Psychic,
    Ghost
  )
  val weakAgainst: List[Type] = List(
    Dark
  )
  override val noEffectAgainst: List[Type] = List(
    Normal
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
  override val noEffectAgainst: List[Type] = List(
    Fairy
  )
}

object Dark extends Type {
  val name: String = "Dark"
  val strongAgainst: List[Type] = List(
    Psychic,
    Ghost
  )
  val weakAgainst: List[Type] = List(
    Fighting,
    Dark,
    Fairy
  )
}

object Steel extends Type {
  val name: String = "Steel"
  val strongAgainst: List[Type] = List(
    Ice,
    Rock,
    Fairy
  )
  val weakAgainst: List[Type] = List(
    Fire,
    Water,
    Electric,
    Steel
  )
}

object Fairy extends Type {
  val name: String = "Fairy"
  val strongAgainst: List[Type] = List(
    Fighting,
    Dragon,
    Dark
  )
  val weakAgainst: List[Type] = List(
    Fire,
    Poison,
    Steel
  )
}
