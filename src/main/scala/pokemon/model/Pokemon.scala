package pokemon.model

import scala.util.Random

object Pokedex {
  private var subclasses: List[Class[_ <: Pokemon]] = List.empty

  def registerSubclass[T <: Pokemon](subclass: List[Class[_ <: Pokemon]]): Unit = {
    subclasses = subclass ::: subclasses
  }

  def getSubclasses: List[Class[_ <: Pokemon]] = subclasses

  registerSubclass(List(
    classOf[Charmander],
    classOf[Squirtle],
    classOf[Bulbasaur],
    classOf[Geodude],
    classOf[Pikachu],
    classOf[Breloom],
    classOf[Regice],
    classOf[Hitmonchan],
    classOf[Nidorino],
    classOf[Dustox],
    classOf[Mewtwo],
    classOf[Scyther],
    classOf[Heracross],
    classOf[Onix],
    classOf[Snorlax],
    classOf[Blaziken],
    classOf[Toxicroak],
    classOf[Marshtomp],
    classOf[Slowpoke]
  ))
}

abstract class Pokemon {
  val pName: String
  val attack: Attack
  val defense: Defense
  val accuracy: Accuracy = Accuracy(100)
  val speed: Speed
  private var _level: Int = 5
  private var _baseHP: Int = initHP
  private var _currentHP: Int = initHP
  private var _pTypes: List[Type] = List()
  private var _moves: List[Move] = List()

  protected def initHP: Int

  def level: Int = _level
  def baseHP: Int = _baseHP
  def currentHP: Int = _currentHP
  def pTypes: List[Type] = _pTypes
  def moves: List[Move] = _moves

  /**
    * Set the types for the Pokemon, at most 2 types can be set
    *
    * @param types
    */
  protected def pTypes(types: List[Type]): Unit = {
    if (types.length > 2) {
      throw new Exception(s"Pokemon $pName can have at most 2 types")
    }
    this._pTypes = types
  }

  /**
    * Set the moves for the Pokemon, at most 4 moves can be set
    *
    * @param moves
    */
  protected def moves(moves: List[Move]): Unit = {
    if (moves.length > 4) {
      throw new Exception(s"Pokemon $pName can learn at most 4 moves")
    }
    this._moves = moves
  }

  /**
    * Take damage from an attack, HP cannot go below 0
    *
    * @param damage
    */
  def takeDamage(damage: Int): Unit = {
    this._currentHP = Math.max(this.currentHP - damage, 0)
  }

  private def statusAttack(statusMove: StatusMove, target: Pokemon): Unit = {
    if (statusMove.targetSelf) statusMove.applyEffects(this)
    else statusMove.applyEffects(target)
  }

  private def physicalAttack(physicalMove: PhysicalMove, target: Pokemon): Unit = {
    val damage: Double = physicalMove.calculatePhysicalDamage(this, target)
    target.takeDamage(damage.toInt)
  }

  /**
    * Attack the target Pokemon with the move
    *
    * Consider accuracy for both move and Pokemon
    *
    * - PhysicalMove: Calculate damage based on the user's attack and the target's defense
    * - StatusMove: Apply effects of the move to the target Pokemon
    *
    * @param move
    * @param target
    */
  def attack(move: Move, target: Pokemon): Boolean = {
    if (!calculatePokemonAccuracy() ||
        !move.calculateMoveAccuracy()) {
      return false
    }

    if (move.isInstanceOf[PhysicalMove]) physicalAttack(move.asInstanceOf[PhysicalMove], target)
    if (move.isInstanceOf[StatusMove]) statusAttack(move.asInstanceOf[StatusMove], target)
    true
  }

  private def calculatePokemonAccuracy(): Boolean = {
    val random = new Random()
    random.nextInt(100) <= this.accuracy.value
  }
}

class Charmander extends Pokemon {
  val pName = "Charmander"
  val attack = Attack(52)
  val defense = Defense(43)
  val speed = Speed(65)
  override def initHP: Int = 39
  pTypes(List(
    Fire
  ))
  moves(List(
    Growl,
    Scratch,
    Ember
  ))
}

class Squirtle extends Pokemon {
  val pName = "Squirtle"
  val attack = Attack(48)
  val defense = Defense(65)
  val speed = Speed(43)
  override def initHP: Int = 44
  pTypes(List(
    Water
  ))
  moves(List(
    Growl,
    Tackle,
    WaterGun
  ))
}

class Bulbasaur extends Pokemon {
  val pName = "Bulbasaur"
  val attack = Attack(49)
  val defense = Defense(49)
  val speed = Speed(45)
  override def initHP: Int = 45
  pTypes(List(
    Grass,
    Poison
  ))
  moves(List(
    Growl,
    Tackle,
    VineWhip
  ))
}

class Geodude extends Pokemon {
  val pName = "Geodude"
  val attack = Attack(80)
  val defense = Defense(100)
  val speed = Speed(20)
  override def initHP: Int = 40
  pTypes(List(
    Rock
  ))
  moves(List(
    Tackle,
    Pound,
    RockTomb
  ))
}

class Pikachu extends Pokemon {
  val pName = "Pikachu"
  val attack = Attack(55)
  val defense = Defense(40)
  val speed = Speed(90)
  override def initHP: Int = 35
  pTypes(List(
    Electric
  ))
  moves(List(
    Growl,
    Charm,
    Spark
  ))
}

class Breloom extends Pokemon {
  val pName = "Breloom"
  val attack = Attack(130)
  val defense = Defense(80)
  val speed = Speed(70)
  override def initHP: Int = 60
  pTypes(List(
    Grass,
    Fighting
  ))
  moves(List(
    Tackle,
    Smokescreen,
    VineWhip
  ))
}

class Regice extends Pokemon {
  val pName = "Regice"
  val attack = Attack(50)
  val defense = Defense(100)
  val speed = Speed(50)
  override def initHP: Int = 80
  pTypes(List(
    Ice
  ))
  moves(List(
    Harden,
    Cut,
    IcyWind
  ))
}

class Hitmonchan extends Pokemon {
  val pName = "Hitmonchan"
  val attack = Attack(105)
  val defense = Defense(79)
  val speed = Speed(76)
  override def initHP: Int = 50
  pTypes(List(
    Fighting
  ))
  moves(List(
    Tackle,
    Agility,
    DoubleKick
  ))
}

class Nidorino extends Pokemon {
  val pName = "Nidorino"
  val attack = Attack(72)
  val defense = Defense(57)
  val speed = Speed(65)
  override def initHP: Int = 61
  pTypes(List(
    Poison
  ))
  moves(List(
    Leer,
    Tackle,
    PoisonFang
  ))
}

class Dustox extends Pokemon {
  val pName = "Dustox"
  val attack = Attack(50)
  val defense = Defense(70)
  val speed = Speed(65)
  override def initHP: Int = 60
  pTypes(List(
    Bug,
    Poison
  ))
  moves(List(
    Tackle,
    PoisonSting,
    StringShot
  ))
}

class Mewtwo extends Pokemon {
  val pName = "Mewtwo"
  val attack = Attack(110)
  val defense = Defense(90)
  val speed = Speed(130)
  override def initHP: Int = 106
  pTypes(List(
    Psychic
  ))
  moves(List(
    AncientPower,
    PsychoCut,
    Agility
  ))
}

class Scyther extends Pokemon {
  val pName = "Scyther"
  val attack = Attack(110)
  val defense = Defense(80)
  val speed = Speed(105)
  override def initHP: Int = 70
  pTypes(List(
    Bug,
    Grass
  ))
  moves(List(
    XScissor,
    VineWhip,
    QuiverDance
  ))
}

class Heracross extends Pokemon {
  val pName = "Heracross"
  val attack = Attack(125)
  val defense = Defense(75)
  val speed = Speed(85)
  override def initHP: Int = 80
  pTypes(List(
    Bug,
    Fighting
  ))
  moves(List(
    Tackle,
    DoubleKick,
    QuiverDance
  ))
}

class Onix extends Pokemon {
  val pName = "Onix"
  val attack = Attack(45)
  val defense = Defense(160)
  val speed = Speed(70)
  override def initHP: Int = 35
  pTypes(List(
    Rock
  ))
  moves(List(
    Tackle,
    RockTomb,
    Harden
  ))
}

class Snorlax extends Pokemon {
  val pName = "Snorlax"
  val attack = Attack(110)
  val defense = Defense(65)
  val speed = Speed(30)
  override def initHP: Int = 160
  pTypes(List(
    Normal
  ))
  moves(List(
    BodySlam,
    Growl,
    Screech
  ))
}

class Blaziken extends Pokemon {
  val pName = "Blaziken"
  val attack = Attack(120)
  val defense = Defense(70)
  val speed = Speed(80)
  override def initHP: Int = 80
  pTypes(List(
    Fire,
    Fighting
  ))
  moves(List(
    BlazeKick,
    BulkUp,
    DoubleKick
  ))
}

class Toxicroak extends Pokemon {
  val pName = "Toxicroak"
  val attack = Attack(106)
  val defense = Defense(65)
  val speed = Speed(85)
  override def initHP: Int = 83
  pTypes(List(
    Poison,
    Fighting
  ))
  moves(List(
    Growth,
    BulkUp,
    DoubleKick
  ))
}

class Marshtomp extends Pokemon {
  val pName = "Marshtomp"
  val attack = Attack(85)
  val defense = Defense(70)
  val speed = Speed(50)
  override def initHP: Int = 70
  pTypes(List(
    Water
  ))
  moves(List(
    WaterGun,
    MuddyWater,
    Leer
  ))
}

class Slowpoke extends Pokemon {
  val pName = "Slowpoke"
  val attack = Attack(65)
  val defense = Defense(65)
  val speed = Speed(15)
  override def initHP: Int = 90
  pTypes(List(
    Water,
    Psychic
  ))
  moves(List(
    WaterGun,
    ShellSmash,
    Growl
  ))
}
