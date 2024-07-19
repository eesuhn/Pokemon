package pokemon.model

abstract class StatEffect {
  val stage: Int

  /**
    * Apply the effect to the Pokemon
    *
    * @param pokemon
    */
  def applyEffect(pokemon: Pokemon): Unit
}

class AttackEffect(
  val stage: Int,
  ) extends StatEffect {

  override def applyEffect(pokemon: Pokemon): Unit = pokemon.attack.value(this.stage)
}

class DefenseEffect(
  val stage: Int,
  ) extends StatEffect {

  override def applyEffect(pokemon: Pokemon): Unit = pokemon.defense.value(this.stage)
}

class AccuracyEffect(
  val stage: Int,
  ) extends StatEffect {

  override def applyEffect(pokemon: Pokemon): Unit = pokemon.accuracy.value(this.stage)
}
