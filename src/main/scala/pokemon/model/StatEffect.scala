package pokemon.model

abstract class StatEffect {
  private val _stage: Int = initStage

  protected def initStage: Int
  protected def stat(pokemon: Pokemon): Stat

  def stage: Int = _stage

  def applyEffect(pokemon: Pokemon): Unit = stat(pokemon).value(_stage)
}

case class AttackEffect(
  initStage: Int
) extends StatEffect {
  override protected def stat(pokemon: Pokemon): Stat = pokemon.attack
}

case class DefenseEffect(
  initStage: Int
) extends StatEffect {
  override protected def stat(pokemon: Pokemon): Stat = pokemon.defense
}

case class AccuracyEffect(
  initStage: Int
) extends StatEffect {
  override protected def stat(pokemon: Pokemon): Stat = pokemon.accuracy
}

case class SpeedEffect(
  initStage: Int
) extends StatEffect {
  override protected def stat(pokemon: Pokemon): Stat = pokemon.speed
}

case class CriticalHitEffect(
  initStage: Int
) extends StatEffect {
  override protected def stat(pokemon: Pokemon): Stat = pokemon.criticalHit
}
