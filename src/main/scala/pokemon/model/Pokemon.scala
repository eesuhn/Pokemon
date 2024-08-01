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

  def pTypeNames: List[String] = pTypes.map(_.name.toLowerCase)

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

  private def statusAttack(statusMove: StatusMove, target: Pokemon): List[String] = {
    if (statusMove.targetSelf) statusMove.applyEffects(this)
    else statusMove.applyEffects(target)
  }

  private def physicalAttack(physicalMove: PhysicalMove, target: Pokemon): List[String] = {
    val (damage, effectivenessMessage) = physicalMove.calculatePhysicalDamage(this, target)
    target.takeDamage(damage.toInt)
    List(effectivenessMessage).filter(_.nonEmpty)
  }

  private def specialAttack(specialMove: SpecialMove, target: Pokemon): List[String] = {
    val physicalMessage = physicalAttack(specialMove, target)
    val statusMessages = statusAttack(specialMove, target)
    (physicalMessage ::: statusMessages)
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
  def attack(move: Move, target: Pokemon): (Boolean, List[String]) = {
    if (!calculatePokemonAccuracy() || !move.calculateMoveAccuracy()) {
      (false, List.empty)
    } else {
      val effectMessages = move match {
        case specialMove: SpecialMove => specialAttack(specialMove, target)
        case physicalMove: PhysicalMove => physicalAttack(physicalMove, target)
        case statusMove: StatusMove => statusAttack(statusMove, target)
      }
      (true, effectMessages)
    }
  }

  private def calculatePokemonAccuracy(): Boolean = {
    val random = new Random()
    random.nextInt(100) <= accuracy.value
  }

  def pokemonHpPercentage: Double = currentHP.toDouble / baseHP.toDouble
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
    Ember,
    FireSpin
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
    WaterGun,
    Bubble
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
    VineWhip,
    PoisonPowder
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
    RockTomb,
    Harden
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
    Spark,
    ThunderShock
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
    VineWhip,
    DoubleKick
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
    IcyWind,
    AncientPower
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
    DoubleKick,
    BulkUp
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
    PoisonFang,
    DoubleKick
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
    StringShot,
    Harden
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
    Agility,
    FocusEnergy
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
    SwordsDance,
    QuiverDance,
    DoubleKick
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
    QuiverDance,
    BulkUp
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
    Harden,
    Sandstorm
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
    Screech,
    BulkUp
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
    DoubleKick,
    Screech
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
    FocusEnergy,
    BulkUp,
    DoubleKick,
    Screech
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
    Leer,
    Growl
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
    Growl,
    Screech
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
    Scratch,
    BulkUp
  ))
}

class Solrock extends Pokemon {
  val pName: String = "Solrock"
  val attack: Attack = Attack(95)
  val defense: Defense = Defense(85)
  val speed: Speed = Speed(70)
  override def initHP: Int = 70
  pTypes(List(
    Rock,
    Psychic
  ))
  moves(List(
    Explosion,
    Harden,
    Screech
  ))
}

class Rhyhorn extends Pokemon {
  val pName: String = "Rhyhorn"
  val attack: Attack = Attack(85)
  val defense: Defense = Defense(95)
  val speed: Speed = Speed(25)
  override def initHP: Int = 80
  pTypes(List(
    Rock
  ))
  moves(List(
    Tackle,
    RockTomb,
    Harden,
    Sandstorm
  ))
}

class Shuckle extends Pokemon {
  val pName: String = "Shuckle"
  val attack: Attack = Attack(10)
  val defense: Defense = Defense(230)
  val speed: Speed = Speed(5)
  override def initHP: Int = 20
  pTypes(List(
    Bug,
    Rock
  ))
  moves(List(
    Tackle,
    ShellSmash
  ))
}

class Regirock extends Pokemon {
  val pName: String = "Regirock"
  val attack: Attack = Attack(100)
  val defense: Defense = Defense(200)
  val speed: Speed = Speed(50)
  override def initHP: Int = 80
  pTypes(List(
    Rock
  ))
  moves(List(
    RockTomb,
    Harden,
    AncientPower,
    Screech
  ))
}

class Charizard extends Pokemon {
  val pName: String = "Charizard"
  val attack: Attack = Attack(84)
  val defense: Defense = Defense(78)
  val speed: Speed = Speed(100)
  override def initHP: Int = 78
  pTypes(List(
    Fire
  ))
  moves(List(
    Growl,
    Scratch,
    Ember,
    FlareBlitz
  ))
}

class Arbok extends Pokemon {
  val pName: String = "Arbok"
  val attack: Attack = Attack(85)
  val defense: Defense = Defense(69)
  val speed: Speed = Speed(80)
  override def initHP: Int = 60
  pTypes(List(
    Poison
  ))
  moves(List(
    IceFang,
    PoisonFang,
    Screech,
    BulkUp
  ))
}

class Hariyama extends Pokemon {
  val pName: String = "Hariyama"
  val attack: Attack = Attack(120)
  val defense: Defense = Defense(60)
  val speed: Speed = Speed(50)
  override def initHP: Int = 144
  pTypes(List(
    Fighting
  ))
  moves(List(
    ArmThrust,
    Brine,
    FocusEnergy,
    SandAttack
  ))
}

class Giratina extends Pokemon {
  val pName: String = "Giratina"
  val attack: Attack = Attack(100)
  val defense: Defense = Defense(120)
  val speed: Speed = Speed(90)
  override def initHP: Int = 150
  pTypes(List(
    Normal
  ))
  moves(List(
    DragonBreath,
    ShadowForce,
    ScaryFace,
    DragonClaw
  ))
}

class Kyogre extends Pokemon {
  val pName: String = "Kyogre"
  val attack: Attack = Attack(100)
  val defense: Defense = Defense(90)
  val speed: Speed = Speed(90)
  override def initHP: Int = 100
  pTypes(List(
    Water
  ))
  moves(List(
    WaterPulse,
    AquaRing,
    HydroPump
  ))
}
