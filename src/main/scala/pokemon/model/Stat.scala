package pokemon.model

abstract class Stat {
  var value: Int
  var currentStage: Int = 2

  def increase(stage: Int): Unit = {
    this.currentStage = Math.min(this.currentStage + stage, 6)
  }
  
  def decrease(stage: Int): Unit = {
    this.currentStage = Math.max(this.currentStage - stage, -6)
  }
}

case class Attack(var value: Int) extends Stat
case class Defense(var value: Int) extends Stat
case class Accuracy(var value: Int) extends Stat
