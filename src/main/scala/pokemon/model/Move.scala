package pokemon.model

import scala.util.Random

import pokemon.macros.Macros

object MoveRegistry {
  private var _moves: List[Move] = List.empty

  def registerMoves(newMoves: List[Move]): Unit = {
    _moves = newMoves ::: _moves
  }

  def moves: List[Move] = _moves

  registerMoves(Macros.registerInstances[Move]("pokemon.model"))
}

abstract class Move {
  val moveName: String
  val accuracy: Int
  val moveType: Type

  protected val _physicalWeight: Double = 0.45
  protected val _statusWeight: Double = 0.55

  private val _maxBasePower: Double = 300.0
  private val _maxStageValue: Int = 6

  def moveEfficiency(): Double = {
    val efficiency = targetMoveEfficiency()
    efficiency * (accuracy / 100.0)
  }

  protected def targetMoveEfficiency(): Double

  /**
    * Return power of the move if it is a PhysicalMove
    *
    * @return
    */
  def movePower: String = this match {
    case physicalMove: PhysicalMove => physicalMove.basePower.toString
    case _ => "-"
  }

  def moveTypeName: String = {
    moveType
      .getClass
      .getSimpleName
      .toLowerCase
      .replace("$", "")
  }

  /**
    * @throws Exception if move category is invalid
    * @return
    */
  def moveCategoryName: String = this match {
    case _: SpecialMove => "special"
    case _: PhysicalMove => "physical"
    case _: StatusMove => "status"
    case _ => throw new Exception(s"Invalid move category: $moveName")
  }

  def calculateMoveAccuracy(): Boolean = {
    val random = Random
    random.nextInt(100) <= accuracy
  }

  /**
    * `_maxBasePower` used to normalize base power
    *
    * @param basePower
    * @return
    */
  protected def physicalMoveScore(basePower: Int): Double = basePower / _maxBasePower

  protected def statusMoveScore(effects: List[StatEffect], targetSelf: Boolean): Double = {
    val scoreSum = effects.map { effect =>
      val weight = statEffectWeight(effect)
      val stageValue = effect.stage
      if (targetSelf) stageValue * weight else -stageValue * weight
    }.sum
    scoreSum / _maxStageValue
  }

  /**
    * Return defined weight of the stat effect
    *
    * @param effect
    * @return
    */
  private def statEffectWeight(effect: StatEffect): Double = effect match {
    case _: AttackEffect => 1.0
    case _: DefenseEffect => 1.0
    case _: AccuracyEffect => 0.8
    case _: SpeedEffect => 0.6
    case _: CriticalHitEffect => 0.8
    case _ => 0.0
  }
}

/**
  * StatusMove is a move that affects the target's stats
  * based on the move's status and stage
  */
trait StatusMove extends Move {
  def effects: List[StatEffect]
  def targetSelf: Boolean

  /**
    * Returns a list of effects (message) applied to the target Pokemon
    *
    * @param pokemon
    * @return
    */
  def applyEffects(pokemon: Pokemon): List[String] = {
    effects.map { effect =>
      effect.applyEffect(pokemon)
      val statName = effect match {
        case _: AttackEffect => "attack"
        case _: DefenseEffect => "defense"
        case _: AccuracyEffect => "accuracy"
        case _: SpeedEffect => "speed"
        case _: CriticalHitEffect => "critical"
      }
      val changeType = if (effect.stage > 0) "rose" else "fell"
      val intensity = Math.abs(effect.stage) match {
        case 1 => ""
        case 2 => "sharply "
        case n if n > 2 => "drastically "
      }
      s"${pokemon.pName}'s $statName $intensity$changeType!"
    }
  }

  override protected def targetMoveEfficiency(): Double = {
    val statusScore = statusMoveScore(effects, targetSelf)
    statusScore * _statusWeight
  }
}

/**
  * PhysicalMove is a move that deals damage to the target
  * based on the user's attack and the target's defense
  */
trait PhysicalMove extends Move {
  def basePower: Int

  private val _controlDamage: Int = 12

  /**
    * Calculate modifier for the move based on target's type
    *
    * - multiply by 2 if move is strong against target type
    * - multiply by 0.5 if move is weak against target type
    * - multiply by 0 if move has no effect against target type
    *
    * @param target
    * @return
    */
  private def calculateModifier(target: Pokemon): Double = {
    target
      .pTypes
      .foldLeft(1.0) { (modifier, t) =>
        modifier * (
          if (moveType.strongAgainst.contains(t)) 2.0
          else if (moveType.weakAgainst.contains(t)) 0.5
          else if (moveType.noEffectAgainst.contains(t)) 0.0
          else 1.0
        )
      }
  }

  /**
    * Return effectiveness (message) of the move based on target's type
    *
    * @param target
    * @return
    */
  private def calculateEffectiveness(target: Pokemon): (Double, String) = {
    val modifier = calculateModifier(target)
    val effectivenessMessage = modifier match {
      case m if m >= 2 => "It's super effective!"
      case m if (m > 0 && m < 1) => "It's not very effective..."
      case m if m == 0.0 => "It doesn't affect..."
      case _ => ""
    }
    (modifier, effectivenessMessage)
  }

  /**
    * Damage = (2 * Level / 5 + 2) * Attack * Power / Defense / 50 + Control
    *
    * - If critical, halve target's defense, double damage
    *
    * @param attacker
    * @param target
    * @return
    */
  def calculatePhysicalDamage(attacker: Pokemon, target: Pokemon): (Double, String) = {
    val (modifier, effectivenessMessage) = calculateEffectiveness(target)

    // Never critical if noEffectAgainst
    val isCritical = if (modifier > 0) attacker.criticalHit.isCritical else false
    val targetDefense = if (isCritical) target.defense.value / 2 else target.defense.value

    val damage: Double = (
      (2 * attacker.level / 5 + 2) * attacker.attack.value * basePower / targetDefense / 50 + _controlDamage
    )
    val finalDamage = if (isCritical) damage * 2 else damage
    val criticalMessage = if (isCritical) "A critical hit!" else ""
    (finalDamage * modifier, List(criticalMessage, effectivenessMessage).filter(_.nonEmpty).mkString(" "))
  }

  override protected def targetMoveEfficiency(): Double = {
    val powerScore = physicalMoveScore(basePower)
    powerScore * _physicalWeight
  }
}

/**
  * SpecialMove is the combination of PhysicalMove and StatusMove
  */
trait SpecialMove extends PhysicalMove with StatusMove {

  override protected def targetMoveEfficiency(): Double = {
    val powerScore = physicalMoveScore(basePower)
    val statusScore = statusMoveScore(effects, targetSelf)
    (powerScore * _physicalWeight) + (statusScore * _statusWeight)
  }
}
