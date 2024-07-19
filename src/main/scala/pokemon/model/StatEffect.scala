package pokemon.model

abstract class StatEffect {
  val stage: Int

  /**
    * Apply the effect to the Pokemon
    *
    * @param pokemon
    */
  def applyEffect(pokemon: Pokemon): Unit

  /**
    * Calculate the stage adjustment
    * 
    * If stage < 0, return 2.0 / (2.0 - stage)
    * 
    * If stage > 0, return (2.0 + stage) / 2.0
    *
    * @return
    */
  def calculateStage(): Double = {
    if (this.stage < 0) 2.0 / (2.0 - this.stage)
    else if (this.stage > 0) (2.0 + this.stage) / 2.0
    else throw new Exception("Adjustment cannot be 0")
  }
}

class AttackEffect(
  val stage: Int,
  ) extends StatEffect {

  override def applyEffect(pokemon: Pokemon): Unit = {
    pokemon.attack = (pokemon.attack * super.calculateStage()).toInt
  }
}

class DefenseEffect(
  val stage: Int,
  ) extends StatEffect {

  override def applyEffect(pokemon: Pokemon): Unit = {
    pokemon.defense = (pokemon.defense * super.calculateStage()).toInt
  }
}

class AccuracyEffect(
  val stage: Int,
  ) extends StatEffect {

  override def applyEffect(pokemon: Pokemon): Unit = {
    pokemon.pAccuracy = (pokemon.pAccuracy * super.calculateStage()).toInt
  }
}
