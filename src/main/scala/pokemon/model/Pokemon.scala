package pokemon.model

import scala.util.Random

import pokemon.macros.Macros

object PokemonRegistry {
  private var _pokemons: List[Class[_ <: Pokemon]] = List.empty
  private lazy val _pokemonInstances: Map[String, Pokemon] = createPokemonInstances()

  def registerPokemon(newPokemons: List[Class[_ <: Pokemon]]): Unit = {
    _pokemons = newPokemons ::: _pokemons
  }

  def pokemons: List[Class[_ <: Pokemon]] = _pokemons

  def pokemonInstances: Map[String, Pokemon] = _pokemonInstances

  private def createPokemonInstances(): Map[String, Pokemon] = {
    _pokemons.map { pokemonClass =>
      val instance = pokemonClass.getDeclaredConstructor().newInstance()
      instance.pName -> instance
    }.toMap
  }

  registerPokemon(Macros.registerSubclasses[Pokemon]("pokemon.model"))
}

abstract class Pokemon {
  val pName: String
  val rarity: Rarity
  val health: Health
  val attack: Attack
  val defense: Defense
  val accuracy: Accuracy = Accuracy(100)
  val speed: Speed
  val critical: Critical = Critical()

  private val _level: Int = 5
  private var _pTypes: List[Type] = List(NoType)
  private var _moves: List[Move] = List(Struggle)
  private lazy val _score: Double = calculateScore()
  private val _baseStatNorm: Double = 1.0
  private val _moveScoreNorm: Double = 100.0

  def level: Int = _level
  def pTypes: List[Type] = _pTypes
  def moves: List[Move] = _moves
  def score: Double = _score

  def pTypeNames: List[String] = pTypes.map(_.name.toLowerCase)

  /**
    * Set the types for the Pokemon, at most 2 types can be set
    *
    * @param types
    *
    * @throws Exception if `types.length > 2`
    */
  protected def pTypes_=(types: List[Type]): Unit = {
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
  protected def moves_=(moves: List[Move]): Unit = {
    if (moves.length > 4) throw new Exception(s"Pokemon $pName can learn at most 4 moves")
    _moves = moves
  }

  private def statusAttack(statusMove: StatusMove, target: Pokemon): List[String] = {
    if (statusMove.targetSelf) statusMove.applyEffects(this)
    else statusMove.applyEffects(target)
  }

  private def physicalAttack(physicalMove: PhysicalMove, target: Pokemon): List[String] = {
    val (damage, effectivenessMessage) = physicalMove.calculatePhysicalDamage(this, target)
    target.health.updateValue(-damage.toInt)
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

  def pokemonHpPercentage: Double = health.value.toDouble / health.baseValue.toDouble

  def baseStatScore(): Double = {
    val statList = List(health, attack, defense, speed)
    statList.map(_.statScore()).sum
  }

  /**
    * Normalize by `_baseStatNorm` and `_moveScoreNorm`
    *
    * @return
    */
  def calculateScore(): Double = {
    val statScore = baseStatScore() * _baseStatNorm
    val moveScore = moves.map(_.moveEfficiency()).sum * _moveScoreNorm
    statScore + moveScore
  }

  /**
    * Check if Pokemon is out of bounds of the rarity's score range
    *
    * @return
    */
  def outOfBounds: Boolean = {
    _score > rarity.weightageUpperBound ||
      _score < rarity.weightageLowerBound
  }

  /**
    * Check if Pokemon is within `boundRange` of the rarity's score range
    *
    * @return
    */
  def nearBounds: Boolean = {
    val boundRange = 10.0
    _score > rarity.weightageUpperBound - boundRange ||
      _score < rarity.weightageLowerBound + boundRange
  }

  /**
    * Calculate the efficiency of each move, round up to 2 decimal places
    *
    * - Consider the target's type and the move's type (modifier)
    *
    * @return
    */
  def moveEffMap(target: Pokemon): Map[Move, Double] = {
    moves.map(move =>
      move -> BigDecimal(move.moveEfficiencyByTarget(target))
        .setScale(2, BigDecimal.RoundingMode.HALF_UP)
        .toDouble
    ).toMap
  }
}

class Pikachu extends Pokemon {
  val pName: String = "Pikachu"
  val rarity: Rarity = Uncommon
  val health: Health = Health(35)
  val attack: Attack = Attack(55)
  val defense: Defense = Defense(40)
  val speed: Speed = Speed(90)

  pTypes_=(List(
    Electric
  ))
  moves_=(List(
    Charm,
    ThunderShock,
    ThunderWave,
    TailWhip
  ))
}

class Rattata extends Pokemon {
  val pName: String = "Rattata"
  val rarity: Rarity = Common
  val health: Health = Health(30)
  val attack: Attack = Attack(56)
  val defense: Defense = Defense(35)
  val speed: Speed = Speed(72)

  pTypes_=(List(
    Normal
  ))
  moves_=(List(
    Tackle,
    FocusEnergy
  ))
}

class Hitmonchan extends Pokemon {
  val pName: String = "Hitmonchan"
  val rarity: Rarity = Rare
  val health: Health = Health(50)
  val attack: Attack = Attack(105)
  val defense: Defense = Defense(79)
  val speed: Speed = Speed(76)

  pTypes_=(List(
    Fighting
  ))
  moves_=(List(
    Agility,
    FirePunch,
    IcePunch
  ))
}

class Snorlax extends Pokemon {
  val pName: String = "Snorlax"
  val rarity: Rarity = SuperRare
  val health: Health = Health(160)
  val attack: Attack = Attack(110)
  val defense: Defense = Defense(65)
  val speed: Speed = Speed(30)

  pTypes_=(List(
    Normal
  ))
  moves_=(List(
    BodySlam,
    BellyDrum,
    Bite,
    Screech
  ))
}

class Metagross extends Pokemon {
  val pName: String = "Metagross"
  val rarity: Rarity = SuperRare
  val health: Health = Health(80)
  val attack: Attack = Attack(135)
  val defense: Defense = Defense(130)
  val speed: Speed = Speed(70)

  pTypes_=(List(
    Steel,
    Psychic
  ))
  moves_=(List(
    MeteorMash,
    ZenHeadbutt,
    BulletPunch,
    Agility
  ))
}

class Giratina extends Pokemon {
  val pName: String = "Giratina"
  val rarity: Rarity = UltraRare
  val health: Health = Health(150)
  val attack: Attack = Attack(100)
  val defense: Defense = Defense(120)
  val speed: Speed = Speed(90)

  pTypes_=(List(
    Ghost,
    Dragon
  ))
  moves_=(List(
    ShadowForce,
    DragonClaw,
    DragonBreath,
    AncientPower
  ))
}

class Bulbasaur extends Pokemon {
  val pName: String = "Bulbasaur"
  val rarity: Rarity = Uncommon
  val health: Health = Health(45)
  val attack: Attack = Attack(49)
  val defense: Defense = Defense(49)
  val speed: Speed = Speed(45)

  pTypes_=(List(
    Grass,
    Poison
  ))
  moves_=(List(
    Tackle,
    Growl,
    VineWhip,
    RazorLeaf
  ))
}

class Charmander extends Pokemon {
  val pName: String = "Charmander"
  val rarity: Rarity = Uncommon
  val health: Health = Health(39)
  val attack: Attack = Attack(52)
  val defense: Defense = Defense(43)
  val speed: Speed = Speed(65)

  pTypes_=(List(
    Fire
  ))
  moves_=(List(
    Scratch,
    Growl,
    Ember,
    Smokescreen
  ))
}

class Squirtle extends Pokemon {
  val pName: String = "Squirtle"
  val rarity: Rarity = Uncommon
  val health: Health = Health(44)
  val attack: Attack = Attack(48)
  val defense: Defense = Defense(65)
  val speed: Speed = Speed(43)

  pTypes_=(List(
    Water
  ))
  moves_=(List(
    Tackle,
    TailWhip,
    WaterGun,
    Bite
  ))
}

class Venusaur extends Pokemon {
  val pName: String = "Venusaur"
  val rarity: Rarity = SuperRare
  val health: Health = Health(80)
  val attack: Attack = Attack(96)
  val defense: Defense = Defense(83)
  val speed: Speed = Speed(80)

  pTypes_=(List(
    Grass,
    Poison
  ))
  moves_=(List(
    SolarBeam,
    PetalDance,
    RazorLeaf,
    SeedBomb
  ))
}

class Charizard extends Pokemon {
  val pName: String = "Charizard"
  val rarity: Rarity = SuperRare
  val health: Health = Health(78)
  val attack: Attack = Attack(94)
  val defense: Defense = Defense(78)
  val speed: Speed = Speed(100)

  pTypes_=(List(
    Fire,
    Flying
  ))
  moves_=(List(
    Flamethrower,
    DragonClaw,
    AirSlash,
    DragonBreath
  ))
}

class Blastoise extends Pokemon {
  val pName: String = "Blastoise"
  val rarity: Rarity = SuperRare
  val health: Health = Health(79)
  val attack: Attack = Attack(83)
  val defense: Defense = Defense(110)
  val speed: Speed = Speed(78)

  pTypes_=(List(
    Water
  ))
  moves_=(List(
    HydroPump,
    AquaTail,
    Bite,
    ShellSmash
  ))
}

class Gengar extends Pokemon {
  val pName: String = "Gengar"
  val rarity: Rarity = SuperRare
  val health: Health = Health(60)
  val attack: Attack = Attack(65)
  val defense: Defense = Defense(60)
  val speed: Speed = Speed(110)

  pTypes_=(List(
    Ghost,
    Poison
  ))
  moves_=(List(
    ShadowBall,
    SludgeBomb,
    DarkPulse,
    DreamEater
  ))
}

class Armaldo extends Pokemon {
  val pName: String = "Armaldo"
  val rarity: Rarity = SuperRare
  val health: Health = Health(75)
  val attack: Attack = Attack(125)
  val defense: Defense = Defense(100)
  val speed: Speed = Speed(45)

  pTypes_=(List(
    Rock,
    Bug
  ))
  moves_=(List(
    XScissor,
    RockSlide,
    Earthquake,
    IronDefense
  ))
}

class Togekiss extends Pokemon {
  val pName: String = "Togekiss"
  val rarity: Rarity = SuperRare
  val health: Health = Health(85)
  val attack: Attack = Attack(50)
  val defense: Defense = Defense(95)
  val speed: Speed = Speed(80)

  pTypes_=(List(
    Fairy,
    Flying
  ))
  moves_=(List(
    AirSlash,
    Charm,
    AncientPower,
    SkyAttack
  ))
}

class Mawile extends Pokemon {
  val pName: String = "Mawile"
  val rarity: Rarity = Rare
  val health: Health = Health(70)
  val attack: Attack = Attack(85)
  val defense: Defense = Defense(85)
  val speed: Speed = Speed(50)

  pTypes_=(List(
    Steel,
    Fairy
  ))
  moves_=(List(
    Bite,
    IronDefense,
    IronHead,
    Crunch
  ))
}

class Gardevoir extends Pokemon {
  val pName: String = "Gardevoir"
  val rarity: Rarity = SuperRare
  val health: Health = Health(68)
  val attack: Attack = Attack(75)
  val defense: Defense = Defense(65)
  val speed: Speed = Speed(80)

  pTypes_=(List(
    Psychic,
    Fairy
  ))
  moves_=(List(
    Confusion,
    CalmMind,
    DreamEater,
    Charm
  ))
}

class Jigglypuff extends Pokemon {
  val pName: String = "Jigglypuff"
  val rarity: Rarity = Uncommon
  val health: Health = Health(115)
  val attack: Attack = Attack(45)
  val defense: Defense = Defense(20)
  val speed: Speed = Speed(20)

  pTypes_=(List(
    Normal,
    Fairy
  ))
  moves_=(List(
    Charm,
    Pound,
    DefenseCurl
  ))
}

class Salamence extends Pokemon {
  val pName: String = "Salamence"
  val rarity: Rarity = SuperRare
  val health: Health = Health(95)
  val attack: Attack = Attack(135)
  val defense: Defense = Defense(80)
  val speed: Speed = Speed(100)

  pTypes_=(List(
    Dragon,
    Flying
  ))
  moves_=(List(
    DragonClaw,
    DragonBreath,
    ZenHeadbutt,
    Crunch
  ))
}

class Garchomp extends Pokemon {
  val pName: String = "Garchomp"
  val rarity: Rarity = SuperRare
  val health: Health = Health(102)
  val attack: Attack = Attack(114)
  val defense: Defense = Defense(96)
  val speed: Speed = Speed(94)

  pTypes_=(List(
    Dragon,
    Ground
  ))
  moves_=(List(
    DragonClaw,
    Earthquake,
    IronDefense,
    SwordsDance
  ))
}

class Gible extends Pokemon {
  val pName: String = "Gible"
  val rarity: Rarity = Uncommon
  val health: Health = Health(58)
  val attack: Attack = Attack(70)
  val defense: Defense = Defense(45)
  val speed: Speed = Speed(42)

  pTypes_=(List(
    Dragon,
    Ground
  ))
  moves_=(List(
    DragonBreath,
    SandAttack,
    Bite
  ))
}

class Altaria extends Pokemon {
  val pName: String = "Altaria"
  val rarity: Rarity = Rare
  val health: Health = Health(75)
  val attack: Attack = Attack(70)
  val defense: Defense = Defense(90)
  val speed: Speed = Speed(80)

  pTypes_=(List(
    Dragon,
    Flying
  ))
  moves_=(List(
    DragonBreath,
    DragonDance,
    AirSlash
  ))
}

class Dialga extends Pokemon {
  val pName: String = "Dialga"
  val rarity: Rarity = UltraRare
  val health: Health = Health(100)
  val attack: Attack = Attack(120)
  val defense: Defense = Defense(120)
  val speed: Speed = Speed(90)

  pTypes_=(List(
    Steel,
    Dragon
  ))
  moves_=(List(
    DragonClaw,
    IronDefense,
    RoarOfTime,
    FlashCannon
  ))
}

class Zekrom extends Pokemon {
  val pName: String = "Zekrom"
  val rarity: Rarity = UltraRare
  val health: Health = Health(100)
  val attack: Attack = Attack(150)
  val defense: Defense = Defense(120)
  val speed: Speed = Speed(90)

  pTypes_=(List(
    Dragon,
    Electric
  ))
  moves_=(List(
    DragonClaw,
    FusionBolt,
    ZenHeadbutt,
    BoltStrike
  ))
}

class Rayquaza extends Pokemon {
  val pName: String = "Rayquaza"
  val rarity: Rarity = UltraRare
  val health: Health = Health(105)
  val attack: Attack = Attack(150)
  val defense: Defense = Defense(90)
  val speed: Speed = Speed(95)

  pTypes_=(List(
    Dragon,
    Flying
  ))
  moves_=(List(
    DragonClaw,
    DragonDance,
    AirSlash,
    Hurricane
  ))
}

class Geodude extends Pokemon {
  val pName: String = "Geodude"
  val rarity: Rarity = Common
  val health: Health = Health(40)
  val attack: Attack = Attack(65)
  val defense: Defense = Defense(65)
  val speed: Speed = Speed(22)

  pTypes_=(List(
    Rock,
    Ground
  ))
  moves_=(List(
    Tackle,
    DefenseCurl
  ))
}

class Shuckle extends Pokemon {
  val pName: String = "Shuckle"
  val rarity: Rarity = Uncommon
  val health: Health = Health(20)
  val attack: Attack = Attack(10)
  val defense: Defense = Defense(230)
  val speed: Speed = Speed(5)

  pTypes_=(List(
    Bug,
    Rock
  ))
  moves_=(List(
    ShellSmash,
    Pound,
    BugBite
  ))
}

class Cyndaquil extends Pokemon {
  val pName: String = "Cyndaquil"
  val rarity: Rarity = Uncommon
  val health: Health = Health(39)
  val attack: Attack = Attack(52)
  val defense: Defense = Defense(43)
  val speed: Speed = Speed(65)

  pTypes_=(List(
    Fire
  ))
  moves_=(List(
    Tackle,
    Leer,
    Ember,
    Smokescreen
  ))
}

class Torchic extends Pokemon {
  val pName: String = "Torchic"
  val rarity: Rarity = Uncommon
  val health: Health = Health(45)
  val attack: Attack = Attack(60)
  val defense: Defense = Defense(40)
  val speed: Speed = Speed(45)

  pTypes_=(List(
    Fire
  ))
  moves_=(List(
    Scratch,
    Growl,
    Ember,
    Peck
  ))
}

class Infernape extends Pokemon {
  val pName: String = "Infernape"
  val rarity: Rarity = Rare
  val health: Health = Health(76)
  val attack: Attack = Attack(104)
  val defense: Defense = Defense(71)
  val speed: Speed = Speed(108)

  pTypes_=(List(
    Fire,
    Fighting
  ))
  moves_=(List(
    FirePunch,
    MachPunch,
    FlameWheel
  ))
}

class Victini extends Pokemon {
  val pName: String = "Victini"
  val rarity: Rarity = SuperRare
  val health: Health = Health(100)
  val attack: Attack = Attack(100)
  val defense: Defense = Defense(100)
  val speed: Speed = Speed(100)

  pTypes_=(List(
    Psychic,
    Fire
  ))
  moves_=(List(
    ZenHeadbutt,
    VCreate,
    FocusEnergy,
    FlameCharge
  ))
}

class Kyogre extends Pokemon {
  val pName: String = "Kyogre"
  val rarity: Rarity = UltraRare
  val health: Health = Health(100)
  val attack: Attack = Attack(100)
  val defense: Defense = Defense(90)
  val speed: Speed = Speed(90)

  pTypes_=(List(
    Water
  ))
  moves_=(List(
    HydroPump,
    AquaTail,
    AquaRing,
    IceBeam
  ))
}

class Groudon extends Pokemon {
  val pName: String = "Groudon"
  val rarity: Rarity = UltraRare
  val health: Health = Health(100)
  val attack: Attack = Attack(150)
  val defense: Defense = Defense(140)
  val speed: Speed = Speed(90)

  pTypes_=(List(
    Ground
  ))
  moves_=(List(
    SolarBeam,
    SwordsDance,
    Earthquake,
    Eruption
  ))
}

class Psyduck extends Pokemon {
  val pName: String = "Psyduck"
  val rarity: Rarity = Common
  val health: Health = Health(50)
  val attack: Attack = Attack(40)
  val defense: Defense = Defense(44)
  val speed: Speed = Speed(50)

  pTypes_=(List(
    Water
  ))
  moves_=(List(
    Confusion
  ))
}

class Voltorb extends Pokemon {
  val pName: String = "Voltorb"
  val rarity: Rarity = Common
  val health: Health = Health(30)
  val attack: Attack = Attack(50)
  val defense: Defense = Defense(32)
  val speed: Speed = Speed(80)

  pTypes_=(List(
    Electric
  ))
  moves_=(List(
    Explosion,
    Tackle
  ))
}

class Shedinja extends Pokemon {
  val pName: String = "Shedinja"
  val rarity: Rarity = Rare
  val health: Health = Health(1)
  val attack: Attack = Attack(90)
  val defense: Defense = Defense(45)
  val speed: Speed = Speed(90)

  pTypes_=(List(
    Bug,
    Ghost
  ))
  moves_=(List(
    ShadowForce,
    XScissor,
    SandAttack,
    MetalClaw
  ))
}

class Shuppet extends Pokemon {
  val pName: String = "Shuppet"
  val rarity: Rarity = Common
  val health: Health = Health(45)
  val attack: Attack = Attack(70)
  val defense: Defense = Defense(30)
  val speed: Speed = Speed(40)

  pTypes_=(List(
    Ghost
  ))
  moves_=(List(
    Astonish,
    Growl
  ))
}

class Spiritomb extends Pokemon {
  val pName: String = "Spiritomb"
  val rarity: Rarity = SuperRare
  val health: Health = Health(50)
  val attack: Attack = Attack(92)
  val defense: Defense = Defense(98)
  val speed: Speed = Speed(35)

  pTypes_=(List(
    Ghost,
    Dark
  ))
  moves_=(List(
    DreamEater,
    SuckerPunch,
    ConfuseRay,
    ShadowBall
  ))
}

class Froslass extends Pokemon {
  val pName: String = "Froslass"
  val rarity: Rarity = Rare
  val health: Health = Health(66)
  val attack: Attack = Attack(70)
  val defense: Defense = Defense(70)
  val speed: Speed = Speed(100)

  pTypes_=(List(
    Ice,
    Ghost
  ))
  moves_=(List(
    IceBeam,
    ShadowBall,
    ConfuseRay
  ))
}

class Golett extends Pokemon {
  val pName: String = "Golett"
  val rarity: Rarity = Uncommon
  val health: Health = Health(59)
  val attack: Attack = Attack(74)
  val defense: Defense = Defense(50)
  val speed: Speed = Speed(35)

  pTypes_=(List(
    Ground,
    Ghost
  ))
  moves_=(List(
    ShadowPunch,
    Harden,
    Earthquake
  ))
}

class Golurk extends Pokemon {
  val pName: String = "Golurk"
  val rarity: Rarity = SuperRare
  val health: Health = Health(89)
  val attack: Attack = Attack(124)
  val defense: Defense = Defense(80)
  val speed: Speed = Speed(55)

  pTypes_=(List(
    Ground,
    Ghost
  ))
  moves_=(List(
    ShadowBall,
    Earthquake,
    HammerArm,
    DynamicPunch
  ))
}

class Sableye extends Pokemon {
  val pName: String = "Sableye"
  val rarity: Rarity = Rare
  val health: Health = Health(50)
  val attack: Attack = Attack(75)
  val defense: Defense = Defense(75)
  val speed: Speed = Speed(50)

  pTypes_=(List(
    Dark,
    Ghost
  ))
  moves_=(List(
    ShadowBall,
    Astonish,
    ZenHeadbutt,
    ShadowClaw
  ))
}

class Lapras extends Pokemon {
  val pName: String = "Lapras"
  val rarity: Rarity = Rare
  val health: Health = Health(130)
  val attack: Attack = Attack(85)
  val defense: Defense = Defense(80)
  val speed: Speed = Speed(60)

  pTypes_=(List(
    Water,
    Ice
  ))
  moves_=(List(
    IceShard,
    ShellSmash,
    BodySlam,
    Surf
  ))
}

class Spheal extends Pokemon {
  val pName: String = "Spheal"
  val rarity: Rarity = Common
  val health: Health = Health(70)
  val attack: Attack = Attack(40)
  val defense: Defense = Defense(50)
  val speed: Speed = Speed(25)

  pTypes_=(List(
    Ice,
    Water
  ))
  moves_=(List(
    PowderSnow,
    DefenseCurl
  ))
}

class Regice extends Pokemon {
  val pName: String = "Regice"
  val rarity: Rarity = UltraRare
  val health: Health = Health(100)
  val attack: Attack = Attack(80)
  val defense: Defense = Defense(200)
  val speed: Speed = Speed(60)

  pTypes_=(List(
    Ice
  ))
  moves_=(List(
    IceBeam,
    AncientPower,
    HammerArm,
    Blizzard
  ))
}

class Walrein extends Pokemon {
  val pName: String = "Walrein"
  val rarity: Rarity = SuperRare
  val health: Health = Health(110)
  val attack: Attack = Attack(80)
  val defense: Defense = Defense(90)
  val speed: Speed = Speed(65)

  pTypes_=(List(
    Ice,
    Water
  ))
  moves_=(List(
    IceBeam,
    Surf,
    BodySlam,
    Blizzard
  ))
}

class Abomasnow extends Pokemon {
  val pName: String = "Abomasnow"
  val rarity: Rarity = Rare
  val health: Health = Health(90)
  val attack: Attack = Attack(86)
  val defense: Defense = Defense(75)
  val speed: Speed = Speed(60)

  pTypes_=(List(
    Ice,
    Grass
  ))
  moves_=(List(
    IceShard,
    WoodHammer,
    Blizzard,
    RazorLeaf
  ))
}

class Kyurem extends Pokemon {
  val pName: String = "Kyurem"
  val rarity: Rarity = UltraRare
  val health: Health = Health(125)
  val attack: Attack = Attack(130)
  val defense: Defense = Defense(90)
  val speed: Speed = Speed(95)

  pTypes_=(List(
    Dragon,
    Ice
  ))
  moves_=(List(
    IceBeam,
    DragonClaw,
    Blizzard,
    FusionBolt
  ))
}

class Meganium extends Pokemon {
  val pName: String = "Meganium"
  val rarity: Rarity = SuperRare
  val health: Health = Health(80)
  val attack: Attack = Attack(82)
  val defense: Defense = Defense(100)
  val speed: Speed = Speed(80)

  pTypes_=(List(
    Grass
  ))
  moves_=(List(
    SolarBeam,
    PetalDance,
    RazorLeaf,
    SeedBomb
  ))
}

class Chikorita extends Pokemon {
  val pName: String = "Chikorita"
  val rarity: Rarity = Common
  val health: Health = Health(45)
  val attack: Attack = Attack(50)
  val defense: Defense = Defense(40)
  val speed: Speed = Speed(45)

  pTypes_=(List(
    Grass
  ))
  moves_=(List(
    Growl,
    RazorLeaf
  ))
}

class Treecko extends Pokemon {
  val pName: String = "Treecko"
  val rarity: Rarity = Uncommon
  val health: Health = Health(40)
  val attack: Attack = Attack(45)
  val defense: Defense = Defense(35)
  val speed: Speed = Speed(70)

  pTypes_=(List(
    Grass
  ))
  moves_=(List(
    Scratch,
    Leer,
    RazorLeaf,
    SeedBomb
  ))
}

class Sceptile extends Pokemon {
  val pName: String = "Sceptile"
  val rarity: Rarity = SuperRare
  val health: Health = Health(70)
  val attack: Attack = Attack(85)
  val defense: Defense = Defense(65)
  val speed: Speed = Speed(120)

  pTypes_=(List(
    Grass
  ))
  moves_=(List(
    SolarBeam,
    Agility,
    Screech,
    LeafStorm
  ))
}

class Nuzleaf extends Pokemon {
  val pName: String = "Nuzleaf"
  val rarity: Rarity = Uncommon
  val health: Health = Health(70)
  val attack: Attack = Attack(70)
  val defense: Defense = Defense(40)
  val speed: Speed = Speed(60)

  pTypes_=(List(
    Grass,
    Dark
  ))
  moves_=(List(
    RazorLeaf,
    Harden,
    Astonish
  ))
}

class Breloom extends Pokemon {
  val pName: String = "Breloom"
  val rarity: Rarity = Rare
  val health: Health = Health(60)
  val attack: Attack = Attack(120)
  val defense: Defense = Defense(80)
  val speed: Speed = Speed(70)

  pTypes_=(List(
    Grass,
    Fighting
  ))
  moves_=(List(
    SeedBomb,
    ForcePalm,
    MachPunch,
    DynamicPunch
  ))
}

class Umbreon extends Pokemon {
  val pName: String = "Umbreon"
  val rarity: Rarity = Rare
  val health: Health = Health(95)
  val attack: Attack = Attack(65)
  val defense: Defense = Defense(110)
  val speed: Speed = Speed(65)

  pTypes_=(List(
    Dark
  ))
  moves_=(List(
    DarkPulse,
    Bite,
    ShadowBall
  ))
}

class Tyranitar extends Pokemon {
  val pName: String = "Tyranitar"
  val rarity: Rarity = SuperRare
  val health: Health = Health(100)
  val attack: Attack = Attack(134)
  val defense: Defense = Defense(110)
  val speed: Speed = Speed(61)

  pTypes_=(List(
    Rock,
    Dark
  ))
  moves_=(List(
    Crunch,
    StoneEdge,
    Earthquake,
    IronDefense
  ))
}

class Carvanha extends Pokemon {
  val pName: String = "Carvanha"
  val rarity: Rarity = Common
  val health: Health = Health(35)
  val attack: Attack = Attack(60)
  val defense: Defense = Defense(25)
  val speed: Speed = Speed(60)

  pTypes_=(List(
    Water,
    Dark
  ))
  moves_=(List(
    Bite,
    Leer
  ))
}

class Darkrai extends Pokemon {
  val pName: String = "Darkrai"
  val rarity: Rarity = UltraRare
  val health: Health = Health(70)
  val attack: Attack = Attack(90)
  val defense: Defense = Defense(90)
  val speed: Speed = Speed(125)

  pTypes_=(List(
    Dark
  ))
  moves_=(List(
    DarkPulse,
    DreamEater,
    DarkVoid,
    SuckerPunch
  ))
}

class Pawniard extends Pokemon {
  val pName: String = "Pawniard"
  val rarity: Rarity = Uncommon
  val health: Health = Health(45)
  val attack: Attack = Attack(75)
  val defense: Defense = Defense(70)
  val speed: Speed = Speed(60)

  pTypes_=(List(
    Dark,
    Steel
  ))
  moves_=(List(
    MetalClaw,
    FuryCutter,
    IronDefense
  ))
}

class Deino extends Pokemon {
  val pName: String = "Deino"
  val rarity: Rarity = Uncommon
  val health: Health = Health(52)
  val attack: Attack = Attack(65)
  val defense: Defense = Defense(50)
  val speed: Speed = Speed(38)

  pTypes_=(List(
    Dark,
    Dragon
  ))
  moves_=(List(
    Bite,
    DragonDance,
    DragonClaw
  ))
}

class Onix extends Pokemon {
  val pName: String = "Onix"
  val rarity: Rarity = Uncommon
  val health: Health = Health(35)
  val attack: Attack = Attack(42)
  val defense: Defense = Defense(130)
  val speed: Speed = Speed(60)

  pTypes_=(List(
    Rock,
    Ground
  ))
  moves_=(List(
    RockThrow,
    Harden,
    Earthquake
  ))
}

class Lairon extends Pokemon {
  val pName: String = "Lairon"
  val rarity: Rarity = Uncommon
  val health: Health = Health(60)
  val attack: Attack = Attack(60)
  val defense: Defense = Defense(100)
  val speed: Speed = Speed(45)

  pTypes_=(List(
    Steel,
    Rock
  ))
  moves_=(List(
    IronDefense,
    RockSlide
  ))
}

class Solrock extends Pokemon {
  val pName: String = "Solrock"
  val rarity: Rarity = Rare
  val health: Health = Health(70)
  val attack: Attack = Attack(95)
  val defense: Defense = Defense(85)
  val speed: Speed = Speed(70)

  pTypes_=(List(
    Rock,
    Psychic
  ))
  moves_=(List(
    Confusion,
    SolarBeam,
    Harden
  ))
}

class Bonsly extends Pokemon {
  val pName: String = "Bonsly"
  val rarity: Rarity = Common
  val health: Health = Health(40)
  val attack: Attack = Attack(60)
  val defense: Defense = Defense(75)
  val speed: Speed = Speed(10)

  pTypes_=(List(
    Rock
  ))
  moves_=(List(
    RockThrow,
    Harden
  ))
}

class Bastiodon extends Pokemon {
  val pName: String = "Bastiodon"
  val rarity: Rarity = Rare
  val health: Health = Health(60)
  val attack: Attack = Attack(52)
  val defense: Defense = Defense(168)
  val speed: Speed = Speed(30)

  pTypes_=(List(
    Rock,
    Steel
  ))
  moves_=(List(
    IronDefense,
    MetalClaw,
    IronHead
  ))
}

class Regirock extends Pokemon {
  val pName: String = "Regirock"
  val rarity: Rarity = UltraRare
  val health: Health = Health(80)
  val attack: Attack = Attack(100)
  val defense: Defense = Defense(200)
  val speed: Speed = Speed(50)

  pTypes_=(List(
    Rock
  ))
  moves_=(List(
    Earthquake,
    IronDefense,
    HammerArm,
    StoneEdge
  ))
}

class Mankey extends Pokemon {
  val pName: String = "Mankey"
  val rarity: Rarity = Common
  val health: Health = Health(40)
  val attack: Attack = Attack(65)
  val defense: Defense = Defense(35)
  val speed: Speed = Speed(50)

  pTypes_=(List(
    Fighting
  ))
  moves_=(List(
    TailWhip,
    LowKick
  ))
}

class Machamp extends Pokemon {
  val pName: String = "Machamp"
  val rarity: Rarity = SuperRare
  val health: Health = Health(90)
  val attack: Attack = Attack(130)
  val defense: Defense = Defense(80)
  val speed: Speed = Speed(55)

  pTypes_=(List(
    Fighting
  ))
  moves_=(List(
    ScaryFace,
    DynamicPunch,
    BulkUp,
    SandAttack
  ))
}

class Heracross extends Pokemon {
  val pName: String = "Heracross"
  val rarity: Rarity = SuperRare
  val health: Health = Health(80)
  val attack: Attack = Attack(125)
  val defense: Defense = Defense(75)
  val speed: Speed = Speed(85)

  pTypes_=(List(
    Bug,
    Fighting
  ))
  moves_=(List(
    FuryCutter,
    SwordsDance,
    HornAttack,
    Harden
  ))
}

class Blaziken extends Pokemon {
  val pName: String = "Blaziken"
  val rarity: Rarity = SuperRare
  val health: Health = Health(80)
  val attack: Attack = Attack(120)
  val defense: Defense = Defense(70)
  val speed: Speed = Speed(80)

  pTypes_=(List(
    Fire,
    Fighting
  ))
  moves_=(List(
    BlazeKick,
    BulkUp,
    FocusEnergy,
    FirePunch
  ))
}

class Lucario extends Pokemon {
  val pName: String = "Lucario"
  val rarity: Rarity = SuperRare
  val health: Health = Health(70)
  val attack: Attack = Attack(110)
  val defense: Defense = Defense(70)
  val speed: Speed = Speed(90)

  pTypes_=(List(
    Fighting,
    Steel
  ))
  moves_=(List(
    ForcePalm,
    MetalClaw,
    SwordsDance,
    CloseCombat
  ))
}

class Nidoran extends Pokemon {
  val pName: String = "Nidoran"
  val rarity: Rarity = Common
  val health: Health = Health(55)
  val attack: Attack = Attack(42)
  val defense: Defense = Defense(52)
  val speed: Speed = Speed(40)

  pTypes_=(List(
    Poison
  ))
  moves_=(List(
    PoisonSting,
    Growl
  ))
}

class Arbok extends Pokemon {
  val pName: String = "Arbok"
  val rarity: Rarity = Rare
  val health: Health = Health(60)
  val attack: Attack = Attack(85)
  val defense: Defense = Defense(69)
  val speed: Speed = Speed(80)

  pTypes_=(List(
    Poison
  ))
  moves_=(List(
    PoisonSting,
    Bite,
    ScaryFace,
    IceFang
  ))
}

class Kakuna extends Pokemon {
  val pName: String = "Kakuna"
  val rarity: Rarity = Common
  val health: Health = Health(45)
  val attack: Attack = Attack(25)
  val defense: Defense = Defense(50)
  val speed: Speed = Speed(35)

  pTypes_=(List(
    Bug,
    Poison
  ))
  moves_=(List(
    Harden,
    Tackle
  ))
}

class Weezing extends Pokemon {
  val pName: String = "Weezing"
  val rarity: Rarity = Rare
  val health: Health = Health(65)
  val attack: Attack = Attack(90)
  val defense: Defense = Defense(100)
  val speed: Speed = Speed(60)

  pTypes_=(List(
    Poison
  ))
  moves_=(List(
    SludgeBomb,
    Smog,
    Explosion,
    Sludge
  ))
}

class Crobat extends Pokemon {
  val pName: String = "Crobat"
  val rarity: Rarity = SuperRare
  val health: Health = Health(85)
  val attack: Attack = Attack(90)
  val defense: Defense = Defense(80)
  val speed: Speed = Speed(130)

  pTypes_=(List(
    Poison,
    Flying
  ))
  moves_=(List(
    AirSlash,
    PoisonFang,
    Screech,
    ConfuseRay
  ))
}

class Steelix extends Pokemon {
  val pName: String = "Steelix"
  val rarity: Rarity = SuperRare
  val health: Health = Health(75)
  val attack: Attack = Attack(85)
  val defense: Defense = Defense(200)
  val speed: Speed = Speed(30)

  pTypes_=(List(
    Steel,
    Ground
  ))
  moves_=(List(
    IronDefense,
    Earthquake,
    IronTail,
    Sandstorm
  ))
}

class Regigigas extends Pokemon {
  val pName: String = "Regigigas"
  val rarity: Rarity = UltraRare
  val health: Health = Health(110)
  val attack: Attack = Attack(160)
  val defense: Defense = Defense(110)
  val speed: Speed = Speed(100)

  pTypes_=(List(
    Normal
  ))
  moves_=(List(
    GigaImpact,
    ZenHeadbutt,
    AncientPower,
    HammerArm
  ))
}

class Dusknoir extends Pokemon {
  val pName: String = "Dusknoir"
  val rarity: Rarity = SuperRare
  val health: Health = Health(45)
  val attack: Attack = Attack(100)
  val defense: Defense = Defense(135)
  val speed: Speed = Speed(45)

  pTypes_=(List(
    Ghost
  ))
  moves_=(List(
    FirePunch,
    ShadowPunch,
    ConfuseRay,
    IcePunch
  ))
}

class Hariyama extends Pokemon {
  val pName: String = "Hariyama"
  val rarity: Rarity = SuperRare
  val health: Health = Health(144)
  val attack: Attack = Attack(120)
  val defense: Defense = Defense(60)
  val speed: Speed = Speed(50)
  override val critical: Critical = Critical(2)

  pTypes_=(List(
    Fighting
  ))
  moves_=(List(
    ForcePalm,
    SwordsDance,
    CloseCombat,
    FocusEnergy
  ))
}

class Arceus extends Pokemon {
  val pName: String = "Arceus"
  val rarity: Rarity = UltraRare
  val health: Health = Health(116)
  val attack: Attack = Attack(120)
  val defense: Defense = Defense(120)
  val speed: Speed = Speed(120)

  pTypes_=(List(
    Normal
  ))
  moves_=(List(
    HyperBeam,
    SwordsDance,
    ExtremeSpeed,
    ScaryFace
  ))
}

class Reshiram extends Pokemon {
  val pName: String = "Reshiram"
  val rarity: Rarity = UltraRare
  val health: Health = Health(120)
  val attack: Attack = Attack(120)
  val defense: Defense = Defense(100)
  val speed: Speed = Speed(90)

  pTypes_=(List(
    Dragon,
    Fire
  ))
  moves_=(List(
    FusionFlare,
    DragonDance,
    AncientPower,
    BlueFlare
  ))
}

class Magneton extends Pokemon {
  val pName: String = "Magneton"
  val rarity: Rarity = Rare
  val health: Health = Health(50)
  val attack: Attack = Attack(60)
  val defense: Defense = Defense(95)
  val speed: Speed = Speed(70)

  pTypes_=(List(
    Electric,
    Steel
  ))
  moves_=(List(
    ThunderShock,
    ThunderWave,
    FlashCannon
  ))
}

class Electabuzz extends Pokemon {
  val pName: String = "Electabuzz"
  val rarity: Rarity = Rare
  val health: Health = Health(65)
  val attack: Attack = Attack(83)
  val defense: Defense = Defense(57)
  val speed: Speed = Speed(105)

  pTypes_=(List(
    Electric
  ))
  moves_=(List(
    ThunderShock,
    ThunderPunch,
    ThunderWave
  ))
}

class Raikou extends Pokemon {
  val pName: String = "Raikou"
  val rarity: Rarity = UltraRare
  val health: Health = Health(90)
  val attack: Attack = Attack(115)
  val defense: Defense = Defense(75)
  val speed: Speed = Speed(115)

  pTypes_=(List(
    Electric
  ))
  moves_=(List(
    ThunderFang,
    Thunder,
    ExtremeSpeed,
    SwordsDance
  ))
}

class Pinsir extends Pokemon {
  val pName: String = "Pinsir"
  val rarity: Rarity = SuperRare
  val health: Health = Health(65)
  val attack: Attack = Attack(125)
  val defense: Defense = Defense(100)
  val speed: Speed = Speed(85)

  pTypes_=(List(
    Bug
  ))
  moves_=(List(
    FuryCutter,
    SwordsDance,
    XScissor,
    FocusEnergy
  ))
}

class Scizor extends Pokemon {
  val pName: String = "Scizor"
  val rarity: Rarity = SuperRare
  val health: Health = Health(70)
  val attack: Attack = Attack(130)
  val defense: Defense = Defense(100)
  val speed: Speed = Speed(65)

  pTypes_=(List(
    Bug,
    Steel
  ))
  moves_=(List(
    FuryCutter,
    IronDefense,
    XScissor,
    IronHead
  ))
}

class Caterpie extends Pokemon {
  val pName: String = "Caterpie"
  val rarity: Rarity = Common
  val health: Health = Health(45)
  val attack: Attack = Attack(30)
  val defense: Defense = Defense(35)
  val speed: Speed = Speed(45)

  pTypes_=(List(
    Bug
  ))
  moves_=(List(
    Tackle,
    StringShot
  ))
}

class Meowth extends Pokemon {
  val pName: String = "Meowth"
  val rarity: Rarity = Common
  val health: Health = Health(40)
  val attack: Attack = Attack(45)
  val defense: Defense = Defense(35)
  val speed: Speed = Speed(70)

  pTypes_=(List(
    Normal
  ))
  moves_=(List(
    Scratch,
    Growl
  ))
}

class Farfetchd extends Pokemon {
  val pName: String = "Farfetchd"
  val rarity: Rarity = SuperRare
  val health: Health = Health(62)
  val attack: Attack = Attack(135)
  val defense: Defense = Defense(95)
  val speed: Speed = Speed(65)

  pTypes_=(List(
    Normal,
    Flying
  ))
  moves_=(List(
    LeafBlade,
    SwordsDance,
    FocusEnergy,
    AirSlash
  ))
}

class Togepi extends Pokemon {
  val pName: String = "Togepi"
  val rarity: Rarity = Common
  val health: Health = Health(35)
  val attack: Attack = Attack(20)
  val defense: Defense = Defense(65)
  val speed: Speed = Speed(20)

  pTypes_=(List(
    Fairy
  ))
  moves_=(List(
    Charm,
    Pound
  ))
}

class Snubbull extends Pokemon {
  val pName: String = "Snubbull"
  val rarity: Rarity = Uncommon
  val health: Health = Health(60)
  val attack: Attack = Attack(80)
  val defense: Defense = Defense(50)
  val speed: Speed = Speed(30)

  pTypes_=(List(
    Fairy
  ))
  moves_=(List(
    Bite,
    Charm,
    Tackle
  ))
}

class Slowpoke extends Pokemon {
  val pName: String = "Slowpoke"
  val rarity: Rarity = Uncommon
  val health: Health = Health(90)
  val attack: Attack = Attack(65)
  val defense: Defense = Defense(65)
  val speed: Speed = Speed(15)

  pTypes_=(List(
    Water,
    Psychic
  ))
  moves_=(List(
    WaterGun,
    Confusion,
    Tackle
  ))
}

class Mewtwo extends Pokemon {
  val pName: String = "Mewtwo"
  val rarity: Rarity = UltraRare
  val health: Health = Health(106)
  val attack: Attack = Attack(110)
  val defense: Defense = Defense(90)
  val speed: Speed = Speed(130)

  pTypes_=(List(
    Psychic
  ))
  moves_=(List(
    Confusion,
    PowerSwap,
    AncientPower,
    FutureSight
  ))
}
