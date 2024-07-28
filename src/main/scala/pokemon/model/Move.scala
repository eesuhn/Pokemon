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
  val accuracy: Int
  val moveType: Type

  def movePower: String = this match {
    case physicalMove: PhysicalMove => physicalMove.basePower.toString
    case _ => "-"
  }

  def moveTypeName: String = this.moveType
    .getClass
    .getSimpleName
    .toLowerCase
    .replace("$", "")

  def moveCategoryName: String = this match {
    case _: PhysicalMove => "physical"
    case _: StatusMove => "status"
    case _ => "unknown"
  }

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
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def effects: List[AttackEffect] = List(
    AttackEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object Leer extends StatusMove {
  val moveName: String = "Leer"
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def effects: List[DefenseEffect] = List(
    DefenseEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object Meditate extends StatusMove {
  val moveName: String = "Meditate"
  val accuracy: Int = 100
  val moveType: Psychic.type = Psychic
  override def effects: List[AttackEffect] = List(
    AttackEffect(1)
  )
  override def targetSelf: Boolean = true
}

object Harden extends StatusMove {
  val moveName: String = "Harden"
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def effects: List[DefenseEffect] = List(
    DefenseEffect(1)
  )
  override def targetSelf: Boolean = true
}

object SwordsDance extends StatusMove {
  val moveName: String = "Swords Dance"
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def effects: List[AttackEffect] = List(
    AttackEffect(2)
  )
  override def targetSelf: Boolean = true
}

object Charm extends StatusMove {
  val moveName: String = "Charm"
  val accuracy: Int = 100
  val moveType: Psychic.type = Psychic
  override def effects: List[AttackEffect] = List(
    AttackEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object Screech extends StatusMove {
  val moveName: String = "Screech"
  val accuracy: Int = 85
  val moveType: Normal.type = Normal
  override def effects: List[DefenseEffect] = List(
    DefenseEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object StringShot extends StatusMove {
  val moveName: String = "String Shot"
  val accuracy: Int = 95
  val moveType: Bug.type = Bug
  override def effects: List[SpeedEffect] = List(
    SpeedEffect(-2)
  )
  override def targetSelf: Boolean = false
}

object Agility extends StatusMove {
  val moveName: String = "Agility"
  val accuracy: Int = 100
  val moveType: Psychic.type = Psychic
  override def effects: List[SpeedEffect] = List(
    SpeedEffect(2)
  )
  override def targetSelf: Boolean = true
}

object ShellSmash extends StatusMove {
  val moveName: String = "Shell Smash"
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
  override def basePower: Int = 40
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
  override def basePower: Int = 40
}

object WaterGun extends PhysicalMove {
  val moveName: String = "Water Gun"
  val accuracy: Int = 100
  val moveType: Water.type = Water
  override def basePower: Int = 40
}

object Spark extends PhysicalMove with StatusMove {
  val moveName: String = "Spark"
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
  val accuracy: Int = 100
  val moveType: Grass.type = Grass
  override def basePower: Int = 45
}

object IcePunch extends PhysicalMove with StatusMove {
  val moveName: String = "Ice Punch"
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
  val accuracy: Int = 100
  val moveType: Fighting.type = Fighting
  override def basePower: Int = 60
}

object PoisonFang extends PhysicalMove with StatusMove {
  val moveName: String = "Poison Fang"
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
  val accuracy: Int = 100
  val moveType: Psychic.type = Psychic
  override def basePower: Int = 60
}

object XScissor extends PhysicalMove {
  val moveName: String = "X-Scissor"
  val accuracy: Int = 100
  val moveType: Bug.type = Bug
  override def basePower: Int = 80
}

object RockTomb extends PhysicalMove with StatusMove {
  val moveName: String = "Rock Tomb"
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
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def effects: List[AttackEffect] = List(
    AttackEffect(1)
  )
  override def targetSelf: Boolean = true
}

object IcyWind extends PhysicalMove with StatusMove {
  val moveName: String = "Icy Wind"
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
  val accuracy: Int = 100
  val moveType: Psychic.type = Psychic
  override def basePower: Int = 70
}

object BodySlam extends PhysicalMove with StatusMove {
  val moveName: String = "Body Slam"
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
  val accuracy: Int = 90
  val moveType: Fire.type = Fire
  override def basePower: Int = 85
}

object BulkUp extends StatusMove {
  val moveName: String = "Bulk Up"
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
  val accuracy: Int = 100
  val moveType: Normal.type = Normal
  override def effects: List[AccuracyEffect] = List(
    AccuracyEffect(-1)
  )
  override def targetSelf: Boolean = false
}

object MuddyWater extends PhysicalMove with StatusMove {
  val moveName: String = "Muddy Water"
  val accuracy: Int = 85
  val moveType: Water.type = Water
  override def basePower: Int = 90
  override def effects: List[AccuracyEffect] = List(
    AccuracyEffect(-1)
  )
  override def targetSelf: Boolean = false
}
