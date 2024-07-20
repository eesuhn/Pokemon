package pokemon.model

import scala.util.Random

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
    * - 1.0 if both or neither strong/weak
    * - 2.0 if strong
    * - 0.5 if weak
    *
    * @param target
    * @return
    */
  private def calculateModifier(target: Pokemon): Double = {
    val (strong, weak) = target
      .pTypes
      .foldLeft((false, false)) { case ((s, w), t) => (
        s || this.moveType.strongAgainst.contains(t),
        w || this.moveType.weakAgainst.contains(t))
      }

    if (strong && weak) 1.0
    else if (strong) 2.0
    else if (weak) 0.5
    else 1.0
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
  val moveName = "Growl"
  val moveDesc = "The user growls in an endearing way, making the opposing Pokemon less wary. Lower opponent's Attack stat by 1 stage."
  val accuracy = 100
  val moveType = Normal
  override def effects = List(
    AttackEffect(-1)
  )
  override def targetSelf = false
}

object Leer extends StatusMove {
  val moveName = "Leer"
  val moveDesc = "The user gives the target an intimidating leer. Lower opponent's Defense stat by 1 stage."
  val accuracy = 100
  val moveType = Normal
  override def effects = List(
    DefenseEffect(-1)
  )
  override def targetSelf = false
}

object Meditate extends StatusMove {
  val moveName = "Meditate"
  val moveDesc = "The user meditates to awaken the power deep within its body and raise its Attack stat by 1 stage."
  val accuracy = 100
  val moveType = Psychic
  override def effects = List(
    AttackEffect(1)
  )
  override def targetSelf = true
}

object Harden extends StatusMove {
  val moveName = "Harden"
  val moveDesc = "The user stiffens all the muscles in its body to raise its Defense stat by 1 stage."
  val accuracy = 100
  val moveType = Normal
  override def effects = List(
    DefenseEffect(1)
  )
  override def targetSelf = true
}

object SwordsDance extends StatusMove {
  val moveName = "Swords Dance"
  val moveDesc = "A frenetic dance to uplift the fighting spirit. Sharply raises the user's Attack stat by 2 stages."
  val accuracy = 100
  val moveType = Normal
  override def effects = List(
    AttackEffect(2)
  )
  override def targetSelf = true
}

object Charm extends StatusMove {
  val moveName = "Charm"
  val moveDesc = "The user gazes at the target rather charmingly, making it less wary. Sharply lowers the target's Attack stat by 2 stages."
  val accuracy = 100
  val moveType = Psychic
  override def effects = List(
    AttackEffect(-2)
  )
  override def targetSelf = false
}

object Screech extends StatusMove {
  val moveName = "Screech"
  val moveDesc = "An earsplitting screech harshly lowers the target's Defense stat by 2 stages."
  val accuracy = 85
  val moveType = Normal
  override def effects = List(
    DefenseEffect(-2)
  )
  override def targetSelf = false
}

object StringShot extends StatusMove {
  val moveName = "String Shot"
  val moveDesc = "The opposing Pokemon are bound with silk blown from the user's mouth that harshly lowers the Speed stat by 2 stages."
  val accuracy = 95
  val moveType = Bug
  override def effects = List(
    SpeedEffect(-2)
  )
  override def targetSelf = false
}

object Agility extends StatusMove {
  val moveName = "Agility"
  val moveDesc = "The user relaxes and lightens its body to move faster. Sharply raises the Speed stat by 2 stages."
  val accuracy = 100
  val moveType = Psychic
  override def effects = List(
    SpeedEffect(2)
  )
  override def targetSelf = true
}

object ShellSmash extends StatusMove {
  val moveName = "Shell Smash"
  val moveDesc = "The user breaks its shell, which lowers Defense but sharply raises Attack and Speed stats."
  val accuracy = 100
  val moveType = Normal
  override def effects = List(
    AttackEffect(2),
    SpeedEffect(2),
    DefenseEffect(-1)
  )
  override def targetSelf = true
}

object QuiverDance extends StatusMove {
  val moveName = "Quiver Dance"
  val moveDesc = "The user lightly performs a beautiful, mystic dance. It boosts the user's Atk, Def, and Speed stats."
  val accuracy = 100
  val moveType = Bug
  override def effects = List(
    AttackEffect(1),
    DefenseEffect(1),
    SpeedEffect(1)
  )
  override def targetSelf = true
}

object Tackle extends PhysicalMove {
  val moveName = "Tackle"
  val moveDesc = "A physical attack in which the user charges and slams into the target with its whole body."
  val accuracy = 100
  val moveType = Normal
  override def basePower = 40
}

object Scratch extends PhysicalMove {
  val moveName = "Scratch"
  val moveDesc = "Hard, pointed, and sharp claws rake the target to inflict damage."
  val accuracy = 100
  val moveType = Normal
  override def basePower = 40
}

object Pound extends PhysicalMove {
  val moveName = "Pound"
  val moveDesc = "The target is physically pounded with a long tail, a foreleg, or the like."
  val accuracy = 100
  val moveType = Normal
  override def basePower = 40
}

object Cut extends PhysicalMove {
  val moveName = "Cut"
  val moveDesc = "The target is cut with a scythe or a claw."
  val accuracy = 95
  val moveType = Normal
  override def basePower = 50
}

object Ember extends PhysicalMove {
  val moveName = "Ember"
  val moveDesc = "The target is attacked with small flames."
  val accuracy = 100
  val moveType = Fire
  override def basePower = 40
}

object WaterGun extends PhysicalMove {
  val moveName = "Water Gun"
  val moveDesc = "The target is blasted with a forceful shot of water."
  val accuracy = 100
  val moveType = Water
  override def basePower = 40
}

object Spark extends PhysicalMove with StatusMove {
  val moveName = "Spark"
  val moveDesc = "The user throws an electrically charged tackle at the target."
  val accuracy = 100
  val moveType = Electric
  override def basePower = 65
  override def effects = List(
    SpeedEffect(1)
  )
  override def targetSelf = true
}

object VineWhip extends PhysicalMove {
  val moveName = "Vine Whip"
  val moveDesc = "The target is struck with slender, whiplike vines to inflict damage."
  val accuracy = 100
  val moveType = Grass
  override def basePower = 45
}

object IcePunch extends PhysicalMove with StatusMove {
  val moveName = "Ice Punch"
  val moveDesc = "The target is punched with an icy fist. Slow down opponent's Speed."
  val accuracy = 100
  val moveType = Ice
  override def basePower = 75
  override def effects = List(
    SpeedEffect(-1)
  )
  override def targetSelf = false
}

object DoubleKick extends PhysicalMove {
  val moveName = "Double Kick"
  val moveDesc = "The target is quickly kicked twice in succession using both feet."
  val accuracy = 100
  val moveType = Fighting
  override def basePower = 60
}

object PoisonFang extends PhysicalMove with StatusMove {
  val moveName = "Poison Fang"
  val moveDesc = "The user bites the target with toxic fangs. Lower opponent's Defense stat by 1 stage."
  val accuracy = 100
  val moveType = Poison
  override def basePower = 50
  override def effects = List(
    DefenseEffect(-1)
  )
  override def targetSelf = false
}

object PoisonSting extends PhysicalMove with StatusMove {
  val moveName = "Poison Sting"
  val moveDesc = "The user stabs the target with a poisonous stinger. Lower opponent's Defense stat by 2 stages."
  val accuracy = 100
  val moveType = Poison
  override def basePower = 15
  override def effects = List(
    DefenseEffect(-2)
  )
  override def targetSelf = false
}

object HeartStamp extends PhysicalMove {
  val moveName = "Heart Stamp"
  val moveDesc = "The user unleashes a vicious blow after its cute act makes the target less wary."
  val accuracy = 100
  val moveType = Psychic
  override def basePower = 60
}

object XScissor extends PhysicalMove {
  val moveName = "X-Scissor"
  val moveDesc = "The user slashes at the target by crossing its scythes or claws as if they were a pair of scissors."
  val accuracy = 100
  val moveType = Bug
  override def basePower = 80
}

object RockTomb extends PhysicalMove with StatusMove {
  val moveName = "Rock Tomb"
  val moveDesc = "Boulders are hurled at the target. Lower opponent's Speed stat by 1 stage."
  val accuracy = 95
  val moveType = Rock
  override def basePower = 60
  override def effects = List(
    SpeedEffect(-1)
  )
  override def targetSelf = false
}

object Growth extends StatusMove {
  val moveName = "Growth"
  val moveDesc = "The user's body grows all at once, raising the Attack stats."
  val accuracy = 100
  val moveType = Normal
  override def effects = List(
    AttackEffect(1)
  )
  override def targetSelf = true
}

object IcyWind extends PhysicalMove with StatusMove {
  val moveName = "Icy Wind"
  val moveDesc = "The user attacks with a gust of chilled air. Lower opponent's Speed stat by 1 stage."
  val accuracy = 95
  val moveType = Ice
  override def basePower = 55
  override def effects = List(
    SpeedEffect(-1)
  )
  override def targetSelf = false
}

object AncientPower extends PhysicalMove with StatusMove {
  val moveName = "Ancient Power"
  val moveDesc = "The user attacks with a prehistoric power. Raise all stats."
  val accuracy = 100
  val moveType = Rock
  override def basePower = 60
  override def effects = List(
    AttackEffect(1),
    DefenseEffect(1),
    SpeedEffect(1)
  )
  override def targetSelf = true
}

object PsychoCut extends PhysicalMove {
  val moveName = "Psycho Cut"
  val moveDesc = "The user tears at the target with blades formed by psychic power."
  val accuracy = 100
  val moveType = Psychic
  override def basePower = 70
}

object BodySlam extends PhysicalMove with StatusMove {
  val moveName = "Body Slam"
  val moveDesc = "The user drops onto the target with its full body weight."
  val accuracy = 100
  val moveType = Normal
  override def basePower = 85
  override def effects = List(
    SpeedEffect(-1)
  )
  override def targetSelf = false
}

object BlazeKick extends PhysicalMove {
  val moveName = "Blaze Kick"
  val moveDesc = "The user launches a kick that lands a critical hit more easily."
  val accuracy = 90
  val moveType = Fire
  override def basePower = 85
}

object BulkUp extends StatusMove {
  val moveName = "Bulk Up"
  val moveDesc = "The user tenses its muscles to bulk up its body, raising both its Attack and Defense stats."
  val accuracy = 100
  val moveType = Fighting
  override def effects = List(
    AttackEffect(1),
    DefenseEffect(1)
  )
  override def targetSelf = true
}
