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

object Growl extends StatusMove {
  val moveName: String = "Growl"
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def effects: List[StatEffect] = List(
    AttackEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object Leer extends StatusMove {
  val moveName: String = "Leer"
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def effects: List[StatEffect] = List(
    DefenseEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object Harden extends StatusMove {
  val moveName: String = "Harden"
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def effects: List[StatEffect] = List(
    DefenseEffect(1)
  )
  override def targetSelf: Boolean = true
}

object SwordsDance extends StatusMove {
  val moveName: String = "Swords Dance"
  val accuracy: Int = 90
  val moveType: Normal.type = Normal
  override def effects: List[StatEffect] = List(
    AttackEffect(2)
  )
  override def targetSelf: Boolean = true
}

object Charm extends StatusMove {
  val moveName: String = "Charm"
  val accuracy: Int = 90
  val moveType: Psychic.type = Psychic
  override def effects: List[StatEffect] = List(
    AttackEffect(-1),
    CriticalHitEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object Screech extends StatusMove {
  val moveName: String = "Screech"
  val accuracy: Int = 85
  val moveType: Normal.type = Normal
  override def effects: List[StatEffect] = List(
    DefenseEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object StringShot extends StatusMove {
  val moveName: String = "String Shot"
  val accuracy: Int = 95
  val moveType: Bug.type = Bug
  override def effects: List[StatEffect] = List(
    SpeedEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object Agility extends StatusMove {
  val moveName: String = "Agility"
  val accuracy: Int = 90
  val moveType: Psychic.type = Psychic
  override def effects: List[StatEffect] = List(
    SpeedEffect(2)
  )
  override def targetSelf: Boolean = true
}

object ShellSmash extends StatusMove {
  val moveName: String = "Shell Smash"
  val accuracy: Int = 80
  val moveType: Normal.type = Normal
  override def effects: List[StatEffect] = List(
    AttackEffect(2),
    SpeedEffect(2),
    DefenseEffect(-1)
  )
  override def targetSelf: Boolean = true
}

object QuiverDance extends StatusMove {
  val moveName: String = "Quiver Dance"
  val accuracy: Int = 90
  val moveType: Bug.type = Bug
  override def effects: List[StatEffect] = List(
    AttackEffect(1),
    DefenseEffect(1),
    SpeedEffect(1)
  )
  override def targetSelf: Boolean = true
}

object Tackle extends PhysicalMove {
  val moveName: String = "Tackle"
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def basePower: Int = 40
}

object Scratch extends PhysicalMove {
  val moveName: String = "Scratch"
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def basePower: Int = 40
}

object Pound extends PhysicalMove {
  val moveName: String = "Pound"
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def basePower: Int = 50
}

object Cut extends PhysicalMove {
  val moveName: String = "Cut"
  val accuracy: Int = 95
  val moveType: Normal.type = Normal
  override def basePower: Int = 50
}

object Ember extends PhysicalMove {
  val moveName: String = "Ember"
  val accuracy: Int = 100
  val moveType: Fire.type = Fire
  override def basePower: Int = 50
}

object WaterGun extends PhysicalMove {
  val moveName: String = "Water Gun"
  val accuracy: Int = 100
  val moveType: Water.type = Water
  override def basePower: Int = 40
}

object Spark extends SpecialMove {
  val moveName: String = "Spark"
  val accuracy: Int = 95
  val moveType: Electric.type = Electric
  override def basePower: Int = 65
  override def effects: List[StatEffect] = List(
    SpeedEffect(1)
  )
  override def targetSelf: Boolean = true
}

object VineWhip extends PhysicalMove {
  val moveName: String = "Vine Whip"
  val accuracy: Int = 90
  val moveType: Grass.type = Grass
  override def basePower: Int = 65
}

object IcePunch extends SpecialMove {
  val moveName: String = "Ice Punch"
  val accuracy: Int = 90
  val moveType: Ice.type = Ice
  override def basePower: Int = 75
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object DoubleKick extends SpecialMove {
  val moveName: String = "Double Kick"
  val accuracy: Int = 90
  val moveType: Fighting.type = Fighting
  override def basePower: Int = 50
  override def effects: List[StatEffect] = List(
    CriticalHitEffect(1)
  )
  override def targetSelf: Boolean = true
}

object PoisonFang extends SpecialMove {
  val moveName: String = "Poison Fang"
  val accuracy: Int = 100
  val moveType: Poison.type = Poison
  override def basePower: Int = 50
  override def effects: List[StatEffect] = List(
    DefenseEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object PoisonSting extends SpecialMove {
  val moveName: String = "Poison Sting"
  val accuracy: Int = 90
  val moveType: Poison.type = Poison
  override def basePower: Int = 15
  override def effects: List[StatEffect] = List(
    DefenseEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object XScissor extends PhysicalMove {
  val moveName: String = "X Scissor"
  val accuracy: Int = 90
  val moveType: Bug.type = Bug
  override def basePower: Int = 80
}

object RockTomb extends SpecialMove {
  val moveName: String = "Rock Tomb"
  val accuracy: Int = 95
  val moveType: Rock.type = Rock
  override def basePower: Int = 60
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object IcyWind extends SpecialMove {
  val moveName: String = "Icy Wind"
  val accuracy: Int = 95
  val moveType: Ice.type = Ice
  override def basePower: Int = 55
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object AncientPower extends SpecialMove {
  val moveName: String = "Ancient Power"
  val accuracy: Int = 80
  val moveType: Rock.type = Rock
  override def basePower: Int = 60
  override def effects: List[StatEffect] = List(
    AttackEffect(1),
    DefenseEffect(1),
    SpeedEffect(1)
  )
  override def targetSelf: Boolean = true
}

object PsychoCut extends PhysicalMove {
  val moveName: String = "Psycho Cut"
  val accuracy: Int = 95
  val moveType: Psychic.type = Psychic
  override def basePower: Int = 70
}

object BodySlam extends PhysicalMove {
  val moveName: String = "Body Slam"
  val accuracy: Int = 90
  val moveType: Normal.type = Normal
  override def basePower: Int = 85
}

object BlazeKick extends PhysicalMove {
  val moveName: String = "Blaze Kick"
  val accuracy: Int = 90
  val moveType: Fire.type = Fire
  override def basePower: Int = 85
}

object BulkUp extends StatusMove {
  val moveName: String = "Bulk Up"
  val accuracy: Int = 85
  val moveType: Fighting.type = Fighting
  override def effects: List[StatEffect] = List(
    AttackEffect(1),
    DefenseEffect(1)
  )
  override def targetSelf: Boolean = true
}

object Smokescreen extends StatusMove {
  val moveName: String = "Smokescreen"
  val accuracy: Int = 90
  val moveType: Normal.type = Normal
  override def effects: List[StatEffect] = List(
    AccuracyEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object MuddyWater extends SpecialMove {
  val moveName: String = "Muddy Water"
  val accuracy: Int = 85
  val moveType: Water.type = Water
  override def basePower: Int = 90
  override def effects: List[StatEffect] = List(
    AccuracyEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object FireSpin extends SpecialMove {
  val moveName: String = "Fire Spin"
  val accuracy: Int = 85
  val moveType: Fire.type = Fire
  override def basePower: Int = 35
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object Bubble extends SpecialMove {
  val moveName: String = "Bubble"
  val accuracy: Int = 100
  val moveType: Water.type = Water
  override def basePower: Int = 40
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object PoisonPowder extends StatusMove {
  val moveName: String = "Poison Powder"
  val accuracy: Int = 85
  val moveType: Poison.type = Poison
  override def effects: List[StatEffect] = List(
    DefenseEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object ThunderShock extends SpecialMove {
  val moveName: String = "Thunder Shock"
  val accuracy: Int = 95
  val moveType: Electric.type = Electric
  override def basePower: Int = 40
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object Sandstorm extends StatusMove {
  val moveName: String = "Sandstorm"
  val accuracy: Int = 90
  val moveType: Rock.type = Rock
  override def effects: List[StatEffect] = List(
    SpeedEffect(-3)
  )
  override def targetSelf: Boolean = false
}

object FlareBlitz extends PhysicalMove {
  val moveName: String = "Flare Blitz"
  val accuracy: Int = 90
  val moveType: Fire.type = Fire
  override def basePower: Int = 120
}

object IceFang extends SpecialMove {
  val moveName: String = "Ice Fang"
  val accuracy: Int = 90
  val moveType: Ice.type = Ice
  override def basePower: Int = 65
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object Explosion extends SpecialMove {
  val moveName: String = "Explosion"
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def basePower: Int = 250
  override def effects: List[StatEffect] = List(
    DefenseEffect(-6)
  )
  override def targetSelf: Boolean = true
}

object ArmThrust extends SpecialMove {
  val moveName: String = "Arm Thrust"
  val accuracy: Int = 90
  val moveType: Fighting.type = Fighting
  override def basePower: Int = 45
  override def effects: List[StatEffect] = List(
    AttackEffect(1),
    CriticalHitEffect(1)
  )
  override def targetSelf: Boolean = true
}

object FocusEnergy extends StatusMove {
  val moveName: String = "Focus Energy"
  val accuracy: Int = 90
  val moveType: Normal.type = Normal
  override def effects: List[StatEffect] = List(
    AttackEffect(2)
  )
  override def targetSelf: Boolean = true
}

object SandAttack extends StatusMove {
  val moveName: String = "Sand Attack"
  val accuracy: Int = 75
  val moveType: Normal.type = Normal
  override def effects: List[StatEffect] = List(
    AccuracyEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object DragonBreath extends SpecialMove {
  val moveName: String = "Dragon Breath"
  val accuracy: Int = 80
  val moveType: Dragon.type = Dragon
  override def basePower: Int = 80
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object ShadowForce extends PhysicalMove {
  val moveName: String = "Shadow Force"
  val accuracy: Int = 90
  val moveType: Ghost.type = Ghost
  override def basePower: Int = 120
}

object ScaryFace extends StatusMove {
  val moveName: String = "Scary Face"
  val accuracy: Int = 70
  val moveType: Normal.type = Normal
  override def effects: List[StatEffect] = List(
    SpeedEffect(-2),
    DefenseEffect(-2),
    AttackEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object DragonClaw extends PhysicalMove {
  val moveName: String = "Dragon Claw"
  val accuracy: Int = 100
  val moveType: Dragon.type = Dragon
  override def basePower: Int = 80
}

object WaterPulse extends SpecialMove {
  val moveName: String = "Water Pulse"
  val accuracy: Int = 90
  val moveType: Water.type = Water
  override def basePower: Int = 60
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object HydroPump extends SpecialMove {
  val moveName: String = "Hydro Pump"
  val accuracy: Int = 80
  val moveType: Water.type = Water
  override def basePower: Int = 110
  override def effects: List[StatEffect] = List(
    DefenseEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object AquaRing extends StatusMove {
  val moveName: String = "Aqua Ring"
  val accuracy: Int = 85
  val moveType: Water.type = Water
  override def effects: List[StatEffect] = List(
    DefenseEffect(6)
  )
  override def targetSelf: Boolean = true
}

object MeteorMash extends PhysicalMove {
  val moveName: String = "Meteor Mash"
  val accuracy: Int = 90
  val moveType: Steel.type = Steel
  override def basePower: Int = 90
}

object ZenHeadbutt extends PhysicalMove {
  val moveName: String = "Zen Headbutt"
  val accuracy: Int = 90
  val moveType: Psychic.type = Psychic
  override def basePower: Int = 80
}

object IronDefense extends StatusMove {
  val moveName: String = "Iron Defense"
  val accuracy: Int = 95
  val moveType: Steel.type = Steel
  override def effects: List[StatEffect] = List(
    DefenseEffect(2)
  )
  override def targetSelf: Boolean = true
}

object AuraSphere extends SpecialMove {
  val moveName: String = "Aura Sphere"
  val accuracy: Int = 90
  val moveType: Fighting.type = Fighting
  override def basePower: Int = 80
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object MetalClaw extends PhysicalMove {
  val moveName: String = "Metal Claw"
  val accuracy: Int = 95
  val moveType: Steel.type = Steel
  override def basePower: Int = 70
}

object RoarOfTime extends SpecialMove {
  val moveName: String = "Roar Of Time"
  val accuracy: Int = 90
  val moveType: Dragon.type = Dragon
  override def basePower: Int = 150
  override def effects: List[StatEffect] = List(
    SpeedEffect(-6)
  )
  override def targetSelf: Boolean = false
}

object IronTail extends PhysicalMove {
  val moveName: String = "Iron Tail"
  val accuracy: Int = 75
  val moveType: Steel.type = Steel
  override def basePower: Int = 100
}

object Earthquake extends SpecialMove {
  val moveName: String = "Earthquake"
  val accuracy: Int = 90
  val moveType: Normal.type = Normal
  override def basePower: Int = 100
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object SpacialRend extends SpecialMove {
  val moveName: String = "Spacial Rend"
  val accuracy: Int = 90
  val moveType: Dragon.type = Dragon
  override def basePower: Int = 100
  override def effects: List[StatEffect] = List(
    CriticalHitEffect(1),
    AttackEffect(1)
  )
  override def targetSelf: Boolean = true
}

object IceBeam extends SpecialMove {
  val moveName: String = "Ice Beam"
  val accuracy: Int = 90
  val moveType: Ice.type = Ice
  override def basePower: Int = 90
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1),
    AttackEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object BoltStrike extends PhysicalMove {
  val moveName: String = "Bolt Strike"
  val accuracy: Int = 80
  val moveType: Electric.type = Electric
  override def basePower: Int = 130
}

object Thunderbolt extends SpecialMove {
  val moveName: String = "Thunderbolt"
  val accuracy: Int = 80
  val moveType: Electric.type = Electric
  override def basePower: Int = 90
  override def effects: List[StatEffect] = List(
    SpeedEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object FlameWheel extends PhysicalMove {
  val moveName: String = "Flame Wheel"
  val accuracy: Int = 80
  val moveType: Fire.type = Fire
  override def basePower: Int = 80
}

object LeafBlade extends PhysicalMove {
  val moveName: String = "Leaf Blade"
  val accuracy: Int = 90
  val moveType: Grass.type = Grass
  override def basePower: Int = 90
}

object RazorLeaf extends PhysicalMove {
  val moveName: String = "Razor Leaf"
  val accuracy: Int = 90
  val moveType: Grass.type = Grass
  override def basePower: Int = 75
}

object ThunderWave extends StatusMove {
  val moveName: String = "Thunder Wave"
  val accuracy: Int = 90
  val moveType: Electric.type = Electric
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1),
    DefenseEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object SludgeBomb extends SpecialMove {
  val moveName: String = "Sludge Bomb"
  val accuracy: Int = 90
  val moveType: Poison.type = Poison
  override def basePower: Int = 80
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object Toxic extends SpecialMove {
  val moveName: String = "Toxic"
  val accuracy: Int = 85
  val moveType: Poison.type = Poison
  override def basePower: Int = 40
  override def effects: List[StatEffect] = List(
    DefenseEffect(-1),
    CriticalHitEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object Psybeam extends SpecialMove {
  val moveName: String = "Psybeam"
  val accuracy: Int = 90
  val moveType: Psychic.type = Psychic
  override def basePower: Int = 65
  override def effects: List[StatEffect] = List(
    CriticalHitEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object HyperBeam extends SpecialMove {
  val moveName: String = "Hyper Beam"
  val accuracy: Int = 90
  val moveType: Normal.type = Normal
  override def basePower: Int = 150
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = true
}

object ExtremeSpeed extends StatusMove {
  val moveName: String = "Extreme Speed"
  val accuracy: Int = 90
  val moveType: Normal.type = Normal
  override def effects: List[StatEffect] = List(
    SpeedEffect(6)
  )
  override def targetSelf: Boolean = true
}

object AuroraBeam extends SpecialMove {
  val moveName: String = "Aurora Beam"
  val accuracy: Int = 90
  val moveType: Ice.type = Ice
  override def basePower: Int = 65
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object SheerCold extends SpecialMove {
  val moveName: String = "Sheer Cold"
  val accuracy: Int = 50
  val moveType: Ice.type = Ice
  override def basePower: Int = 50
  override def effects: List[StatEffect] = List(
    SpeedEffect(-6)
  )
  override def targetSelf: Boolean = false
}

object BulletPunch extends PhysicalMove {
  val moveName: String = "Bullet Punch"
  val accuracy: Int = 65
  val moveType: Steel.type = Steel
  override def basePower: Int = 120
}

object QuickAttack extends PhysicalMove {
  val moveName: String = "Quick Attack"
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def basePower: Int = 55
}

object GigaImpact extends SpecialMove {
  val moveName: String = "Giga Impact"
  val accuracy: Int = 90
  val moveType: Normal.type = Normal
  override def basePower: Int = 170
  override def effects: List[StatEffect] = List(
    DefenseEffect(-2)
  )
  override def targetSelf: Boolean = true
}

object Eruption extends SpecialMove {
  val moveName: String = "Eruption"
  val accuracy: Int = 80
  val moveType: Fire.type = Fire
  override def basePower: Int = 150
  override def effects: List[StatEffect] = List(
    DefenseEffect(-1)
  )
  override def targetSelf: Boolean = true
}

object Outrage extends SpecialMove {
  val moveName: String = "Outrage"
  val accuracy: Int = 90
  val moveType: Dragon.type = Dragon
  override def basePower: Int = 130
  override def effects: List[StatEffect] = List(
    SpeedEffect(3)
  )
  override def targetSelf: Boolean = true
}

object BlueFlare extends SpecialMove {
  val moveName: String = "Blue Flare"
  val accuracy: Int = 85
  val moveType: Fire.type = Fire
  override def basePower: Int = 130
  override def effects: List[StatEffect] = List(
    DefenseEffect(-3)
  )
  override def targetSelf: Boolean = false
}

object ShadowBall extends SpecialMove {
  val moveName: String = "Shadow Ball"
  val accuracy: Int = 90
  val moveType: Ghost.type = Ghost
  override def basePower: Int = 80
  override def effects: List[StatEffect] = List(
    AttackEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object Lick extends SpecialMove {
  val moveName: String = "Lick"
  val accuracy: Int = 90
  val moveType: Ghost.type = Ghost
  override def basePower: Int = 30
  override def effects: List[StatEffect] = List(
    DefenseEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object ConfuseRay extends StatusMove {
  val moveName: String = "Confuse Ray"
  val accuracy: Int = 85
  val moveType: Ghost.type = Ghost
  override def effects: List[StatEffect] = List(
    AccuracyEffect(-2),
    SpeedEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object FirePunch extends SpecialMove {
  val moveName: String = "Fire Punch"
  val accuracy: Int = 90
  val moveType: Fire.type = Fire
  override def basePower: Int = 75
  override def effects: List[StatEffect] = List(
    DefenseEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object ThunderPunch extends SpecialMove {
  val moveName: String = "Thunder Punch"
  val accuracy: Int = 90
  val moveType: Electric.type = Electric
  override def basePower: Int = 75
  override def effects: List[StatEffect] = List(
    AttackEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object Curse extends StatusMove {
  val moveName: String = "Curse"
  val accuracy: Int = 100
  val moveType: Ghost.type = Ghost
  override def effects: List[StatEffect] = List(
    SpeedEffect(-1),
    AttackEffect(2)
  )
  override def targetSelf: Boolean = true
}
