package pokemon.model

trait Type {
  val typeName: String
  val strongAgainst: List[String]
  val weakAgainst: List[String]
}

trait Normal extends Type {
  val typeName = "Normal"
  val strongAgainst = List()
  val weakAgainst = List()
}

trait Fire extends Type {
  val typeName = "Fire"
  val strongAgainst = List(
    "Grass"
  )
  val weakAgainst = List(
    "Water"
  )
}

trait Water extends Type {
  val typeName = "Water"
  val strongAgainst = List(
    "Fire"
  )
  val weakAgainst = List(
    "Grass"
  )
}

trait Grass extends Type {
  val typeName = "Grass"
  val strongAgainst = List(
    "Water"
  )
  val weakAgainst = List(
    "Fire"
  )
}
