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
  val criticalHit: CriticalHit = CriticalHit()

  private val _level: Int = 5
  private var _pTypes: List[Type] = List()
  private var _moves: List[Move] = List()
  private lazy val _score: Double = calculateScore()
  private val _baseStatNorm: Double = 1.0
  private val _moveScoreNorm: Double = 200.0

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
