package pokemon.model

import scala.util.Random

import pokemon.macros.Macros

object MoveRegistry {
  private var _moves: List[Move] = List.empty

  def registerMoves(newMoves: List[Move]): Unit = {
    this._moves = newMoves ::: this._moves
  }

  def moves: List[Move] = this._moves

  registerMoves(Macros.registerInstances[Move]("pokemon.model"))
}

abstract class Move {
  val moveName: String
  val moveDesc: String
  val accuracy: Int
  val moveType: Type

  def calculateMoveAccuracy(): Boolean = {
    val random = Random
    random.nextInt(100) <= this.accuracy
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
    * Apply effects of the move to the target Pokemon
    *
    * @param pokemon
    */
  def applyEffects(pokemon: Pokemon): Unit = {
    this.effects.foreach(_.applyEffect(pokemon))
  }
}

/**
  * PhysicalMove is a move that deals damage to the target
  * based on the user's attack and the target's defense
  */
trait PhysicalMove extends Move {
  def basePower: Int

  /**
    * Calculate modifier for the move based on target's type
    *
    * - *2 if move is strong against target type
    * - *0.5 if move is weak against target type
    *
    * @param target
    * @return
    */
  private def calculateModifier(target: Pokemon): Double = {
    target
      .pTypes
      .foldLeft(1.0) { (modifier, t) =>
        modifier * (
          if (this.moveType.strongAgainst.contains(t)) 2.0
          else if (this.moveType.weakAgainst.contains(t)) 0.5
          else 1.0
        )
      }
  }

  /**
    * Calculate damage for the move
    *
    * Damage = (2 * Level / 5 + 2) * Attack * Power / Defense / 50 + 2
    *
    * @param attacker
    * @param target
    * @return
    */
  def calculatePhysicalDamage(attacker: Pokemon, target: Pokemon): Double = {
    val modifier = calculateModifier(target)
    val damage: Double = (
      (2 * attacker.level / 5 + 2) * attacker.attack.value * basePower / target.defense.value / 50 + 2
    )
    damage * modifier
  }
}

object Growl extends StatusMove {
  val moveName: String = "Growl"
  val moveDesc: String = "The user growls in an endearing way, making the opposing Pokemon less wary. Lower opponent's Attack stat by 1 stage."
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def effects: List[AttackEffect] = List(
    AttackEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object Leer extends StatusMove {
  val moveName: String = "Leer"
  val moveDesc: String = "The user gives the target an intimidating leer. Lower opponent's Defense stat by 1 stage."
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def effects: List[DefenseEffect] = List(
    DefenseEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object Meditate extends StatusMove {
  val moveName: String = "Meditate"
  val moveDesc: String = "The user meditates to awaken the power deep within its body and raise its Attack stat by 1 stage."
  val accuracy: Int = 100
  val moveType: Psychic.type = Psychic
  override def effects: List[AttackEffect] = List(
    AttackEffect(1)
  )
  override def targetSelf: Boolean = true
}

object Harden extends StatusMove {
  val moveName: String = "Harden"
  val moveDesc: String = "The user stiffens all the muscles in its body to raise its Defense stat by 1 stage."
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def effects: List[DefenseEffect] = List(
    DefenseEffect(1)
  )
  override def targetSelf: Boolean = true
}

object SwordsDance extends StatusMove {
  val moveName: String = "Swords Dance"
  val moveDesc: String = "A frenetic dance to uplift the fighting spirit. Sharply raises the user's Attack stat by 2 stages."
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def effects: List[AttackEffect] = List(
    AttackEffect(2)
  )
  override def targetSelf: Boolean = true
}

object Charm extends StatusMove {
  val moveName: String = "Charm"
  val moveDesc: String = "The user gazes at the target rather charmingly, making it less wary. Sharply lowers the target's Attack stat by 2 stages."
  val accuracy: Int = 100
  val moveType: Psychic.type = Psychic
  override def effects: List[AttackEffect] = List(
    AttackEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object Screech extends StatusMove {
  val moveName: String = "Screech"
  val moveDesc: String = "An earsplitting screech harshly lowers the target's Defense stat by 2 stages."
  val accuracy: Int = 85
  val moveType: Normal.type = Normal
  override def effects: List[DefenseEffect] = List(
    DefenseEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object StringShot extends StatusMove {
  val moveName: String = "String Shot"
  val moveDesc: String = "The opposing Pokemon are bound with silk blown from the user's mouth that harshly lowers the Speed stat by 2 stages."
  val accuracy: Int = 95
  val moveType: Bug.type = Bug
  override def effects: List[SpeedEffect] = List(
    SpeedEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object Agility extends StatusMove {
  val moveName: String = "Agility"
  val moveDesc: String = "The user relaxes and lightens its body to move faster. Sharply raises the Speed stat by 2 stages."
  val accuracy: Int = 100
  val moveType: Psychic.type = Psychic
  override def effects: List[SpeedEffect] = List(
    SpeedEffect(2)
  )
  override def targetSelf: Boolean = true
}

object ShellSmash extends StatusMove {
  val moveName: String = "Shell Smash"
  val moveDesc: String = "The user breaks its shell, which lowers Defense but sharply raises Attack and Speed stats."
  val accuracy: Int = 100
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
  val moveDesc: String = "The user lightly performs a beautiful, mystic dance. It boosts the user's Atk, Def, and Speed stats."
  val accuracy: Int = 100
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
  val moveDesc: String = "A physical attack in which the user charges and slams into the target with its whole body."
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def basePower: Int = 40
}

object Scratch extends PhysicalMove {
  val moveName: String = "Scratch"
  val moveDesc: String = "Hard, pointed, and sharp claws rake the target to inflict damage."
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def basePower: Int = 40
}

object Pound extends PhysicalMove {
  val moveName: String = "Pound"
  val moveDesc: String = "The target is physically pounded with a long tail, a foreleg, or the like."
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def basePower: Int = 40
}

object Cut extends PhysicalMove {
  val moveName: String = "Cut"
  val moveDesc: String = "The target is cut with a scythe or a claw."
  val accuracy: Int = 95
  val moveType: Normal.type = Normal
  override def basePower: Int = 50
}

object Ember extends PhysicalMove {
  val moveName: String = "Ember"
  val moveDesc: String = "The target is attacked with small flames."
  val accuracy: Int = 100
  val moveType: Fire.type = Fire
  override def basePower: Int = 40
}

object WaterGun extends PhysicalMove {
  val moveName: String = "Water Gun"
  val moveDesc: String = "The target is blasted with a forceful shot of water."
  val accuracy: Int = 100
  val moveType: Water.type = Water
  override def basePower: Int = 40
}

object Spark extends PhysicalMove with StatusMove {
  val moveName: String = "Spark"
  val moveDesc: String = "The user throws an electrically charged tackle at the target."
  val accuracy: Int = 100
  val moveType: Electric.type = Electric
  override def basePower: Int = 65
  override def effects: List[SpeedEffect] = List(
    SpeedEffect(1)
  )
  override def targetSelf: Boolean = true
}

object VineWhip extends PhysicalMove {
  val moveName: String = "Vine Whip"
  val moveDesc: String = "The target is struck with slender, whiplike vines to inflict damage."
  val accuracy: Int = 100
  val moveType: Grass.type = Grass
  override def basePower: Int = 45
}

object IcePunch extends PhysicalMove with StatusMove {
  val moveName: String = "Ice Punch"
  val moveDesc: String = "The target is punched with an icy fist. Slow down opponent's Speed."
  val accuracy: Int = 100
  val moveType: Ice.type = Ice
  override def basePower: Int = 75
  override def effects: List[SpeedEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object DoubleKick extends PhysicalMove {
  val moveName: String = "Double Kick"
  val moveDesc: String = "The target is quickly kicked twice in succession using both feet."
  val accuracy: Int = 100
  val moveType: Fighting.type = Fighting
  override def basePower: Int = 60
}

object PoisonFang extends PhysicalMove with StatusMove {
  val moveName: String = "Poison Fang"
  val moveDesc: String = "The user bites the target with toxic fangs. Lower opponent's Defense stat by 1 stage."
  val accuracy: Int = 100
  val moveType: Poison.type = Poison
  override def basePower: Int = 50
  override def effects: List[DefenseEffect] = List(
    DefenseEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object PoisonSting extends PhysicalMove with StatusMove {
  val moveName: String = "Poison Sting"
  val moveDesc: String = "The user stabs the target with a poisonous stinger. Lower opponent's Defense stat by 2 stages."
  val accuracy: Int = 100
  val moveType: Poison.type = Poison
  override def basePower: Int = 15
  override def effects: List[DefenseEffect] = List(
    DefenseEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object HeartStamp extends PhysicalMove {
  val moveName: String = "Heart Stamp"
  val moveDesc: String = "The user unleashes a vicious blow after its cute act makes the target less wary."
  val accuracy: Int = 100
  val moveType: Psychic.type = Psychic
  override def basePower: Int = 60
}

object XScissor extends PhysicalMove {
  val moveName: String = "X-Scissor"
  val moveDesc: String = "The user slashes at the target by crossing its scythes or claws as if they were a pair of scissors."
  val accuracy: Int = 100
  val moveType: Bug.type = Bug
  override def basePower: Int = 80
}

object RockTomb extends PhysicalMove with StatusMove {
  val moveName: String = "Rock Tomb"
  val moveDesc: String = "Boulders are hurled at the target. Lower opponent's Speed stat by 1 stage."
  val accuracy: Int = 95
  val moveType: Rock.type = Rock
  override def basePower: Int = 60
  override def effects: List[SpeedEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object Growth extends StatusMove {
  val moveName: String = "Growth"
  val moveDesc: String = "The user's body grows all at once, raising the Attack stats."
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def effects: List[AttackEffect] = List(
    AttackEffect(1)
  )
  override def targetSelf: Boolean = true
}

object IcyWind extends PhysicalMove with StatusMove {
  val moveName: String = "Icy Wind"
  val moveDesc: String = "The user attacks with a gust of chilled air. Lower opponent's Speed stat by 1 stage."
  val accuracy: Int = 95
  val moveType: Ice.type = Ice
  override def basePower: Int = 55
  override def effects: List[SpeedEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object AncientPower extends PhysicalMove with StatusMove {
  val moveName: String = "Ancient Power"
  val moveDesc: String = "The user attacks with a prehistoric power. Raise all stats."
  val accuracy: Int = 100
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
  val moveDesc: String = "The user tears at the target with blades formed by psychic power."
  val accuracy: Int = 100
  val moveType: Psychic.type = Psychic
  override def basePower: Int = 70
}

object BodySlam extends PhysicalMove with StatusMove {
  val moveName: String = "Body Slam"
  val moveDesc: String = "The user drops onto the target with its full body weight."
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def basePower: Int = 85
  override def effects: List[SpeedEffect] = List(
    SpeedEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object BlazeKick extends PhysicalMove {
  val moveName: String = "Blaze Kick"
  val moveDesc: String = "The user launches a kick that lands a critical hit more easily."
  val accuracy: Int = 90
  val moveType: Fire.type = Fire
  override def basePower: Int = 85
}

object BulkUp extends StatusMove {
  val moveName: String = "Bulk Up"
  val moveDesc: String = "The user tenses its muscles to bulk up its body, raising both its Attack and Defense stats."
  val accuracy: Int = 100
  val moveType: Fighting.type = Fighting
  override def effects: List[StatEffect] = List(
    AttackEffect(1),
    DefenseEffect(1)
  )
  override def targetSelf: Boolean = true
}

object Smokescreen extends StatusMove {
  val moveName: String = "Smokescreen"
  val moveDesc: String = "The user releases an obscuring cloud of smoke or ink. Lower opponent's Accuracy stat by 1 stage."
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def effects: List[AccuracyEffect] = List(
    AccuracyEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object MuddyWater extends PhysicalMove with StatusMove {
  val moveName: String = "Muddy Water"
  val moveDesc: String = "The user attacks by shooting muddy water at the opposing Pokemon. Lower opponent's Accuracy stat by 1 stage."
  val accuracy: Int = 85
  val moveType: Water.type = Water
  override def basePower: Int = 90
  override def effects: List[AccuracyEffect] = List(
    AccuracyEffect(-1)
  )
  override def targetSelf: Boolean = false
}
