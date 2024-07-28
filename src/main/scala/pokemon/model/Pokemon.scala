package pokemon.model

import scala.util.Random

import pokemon.macros.Macros

object PokemonRegistry {
  private var _pokemons: List[Class[_ <: Pokemon]] = List.empty

  def registerPokemon(newPokemons: List[Class[_ <: Pokemon]]): Unit = {
    _pokemons = newPokemons ::: _pokemons
  }

  def pokemons: List[Class[_ <: Pokemon]] = _pokemons

  registerPokemon(Macros.registerSubclasses[Pokemon]("pokemon.model"))
}

abstract class Pokemon {
  val pName: String
  val attack: Attack
  val defense: Defense
  val accuracy: Accuracy = Accuracy(100)
  val speed: Speed
  private val _level: Int = 5
  private val _baseHP: Int = initHP
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
    *
    * @throws Exception if `types.length > 2`
    */
  protected def pTypes(types: List[Type]): Unit = {
    if (types.length > 2) throw new Exception(s"Pokemon $pName can have at most 2 types")
    _pTypes = types
  }

  /**
    * Set the moves for the Pokemon, at most 4 moves can be set
    *
    * @param moves
    *
    * @throws Exception if `moves.length > 4`
    */
  protected def moves(moves: List[Move]): Unit = {
    if (moves.length > 4) throw new Exception(s"Pokemon $pName can learn at most 4 moves")
    _moves = moves
  }

  /**
    * Take damage from an attack, HP cannot go below 0
    *
    * @param damage
    */
  def takeDamage(damage: Int): Unit = {
    _currentHP = Math.max(currentHP - damage, 0)
  }

  private def statusAttack(statusMove: StatusMove, target: Pokemon): Unit = {
    if (statusMove.targetSelf) statusMove.applyEffects(this)
    else statusMove.applyEffects(target)
  }

  private def physicalAttack(physicalMove: PhysicalMove, target: Pokemon): Unit = {
    val damage: Double = physicalMove.calculatePhysicalDamage(this, target)
    target.takeDamage(damage.toInt)
  }

  private def specialAttack(specialMove: SpecialMove, target: Pokemon): Unit = {
    physicalAttack(specialMove, target)
    statusAttack(specialMove, target)
  }

  /**
    * Attack the target Pokemon with the move
    *
    * Consider accuracy for both move and Pokemon
    *
    * - SpecialMove: PhysicalMove + StatusMove
    * - PhysicalMove: Calculate damage based on the user's attack and the target's defense
    * - StatusMove: Apply effects of the move to the target Pokemon
    *
    * @param move
    * @param target
    */
  def attack(move: Move, target: Pokemon): Boolean = {
    if (!calculatePokemonAccuracy() || !move.calculateMoveAccuracy()) {
      false
    } else {
      move match {
        case specialMove: SpecialMove => specialAttack(specialMove, target)
        case physicalMove: PhysicalMove => physicalAttack(physicalMove, target)
        case statusMove: StatusMove => statusAttack(statusMove, target)
      }
      true
    }
  }

  private def calculatePokemonAccuracy(): Boolean = {
    val random = new Random()
    random.nextInt(100) <= accuracy.value
  }
}

class Charmander extends Pokemon {
  val pName: String = "Charmander"
  val attack: Attack = Attack(52)
  val defense: Defense = Defense(43)
  val speed: Speed = Speed(65)
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
  val pName: String = "Squirtle"
  val attack: Attack = Attack(48)
  val defense: Defense = Defense(65)
  val speed: Speed = Speed(43)
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
  val pName: String = "Bulbasaur"
  val attack: Attack = Attack(49)
  val defense: Defense = Defense(49)
  val speed: Speed = Speed(45)
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
  val pName: String = "Geodude"
  val attack: Attack = Attack(80)
  val defense: Defense = Defense(100)
  val speed: Speed = Speed(20)
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
  val pName: String = "Pikachu"
  val attack: Attack = Attack(55)
  val defense: Defense = Defense(40)
  val speed: Speed = Speed(90)
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
  val pName: String = "Breloom"
  val attack: Attack = Attack(130)
  val defense: Defense = Defense(80)
  val speed: Speed = Speed(70)
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
  val pName: String = "Regice"
  val attack: Attack = Attack(50)
  val defense: Defense = Defense(100)
  val speed: Speed = Speed(50)
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
  val pName: String = "Hitmonchan"
  val attack: Attack = Attack(105)
  val defense: Defense = Defense(79)
  val speed: Speed = Speed(76)
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
  val pName: String = "Nidorino"
  val attack: Attack = Attack(72)
  val defense: Defense = Defense(57)
  val speed: Speed = Speed(65)
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
  val pName: String = "Dustox"
  val attack: Attack = Attack(50)
  val defense: Defense = Defense(70)
  val speed: Speed = Speed(65)
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
  val pName: String = "Mewtwo"
  val attack: Attack = Attack(110)
  val defense: Defense = Defense(90)
  val speed: Speed = Speed(130)
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
  val pName: String = "Scyther"
  val attack: Attack = Attack(110)
  val defense: Defense = Defense(80)
  val speed: Speed = Speed(105)
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
  val pName: String = "Heracross"
  val attack: Attack = Attack(125)
  val defense: Defense = Defense(75)
  val speed: Speed = Speed(85)
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
  val pName: String = "Onix"
  val attack: Attack = Attack(45)
  val defense: Defense = Defense(160)
  val speed: Speed = Speed(70)
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
  val pName: String = "Snorlax"
  val attack: Attack = Attack(110)
  val defense: Defense = Defense(65)
  val speed: Speed = Speed(30)
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
  val pName: String = "Blaziken"
  val attack: Attack = Attack(120)
  val defense: Defense = Defense(70)
  val speed: Speed = Speed(80)
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
  val pName: String = "Toxicroak"
  val attack: Attack = Attack(106)
  val defense: Defense = Defense(65)
  val speed: Speed = Speed(85)
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
  val pName: String = "Marshtomp"
  val attack: Attack = Attack(85)
  val defense: Defense = Defense(70)
  val speed: Speed = Speed(50)
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
  val pName: String = "Slowpoke"
  val attack: Attack = Attack(65)
  val defense: Defense = Defense(65)
  val speed: Speed = Speed(15)
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

class Exploud extends Pokemon {
  val pName: String = "Exploud"
  val attack: Attack = Attack(91)
  val defense: Defense = Defense(63)
  val speed: Speed = Speed(68)
  override def initHP: Int = 104
  pTypes(List(
    Normal
  ))
  moves(List(
    Growth,
    Screech,
    Scratch
  ))
}
