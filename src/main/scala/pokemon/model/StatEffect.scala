package pokemon.model

abstract class StatEffect {
  protected val _stage: Int = initStage

  protected def initStage: Int

  def applyEffect(pokemon: Pokemon): Unit
}

case class AttackEffect(
  initStage: Int,
  ) extends StatEffect {

  override def applyEffect(pokemon: Pokemon): Unit = pokemon.attack.value(this._stage)
}

case class DefenseEffect(
  initStage: Int,
  ) extends StatEffect {

  override def applyEffect(pokemon: Pokemon): Unit = pokemon.defense.value(this._stage)
}

case class AccuracyEffect(
  initStage: Int,
  ) extends StatEffect {

  override def applyEffect(pokemon: Pokemon): Unit = pokemon.accuracy.value(this._stage)
}
