package pokemon.model

import scala.util.Random

abstract class StatEffect {
  private val _stage: Int = initStage
  val accuracy: Option[Int] = None

  protected def initStage: Int
  protected def stat(pokemon: Pokemon): Stat

  def stage: Int = _stage

  def applyEffect(pokemon: Pokemon): Boolean = {
    if (effectAccuracy) {
      stat(pokemon).updateValue(_stage)
      true
    } else false
  }

  /**
    * Check effect accuracy if defined
    *
    * @return
    */
  private def effectAccuracy: Boolean = {
    val random = new Random()
    accuracy match {
      case Some(acc) => random.nextInt(100) < acc
      case None => true
    }
  }

  def statEffectName: String = this
    .getClass
    .getSimpleName
    .replace("Effect", "")
    .toLowerCase
}

case class AttackEffect(
  initStage: Int,
  override val accuracy: Option[Int] = None
) extends StatEffect {
  override protected def stat(pokemon: Pokemon): Stat = pokemon.attack
}

case class DefenseEffect(
  initStage: Int,
  override val accuracy: Option[Int] = None
) extends StatEffect {
  override protected def stat(pokemon: Pokemon): Stat = pokemon.defense
}

case class AccuracyEffect(
  initStage: Int,
  override val accuracy: Option[Int] = None
) extends StatEffect {
  override protected def stat(pokemon: Pokemon): Stat = pokemon.accuracy
}

case class SpeedEffect(
  initStage: Int,
  override val accuracy: Option[Int] = None
) extends StatEffect {
  override protected def stat(pokemon: Pokemon): Stat = pokemon.speed
}

case class CriticalEffect(
  initStage: Int,
  override val accuracy: Option[Int] = None
) extends StatEffect {
  override protected def stat(pokemon: Pokemon): Stat = pokemon.critical
}
