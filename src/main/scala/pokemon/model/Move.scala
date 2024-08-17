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
  val moveType: Type

  private var _accuracy: Int = 100

  protected val _physicalWeight: Double = 0.45
  protected val _statusWeight: Double = 0.55

  protected val _maxBasePower: Double = 300.0
  private val _stageDiffNorm: Double = 6.0
  private val _efficiencyNorm: Double = 2.5

  def accuracy: Int = _accuracy

  /**
    * @param value
    *
    * @throws Exception if value is not between 0 and 100
    */
  protected def accuracy_=(value: Int): Unit = {
    if (value < 0 || value > 100) throw new Exception("Invalid accuracy value")
    _accuracy = value
  }

  /**
    * Consider `_efficiencyNorm` to normalize score
    *
    * @return
    */
  def moveEfficiency(): Double = {
    val efficiency = targetMoveEfficiency() * _efficiencyNorm
    efficiency * (_accuracy / 100.0)
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
    random.nextInt(100) <= _accuracy
  }

  /**
    * `_maxBasePower` used to normalize base power
    *
    * @param basePower
    * @return
    */
  protected def physicalMoveScore(basePower: Int): Double = basePower / _maxBasePower

  /**
    * Calculate score of the status move based on the effects
    *
    * - Weight of the effect is defined by the stat effect
    * - Stage value is multiplied by the weight
    * - Individual effect accuracy is considered
    *
    * @param effects
    * @param targetSelf
    * @return
    */
  protected def statusMoveScore(effects: List[StatEffect], targetSelf: Boolean): Double = {
    val scoreSum = effects.map { effect =>
      val effectAccuracy = effect.accuracy match {
        case Some(acc) => acc
        case None => 100
      }
      val weight = statEffectWeight(effect)
      val stageValue = effect.stage
      (if (targetSelf) stageValue * weight else -stageValue * weight) * (effectAccuracy / 100.0)
    }.sum
    scoreSum / _stageDiffNorm
  }

  /**
    * Return defined weight of the stat effect
    *
    * @param effect
    * @return
    */
  private def statEffectWeight(effect: StatEffect): Double = effect match {
    case _: AttackEffect => 1.0
    case _: DefenseEffect => 0.8
    case _: AccuracyEffect => 0.8
    case _: SpeedEffect => 0.6
    case _: CriticalEffect => 0.8
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
    effects.flatMap { effect =>
      val (applied, message) = effect.applyEffect(pokemon) match {
        case true =>
          val changeType = if (effect.stage > 0) "rose" else "fell"
          val intensity = Math.abs(effect.stage) match {
            case 1 => ""
            case 2 => "sharply "
            case n if n > 2 => "drastically "
          }
          (true, s"${pokemon.pName}'s ${effect.statEffectName} $intensity$changeType!")
        case false =>
          (false, "")
      }

      if (applied) Some(message) else None
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
  private var _basePower: Int = 0
  private val _controlDamage: Int = 12

  def basePower: Int = _basePower

  /**
    * @param value
    *
    * @throws Exception if value is not between 0 and `_maxBasePower`
    */
  protected def basePower_=(value: Int): Unit = {
    if (value < 0 || value > _maxBasePower) throw new Exception(f"Invalid base power value: $value")
    _basePower = value
  }

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
    val isCritical = if (modifier > 0) attacker.critical.isCritical else false
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

object Struggle extends SpecialMove {
  val moveName: String = "Struggle"
  val moveType: Type = NoType
  basePower_=(50)
  val effects: List[StatEffect] = List(
    DefenseEffect(-2)
  )
  val targetSelf: Boolean = true
}

object Charm extends StatusMove {
  val moveName: String = "Charm"
  val moveType: Type = Fairy
  accuracy_=(90)
  val effects: List[StatEffect] = List(
    AttackEffect(-1),
    CriticalEffect(-1)
  )
  val targetSelf: Boolean = false
}

object ThunderShock extends SpecialMove {
  val moveName: String = "Thunder Shock"
  val moveType: Type = Electric
  accuracy_=(95)
  basePower_=(40)
  val effects: List[StatEffect] = List(
    SpeedEffect(-2, Some(20))
  )
  val targetSelf: Boolean = false
}

object ThunderWave extends StatusMove {
  val moveName: String = "Thunder Wave"
  val moveType: Type = Electric
  accuracy_=(90)
  val effects: List[StatEffect] = List(
    SpeedEffect(-2)
  )
  val targetSelf: Boolean = false
}

object TailWhip extends StatusMove {
  val moveName: String = "Tail Whip"
  val moveType: Type = Normal
  accuracy_=(95)
  val effects: List[StatEffect] = List(
    DefenseEffect(-1)
  )
  val targetSelf: Boolean = false
}

object Tackle extends PhysicalMove {
  val moveName: String = "Tackle"
  val moveType: Type = Normal
  basePower_=(40)
}

object FocusEnergy extends StatusMove {
  val moveName: String = "Focus Energy"
  val moveType: Type = Normal
  accuracy_=(85)
  val effects: List[StatEffect] = List(
    CriticalEffect(1)
  )
  val targetSelf: Boolean = true
}

object Agility extends StatusMove {
  val moveName: String = "Agility"
  val moveType: Type = Psychic
  accuracy_=(95)
  val effects: List[StatEffect] = List(
    SpeedEffect(2)
  )
  val targetSelf: Boolean = true
}

object FirePunch extends SpecialMove {
  val moveName: String = "Fire Punch"
  val moveType: Type = Fire
  accuracy_=(85)
  basePower_=(75)
  val effects: List[StatEffect] = List(
    DefenseEffect(-4, Some(15))
  )
  val targetSelf: Boolean = false
}

object IcePunch extends SpecialMove {
  val moveName: String = "Ice Punch"
  val moveType: Type = Ice
  accuracy_=(85)
  basePower_=(75)
  val effects: List[StatEffect] = List(
    SpeedEffect(-4, Some(15))
  )
  val targetSelf: Boolean = false
}

object BodySlam extends PhysicalMove {
  val moveName: String = "Body Slam"
  val moveType: Type = Normal
  accuracy_=(95)
  basePower_=(85)
}

object BellyDrum extends StatusMove {
  val moveName: String = "Belly Drum"
  val moveType: Type = Normal
  accuracy_=(90)
  val effects: List[StatEffect] = List(
    AttackEffect(3, Some(80)),
    DefenseEffect(-3, Some(20))
  )
  val targetSelf: Boolean = true
}

object Bite extends SpecialMove {
  val moveName: String = "Bite"
  val moveType: Type = Dark
  accuracy_=(90)
  basePower_=(60)
  val effects: List[StatEffect] = List(
    AttackEffect(-2, Some(10))
  )
  val targetSelf: Boolean = false
}

object Screech extends StatusMove {
  val moveName: String = "Screech"
  val moveType: Type = Normal
  accuracy_=(95)
  val effects: List[StatEffect] = List(
    DefenseEffect(-2)
  )
  val targetSelf: Boolean = false
}

object MeteorMash extends SpecialMove {
  val moveName: String = "Meteor Mash"
  val moveType: Type = Steel
  accuracy_=(90)
  basePower_=(90)
  val effects: List[StatEffect] = List(
    AttackEffect(1, Some(50)),
    CriticalEffect(1, Some(50))
  )
  val targetSelf: Boolean = true
}

object ZenHeadbutt extends SpecialMove {
  val moveName: String = "Zen Headbutt"
  val moveType: Type = Psychic
  accuracy_=(90)
  basePower_=(80)
  val effects: List[StatEffect] = List(
    AttackEffect(-2, Some(20))
  )
  val targetSelf: Boolean = false
}

object BulletPunch extends PhysicalMove {
  val moveName: String = "Bullet Punch"
  val moveType: Type = Steel
  basePower_=(40)
}

object ShadowForce extends SpecialMove {
  val moveName: String = "Shadow Force"
  val moveType: Type = Ghost
  accuracy_=(90)
  basePower_=(120)
  val effects: List[StatEffect] = List(
    DefenseEffect(-2, Some(80))
  )
  val targetSelf: Boolean = false
}

object DragonClaw extends PhysicalMove {
  val moveName: String = "Dragon Claw"
  val moveType: Type = Dragon
  basePower_=(90)
}

object DragonBreath extends SpecialMove {
  val moveName: String = "Dragon Breath"
  val moveType: Type = Dragon
  accuracy_=(90)
  basePower_=(60)
  val effects: List[StatEffect] = List(
    DefenseEffect(-2, Some(50))
  )
  val targetSelf: Boolean = false
}

object AncientPower extends SpecialMove {
  val moveName: String = "Ancient Power"
  val moveType: Type = Rock
  basePower_=(60)
  val effects: List[StatEffect] = List(
    AttackEffect(1, Some(20)),
    DefenseEffect(1, Some(20)),
    SpeedEffect(1, Some(20))
  )
  val targetSelf: Boolean = true
}

object Growl extends StatusMove {
  val moveName: String = "Growl"
  val moveType: Type = Normal
  accuracy_=(90)
  val effects: List[StatEffect] = List(
    AttackEffect(-1)
  )
  val targetSelf: Boolean = false
}

object VineWhip extends PhysicalMove {
  val moveName: String = "Vine Whip"
  val moveType: Type = Grass
  accuracy_=(95)
  basePower_=(45)
}

object RazorLeaf extends SpecialMove {
  val moveName: String = "Razor Leaf"
  val moveType: Type = Grass
  accuracy_=(90)
  basePower_=(55)
  val effects: List[StatEffect] = List(
    CriticalEffect(2, Some(20))
  )
  val targetSelf: Boolean = true
}

object Scratch extends PhysicalMove {
  val moveName: String = "Scratch"
  val moveType: Type = Normal
  basePower_=(40)
}

object Ember extends SpecialMove {
  val moveName: String = "Ember"
  val moveType: Type = Fire
  accuracy_=(90)
  basePower_=(40)
  val effects: List[StatEffect] = List(
    DefenseEffect(-1, Some(20))
  )
  val targetSelf: Boolean = false
}

object Smokescreen extends StatusMove {
  val moveName: String = "Smokescreen"
  val moveType: Type = Normal
  accuracy_=(90)
  val effects: List[StatEffect] = List(
    AccuracyEffect(-1)
  )
  val targetSelf: Boolean = false
}

object WaterGun extends SpecialMove {
  val moveName: String = "Water Gun"
  val moveType: Type = Water
  accuracy_=(90)
  basePower_=(40)
  val effects: List[StatEffect] = List(
    SpeedEffect(-1, Some(20))
  )
  val targetSelf: Boolean = false
}

object SeedBomb extends PhysicalMove {
  val moveName: String = "Seed Bomb"
  val moveType: Type = Grass
  basePower_=(80)
}

object SolarBeam extends SpecialMove {
  val moveName: String = "Solar Beam"
  val moveType: Type = Grass
  accuracy_=(85)
  basePower_=(120)
  val effects: List[StatEffect] = List(
    DefenseEffect(-2, Some(60))
  )
  val targetSelf: Boolean = false
}

object PetalDance extends SpecialMove {
  val moveName: String = "Petal Dance"
  val moveType: Type = Grass
  accuracy_=(90)
  basePower_=(120)
  val effects: List[StatEffect] = List(
    DefenseEffect(-2, Some(30)),
    CriticalEffect(2, Some(60))
  )
  val targetSelf: Boolean = true
}

object Flamethrower extends SpecialMove {
  val moveName: String = "Flamethrower"
  val moveType: Type = Fire
  accuracy_=(90)
  basePower_=(90)
  val effects: List[StatEffect] = List(
    DefenseEffect(-2, Some(20))
  )
  val targetSelf: Boolean = false
}

object AirSlash extends SpecialMove {
  val moveName: String = "Air Slash"
  val moveType: Type = Flying
  accuracy_=(95)
  basePower_=(75)
  val effects: List[StatEffect] = List(
    SpeedEffect(2, Some(30))
  )
  val targetSelf: Boolean = true
}

object ScaryFace extends StatusMove {
  val moveName: String = "Scary Face"
  val moveType: Type = Normal
  accuracy_=(85)
  val effects: List[StatEffect] = List(
    AttackEffect(-1),
    DefenseEffect(-1),
    SpeedEffect(-1)
  )
  val targetSelf: Boolean = false
}

object HydroPump extends SpecialMove {
  val moveName: String = "Hydro Pump"
  val moveType: Type = Water
  accuracy_=(85)
  basePower_=(120)
  val effects: List[StatEffect] = List(
    DefenseEffect(-2, Some(80))
  )
  val targetSelf: Boolean = false
}

object ShellSmash extends StatusMove {
  val moveName: String = "Shell Smash"
  val moveType: Type = Water
  val effects: List[StatEffect] = List(
    AttackEffect(2, Some(70)),
    DefenseEffect(-1)
  )
  val targetSelf: Boolean = true
}

object AquaTail extends PhysicalMove {
  val moveName: String = "Aqua Tail"
  val moveType: Type = Water
  accuracy_=(95)
  basePower_=(90)
}

object ShadowBall extends SpecialMove {
  val moveName: String = "Shadow Ball"
  val moveType: Type = Ghost
  accuracy_=(90)
  basePower_=(80)
  val effects: List[StatEffect] = List(
    DefenseEffect(-2, Some(40))
  )
  val targetSelf: Boolean = false
}

object SludgeBomb extends SpecialMove {
  val moveName: String = "Sludge Bomb"
  val moveType: Type = Poison
  accuracy_=(95)
  basePower_=(90)
  val effects: List[StatEffect] = List(
    SpeedEffect(-2, Some(30))
  )
  val targetSelf: Boolean = false
}

object DarkPulse extends SpecialMove {
  val moveName: String = "Dark Pulse"
  val moveType: Type = Dark
  accuracy_=(90)
  basePower_=(80)
  val effects: List[StatEffect] = List(
    AttackEffect(-2, Some(40))
  )
  val targetSelf: Boolean = false
}

object DreamEater extends SpecialMove {
  val moveName: String = "Dream Eater"
  val moveType: Type = Psychic
  accuracy_=(95)
  basePower_=(100)
  val effects: List[StatEffect] = List(
    AttackEffect(-4, Some(30))
  )
  val targetSelf: Boolean = false
}

object XScissor extends SpecialMove {
  val moveName: String = "X Scissor"
  val moveType: Type = Bug
  basePower_=(80)
  val effects: List[StatEffect] = List(
    DefenseEffect(-2, Some(60))
  )
  val targetSelf: Boolean = false
}

object RockSlide extends SpecialMove {
  val moveName: String = "Rock Slide"
  val moveType: Type = Rock
  accuracy_=(90)
  basePower_=(90)
  val effects: List[StatEffect] = List(
    SpeedEffect(-2, Some(30))
  )
  val targetSelf: Boolean = false
}

object Earthquake extends PhysicalMove {
  val moveName: String = "Earthquake"
  val moveType: Type = Ground
  basePower_=(100)
}

object IronDefense extends StatusMove {
  val moveName: String = "Iron Defense"
  val moveType: Type = Steel
  accuracy_=(90)
  val effects: List[StatEffect] = List(
    DefenseEffect(2)
  )
  val targetSelf: Boolean = true
}

object SkyAttack extends SpecialMove {
  val moveName: String = "Sky Attack"
  val moveType: Type = Flying
  accuracy_=(90)
  basePower_=(130)
  val effects: List[StatEffect] = List(
    AttackEffect(-2, Some(50)),
    DefenseEffect(-2, Some(50))
  )
  val targetSelf: Boolean = false
}

object IronHead extends SpecialMove {
  val moveName: String = "Iron Head"
  val moveType: Type = Steel
  accuracy_=(90)
  basePower_=(80)
  val effects: List[StatEffect] = List(
    DefenseEffect(-2, Some(30))
  )
  val targetSelf: Boolean = false
}

object Crunch extends SpecialMove {
  val moveName: String = "Crunch"
  val moveType: Type = Dark
  accuracy_=(90)
  basePower_=(80)
  val effects: List[StatEffect] = List(
    DefenseEffect(-1, Some(30))
  )
  val targetSelf: Boolean = false
}

object CalmMind extends StatusMove {
  val moveName: String = "Calm Mind"
  val moveType: Type = Psychic
  val effects: List[StatEffect] = List(
    AttackEffect(1),
    DefenseEffect(1)
  )
  val targetSelf: Boolean = true
}

object Pound extends PhysicalMove {
  val moveName: String = "Pound"
  val moveType: Type = Normal
  basePower_=(50)
}

object DefenseCurl extends StatusMove {
  val moveName: String = "Defense Curl"
  val moveType: Type = Normal
  accuracy_=(90)
  val effects: List[StatEffect] = List(
    DefenseEffect(1)
  )
  val targetSelf: Boolean = true
}

object Confusion extends SpecialMove {
  val moveName: String = "Confusion"
  val moveType: Type = Psychic
  accuracy_=(90)
  basePower_=(60)
  val effects: List[StatEffect] = List(
    AccuracyEffect(-2, Some(70))
  )
  val targetSelf: Boolean = false
}

object SwordsDance extends StatusMove {
  val moveName: String = "Swords Dance"
  val moveType: Type = Normal
  val effects: List[StatEffect] = List(
    AttackEffect(2)
  )
  val targetSelf: Boolean = true
}

object SandAttack extends StatusMove {
  val moveName: String = "Sand Attack"
  val moveType: Type = Ground
  accuracy_=(90)
  val effects: List[StatEffect] = List(
    AccuracyEffect(-1)
  )
  val targetSelf: Boolean = false
}

object DragonDance extends StatusMove {
  val moveName: String = "Dragon Dance"
  val moveType: Type = Dragon
  accuracy_=(95)
  val effects: List[StatEffect] = List(
    AttackEffect(1),
    SpeedEffect(1)
  )
  val targetSelf: Boolean = true
}

object RoarOfTime extends SpecialMove {
  val moveName: String = "Roar Of Time"
  val moveType: Type = Dragon
  accuracy_=(90)
  basePower_=(150)
  val effects: List[StatEffect] = List(
    SpeedEffect(-1)
  )
  val targetSelf: Boolean = true
}

object FlashCannon extends SpecialMove {
  val moveName: String = "Flash Cannon"
  val moveType: Type = Steel
  accuracy_=(90)
  basePower_=(80)
  val effects: List[StatEffect] = List(
    DefenseEffect(-2)
  )
  val targetSelf: Boolean = false
}

object FusionBolt extends PhysicalMove {
  val moveName: String = "Fusion Bolt"
  val moveType: Type = Electric
  basePower_=(100)
  accuracy_=(95)
}

object BoltStrike extends SpecialMove {
  val moveName: String = "Bolt Strike"
  val moveType: Type = Electric
  basePower_=(130)
  accuracy_=(85)
  val effects: List[StatEffect] = List(
    SpeedEffect(-4, Some(65))
  )
  val targetSelf: Boolean = false
}

object Hurricane extends SpecialMove {
  val moveName: String = "Hurricane"
  val moveType: Type = Flying
  basePower_=(110)
  accuracy_=(75)
  val effects: List[StatEffect] = List(
    AccuracyEffect(-3, Some(30))
  )
  val targetSelf: Boolean = false
}

object BugBite extends PhysicalMove {
  val moveName: String = "Bug Bite"
  val moveType: Type = Bug
  basePower_=(60)
}

object Leer extends StatusMove {
  val moveName: String = "Leer"
  val moveType: Type = Normal
  val effects: List[StatEffect] = List(
    DefenseEffect(-1)
  )
  val targetSelf: Boolean = false
}

object Peck extends SpecialMove {
  val moveName: String = "Peck"
  val moveType: Type = Flying
  accuracy_=(90)
  basePower_=(35)
  val effects: List[StatEffect] = List(
    CriticalEffect(1)
  )
  val targetSelf: Boolean = true
}

object MachPunch extends PhysicalMove {
  val moveName: String = "Mach Punch"
  val moveType: Type = Fighting
  basePower_=(50)
}

object FlameWheel extends SpecialMove {
  val moveName: String = "Flame Wheel"
  val moveType: Type = Fire
  accuracy_=(90)
  basePower_=(60)
  val effects: List[StatEffect] = List(
    DefenseEffect(-2, Some(25))
  )
  val targetSelf: Boolean = false
}

object VCreate extends SpecialMove {
  val moveName: String = "V Create"
  val moveType: Type = Fire
  basePower_=(160)
  accuracy_=(95)
  val effects: List[StatEffect] = List(
    AttackEffect(-1),
    DefenseEffect(-1),
    SpeedEffect(-1)
  )
  val targetSelf: Boolean = true
}

object FlameCharge extends SpecialMove {
  val moveName: String = "Flame Charge"
  val moveType: Type = Fire
  basePower_=(50)
  val effects: List[StatEffect] = List(
    SpeedEffect(1)
  )
  val targetSelf: Boolean = true
}

object AquaRing extends StatusMove {
  val moveName: String = "Aqua Ring"
  val moveType: Type = Water
  val effects: List[StatEffect] = List(
    DefenseEffect(3)
  )
  val targetSelf: Boolean = true
}

object IceBeam extends SpecialMove {
  val moveName: String = "Ice Beam"
  val moveType: Type = Ice
  accuracy_=(95)
  basePower_=(90)
  val effects: List[StatEffect] = List(
    SpeedEffect(-2, Some(70))
  )
  val targetSelf: Boolean = false
}

object Eruption extends SpecialMove {
  val moveName: String = "Eruption"
  val moveType: Type = Fire
  accuracy_=(85)
  basePower_=(130)
  val effects: List[StatEffect] = List(
    DefenseEffect(-3, Some(30))
  )
  val targetSelf: Boolean = false
}

object Explosion extends SpecialMove {
  val moveName: String = "Explosion"
  val moveType: Type = Normal
  basePower_=(300)
  val effects: List[StatEffect] = List(
    DefenseEffect(-6)
  )
  val targetSelf: Boolean = true
}

object MetalClaw extends SpecialMove {
  val moveName: String = "Metal Claw"
  val moveType: Type = Steel
  accuracy_=(90)
  basePower_=(60)
  val effects: List[StatEffect] = List(
    AttackEffect(1, Some(20))
  )
  val targetSelf: Boolean = true
}

object SuckerPunch extends SpecialMove {
  val moveName: String = "Sucker Punch"
  val moveType: Type = Dark
  accuracy_=(95)
  basePower_=(70)
  val effects: List[StatEffect] = List(
    CriticalEffect(-1, Some(30)),
    AccuracyEffect(-1, Some(30))
  )
  val targetSelf: Boolean = false
}

object ConfuseRay extends StatusMove {
  val moveName: String = "Confuse Ray"
  val moveType: Type = Ghost
  accuracy_=(80)
  val effects: List[StatEffect] = List(
    AccuracyEffect(-3)
  )
  val targetSelf: Boolean = false
}

object Astonish extends SpecialMove {
  val moveName: String = "Astonish"
  val moveType: Type = Ghost
  basePower_=(30)
  val effects: List[StatEffect] = List(
    AccuracyEffect(-1, Some(30))
  )
  val targetSelf: Boolean = false
}

object ShadowPunch extends SpecialMove {
  val moveName: String = "Shadow Punch"
  val moveType: Type = Ghost
  basePower_=(60)
  val effects: List[StatEffect] = List(
    DefenseEffect(-1, Some(20))
  )
  val targetSelf: Boolean = false
}

object Harden extends StatusMove {
  val moveName: String = "Harden"
  val moveType: Type = Normal
  accuracy_=(95)
  val effects: List[StatEffect] = List(
    DefenseEffect(1)
  )
  val targetSelf: Boolean = true
}

object HammerArm extends SpecialMove {
  val moveName: String = "Hammer Arm"
  val moveType: Type = Fighting
  accuracy_=(90)
  basePower_=(80)
  val effects: List[StatEffect] = List(
    SpeedEffect(-1, Some(80))
  )
  val targetSelf: Boolean = false
}

object DynamicPunch extends SpecialMove {
  val moveName: String = "Dynamic Punch"
  val moveType: Type = Fighting
  accuracy_=(80)
  basePower_=(100)
  val effects: List[StatEffect] = List(
    CriticalEffect(2),
    AccuracyEffect(-2)
  )
  val targetSelf: Boolean = true
}

object ShadowClaw extends SpecialMove {
  val moveName: String = "Shadow Claw"
  val moveType: Type = Ghost
  accuracy_=(95)
  basePower_=(70)
  val effects: List[StatEffect] = List(
    CriticalEffect(1, Some(50))
  )
  val targetSelf: Boolean = true
}

object PowderSnow extends SpecialMove {
  val moveName: String = "Powder Snow"
  val moveType: Type = Ice
  accuracy_=(95)
  basePower_=(40)
  val effects: List[StatEffect] = List(
    SpeedEffect(-1, Some(15))
  )
  val targetSelf: Boolean = false
}

object Surf extends PhysicalMove {
  val moveName: String = "Surf"
  val moveType: Type = Water
  accuracy_=(95)
  basePower_=(90)
}

object Blizzard extends SpecialMove {
  val moveName: String = "Blizzard"
  val moveType: Type = Ice
  accuracy_=(70)
  basePower_=(110)
  val effects: List[StatEffect] = List(
    SpeedEffect(-2)
  )
  val targetSelf: Boolean = false
}

object IceShard extends PhysicalMove {
  val moveName: String = "Ice Shard"
  val moveType: Type = Ice
  accuracy_=(95)
  basePower_=(60)
}

object WoodHammer extends SpecialMove {
  val moveName: String = "Wood Hammer"
  val moveType: Type = Grass
  accuracy_=(90)
  basePower_=(120)
  val effects: List[StatEffect] = List(
    DefenseEffect(-2, Some(30))
  )
  val targetSelf: Boolean = true
}

object LeafStorm extends SpecialMove {
  val moveName: String = "Leaf Storm"
  val moveType: Type = Grass
  accuracy_=(90)
  basePower_=(130)
  val effects: List[StatEffect] = List(
    AttackEffect(-2, Some(50))
  )
  val targetSelf: Boolean = false
}

object ForcePalm extends SpecialMove {
  val moveName: String = "Force Palm"
  val moveType: Type = Fighting
  accuracy_=(90)
  basePower_=(60)
  val effects: List[StatEffect] = List(
    SpeedEffect(-2, Some(30))
  )
  val targetSelf: Boolean = false
}
