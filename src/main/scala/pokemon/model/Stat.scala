package pokemon.model

import scala.util.Random

abstract class Stat {
  protected var _value: Int = initValue
  protected val _baseValue: Int = initValue
  private var _currentStage: Int = 0

  protected def initValue: Int
  protected def minValue: Int = 0

  protected def minStage: Int = -6
  protected def maxStage: Int = 6

  def value: Int = _value
  def baseValue: Int = _baseValue

  def updateValue(value: Int): Unit

  protected def updateValueByStage(stage: Int): Unit = {
    changeStage(stage)
    val calculated = (_baseValue * calculateStage(_currentStage)).toInt
    _value = Math.max(calculated, minValue)
  }

  /**
    * Limit the stage between -6 and 6 (Default)
    *
    * @param stage
    */
  private def changeStage(stage: Int): Unit = {
    _currentStage = Math.min(Math.max(_currentStage + stage, minStage), maxStage)
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

  def statScore(): Double = _baseValue * baseStatWeight()

  private def baseStatWeight(): Double = this match {
    case _: Health => 0.8
    case _: Attack => 1.0
    case _: Defense => 0.8
    case _: Speed => 0.6
    case _ => 0.0
  }
}

case class Attack(
  initValue: Int
) extends Stat {

  override protected def minValue: Int = 20
  override def updateValue(stage: Int): Unit = updateValueByStage(stage)
}

case class Defense(
  initValue: Int
) extends Stat {

  override protected def minValue: Int = 20
  override def updateValue(stage: Int): Unit = updateValueByStage(stage)
}

case class Accuracy(
  initValue: Int
) extends Stat {

  override protected def minValue: Int = 60
  override def updateValue(stage: Int): Unit = updateValueByStage(stage)
}

case class Speed(
  initValue: Int
) extends Stat {

  override protected def minValue: Int = 10
  override def updateValue(stage: Int): Unit = updateValueByStage(stage)
}

case class CriticalHit(
  initValue: Int = 1
) extends Stat {

  def isCritical: Boolean = {
    val random = new Random()
    val probability = _value.toDouble / 10.0
    random.nextDouble() <= probability
  }

  /**
    * Hard limit critical hit ratio to not exceed 6
    *
    * @param value
    */
  override def updateValue(value: Int): Unit = {
    _value = Math.min(Math.max(_value + value, minValue), 6)
  }
}

case class Health(
  initValue: Int
) extends Stat {

  /**
    * Hard limit health to not exceed the base value
    *
    * @param value
    */
  override def updateValue(value: Int): Unit = {
    _value = Math.min(Math.max(_value + value, minValue), _baseValue)
  }
}
