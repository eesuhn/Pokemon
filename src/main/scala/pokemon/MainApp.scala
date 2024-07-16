package pokemon

import pokemon.model.{Charmander, Squirtle, Bulbasaur}

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

  val bulbasaur = new Bulbasaur
  println(s"${bulbasaur.pName} has ${bulbasaur.currentHP} HP")
  bulbasaur.moves.foreach(move => {
    println(s"\t${move.moveName}")
  })

  charmander.physicalAttack(charmander.moves(2), squirtle)
  squirtle.physicalAttack(squirtle.moves(1), bulbasaur)
  bulbasaur.physicalAttack(bulbasaur.moves(2), charmander)
}
