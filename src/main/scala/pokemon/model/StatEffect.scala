package pokemon.model

abstract class StatEffect {
  private val _stage: Int = stage

  protected def stage: Int
  protected def stat(pokemon: Pokemon): Stat

  def applyEffect(pokemon: Pokemon): Unit = stat(pokemon).value(_stage)
}

case class AttackEffect(
  stage: Int
  ) extends StatEffect {
  override protected def stat(pokemon: Pokemon): Stat = pokemon.attack
}

case class DefenseEffect(
  stage: Int
  ) extends StatEffect {
  override protected def stat(pokemon: Pokemon): Stat = pokemon.defense
}

case class AccuracyEffect(
  stage: Int
  ) extends StatEffect {
  override protected def stat(pokemon: Pokemon): Stat = pokemon.accuracy
}

case class SpeedEffect(
  stage: Int
  ) extends StatEffect {
  override protected def stat(pokemon: Pokemon): Stat = pokemon.speed
}
