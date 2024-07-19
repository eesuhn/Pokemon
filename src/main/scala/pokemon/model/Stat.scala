package pokemon.model

abstract class Stat {
  private var _baseValue: Int = initValue
  private var _value: Int = initValue
  private var currentStage: Int = 0

  def initValue: Int
  def value: Int = _value

  /**
    * Update the staga, and then update the value
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
    * @param stage
    */
  def changeStage(stage: Int): Unit = {
    this.currentStage = Math.min(Math.max(this.currentStage + stage, -6), 6)
  }

  /**
    * Calculate the stage adjustment
    * 
    * If stage < 0, return 2.0 / (2.0 - stage)
    * 
    * If stage > 0, return (2.0 + stage) / 2.0
    *
    * @return
    */
  def calculateStage(stage: Int): Double = {
    if (stage < 0) 2.0 / (2.0 - stage)
    else if (stage > 0) (2.0 + stage) / 2.0
    else 1.0
  }

  /**
    * Update the value of the stat
    */
  def updateValue(): Unit = {
    this._value = (this._baseValue * this.calculateStage(this.currentStage)).toInt
  }
}

case class Attack(initValue: Int) extends Stat
case class Defense(initValue: Int) extends Stat
case class Accuracy(initValue: Int) extends Stat
