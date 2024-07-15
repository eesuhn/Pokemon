package pokemon

import pokemon.model.{Charmander}

object MainApp extends App {
  val charmander = new Charmander
  println(s"${charmander.name} has ${charmander.currentHP} HP")
  charmander.moves.foreach(move => {
    println(s"\t${move.name}")
  })
}
