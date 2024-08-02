package pokemon.model

import scala.util.Random

abstract class Stat {
  private val _baseValue: Int = initValue
  protected var _value: Int = initValue
  private var _currentStage: Int = 0

  protected def initValue: Int
  protected def minValue: Int = 0

  def value: Int = _value

  /**
    * Update the stage count, which update the value of the stat
    *
    * @param stage
    */
  def value(stage: Int): Unit = {
    changeStage(stage)
    updateValue()
  }

  /**
    * Change the stage of the stat
    *
    * Limit the stage between -6 and 6
    *
    * @param stage
    */
  private def changeStage(stage: Int): Unit = {
    _currentStage = Math.min(Math.max(_currentStage + stage, -6), 6)
  }

  /**
    * Calculate the stage adjustment
    *
    * - If stage < 0, return 2.0 / (2.0 - stage)
    * - If stage > 0, return (2.0 + stage) / 2.0
    *
    * @return
    */
  private def calculateStage(stage: Int): Double = {
    if (stage < 0) 2.0 / (2.0 - stage)
    else if (stage > 0) (2.0 + stage) / 2.0
    else 1.0
  }

  /**
    * Update the value of the stat
    *
    * Hard limit the value to minValue
    */
  private def updateValue(): Unit = {
    val calculated = (_baseValue * calculateStage(_currentStage)).toInt
    _value = Math.max(calculated, minValue)
  }
}

case class Attack(
  initValue: Int
) extends Stat {
  override protected def minValue: Int = 20
}

case class Defense(
  initValue: Int
) extends Stat {
  override protected def minValue: Int = 20
}

case class Accuracy(
  initValue: Int
) extends Stat {
  override protected def minValue: Int = 60
}

case class Speed(
  initValue: Int
) extends Stat {
  override protected def minValue: Int = 10
}

case class CriticalHit(
  initValue: Int = 1
) extends Stat {

  override protected def minValue: Int = 1

  private def probability: Double = _value.toDouble / 16.0

  def isCritical: Boolean = {
    val random = new Random()
    random.nextDouble() < probability
  }

  override def value(stage: Int): Unit = {
    _value = Math.min(Math.max(_value + stage, 1), 6)
    println(_value)
  }
}
