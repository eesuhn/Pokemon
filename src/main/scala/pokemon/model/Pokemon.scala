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
    TailWhip,
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
