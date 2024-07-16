package pokemon

import pokemon.model.{PhysicalMove, Charmander, Squirtle, Bulbasaur}

object MainApp extends App {
  val charmander = new Charmander
  val squirtle = new Squirtle
  val bulbasaur = new Bulbasaur

  charmander.physicalAttack(charmander.moves(1).asInstanceOf[PhysicalMove], squirtle)
  println(s"Squirtle HP: ${squirtle.currentHP}")
  // squirtle.physicalAttack(squirtle.moves(0).asInstanceOf[PhysicalMove], bulbasaur)
  // bulbasaur.physicalAttack(bulbasaur.moves(1).asInstanceOf[PhysicalMove], charmander)
}
