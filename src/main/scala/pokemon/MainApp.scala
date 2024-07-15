package pokemon

import pokemon.model.{Charmander, Squirtle}

object MainApp extends App {
  val charmander = new Charmander
  println(s"${charmander.pName} has ${charmander.currentHP} HP")
  charmander.moves.foreach(move => {
    println(s"\t${move.moveName}")
  })

  val squirtle = new Squirtle
  println(s"${squirtle.pName} has ${squirtle.currentHP} HP")
  squirtle.moves.foreach(move => {
    println(s"\t${move.moveName}")
  })

  charmander.physicalAttack(charmander.moves(2), squirtle)
  squirtle.physicalAttack(squirtle.moves(1), charmander)
}
