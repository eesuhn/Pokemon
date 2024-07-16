package pokemon

import pokemon.model.{PhysicalMove, Charmander, Squirtle, Bulbasaur}

object MainApp extends App {
  val charmander = new Charmander
  val squirtle = new Squirtle
  val bulbasaur = new Bulbasaur
  var count = 0

  count += 1
  println(count)
  charmander.physicalAttack(charmander.moves(1), squirtle)
  println(s"Squirtle HP: ${squirtle.currentHP}\n")
  
  count += 1
  println(count)
  charmander.physicalAttack(charmander.moves(1), squirtle)
  println(s"Squirtle HP: ${squirtle.currentHP}\n")
  
  count += 1
  println(count)
  charmander.physicalAttack(charmander.moves(1), squirtle)
  println(s"Squirtle HP: ${squirtle.currentHP}\n")
  
  count += 1
  println(count)
  charmander.physicalAttack(charmander.moves(1), squirtle)
  println(s"Squirtle HP: ${squirtle.currentHP}\n")
  
  count += 1
  println(count)
  charmander.physicalAttack(charmander.moves(1), squirtle)
  println(s"Squirtle HP: ${squirtle.currentHP}\n")
}
