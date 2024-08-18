package pokemon.model

import pokemon.macros.Macros

object RarityRegistry {
  private var _rarities: List[Rarity] = List.empty

  def registerRarities(newRarities: List[Rarity]): Unit = {
    _rarities = newRarities ::: _rarities
  }

  def rarities: List[Rarity] = _rarities

  registerRarities(Macros.registerInstances[Rarity]("pokemon.model"))
}

abstract class Rarity {
  def weightageUpperBound: Double
  def weightageLowerBound: Double

  /**
    * Gacha chance based on `upper bound` of opposite rarity
    *
    * The greater the value, the greater the chance
    *
    * @return
    */
  def gachaChance: Double
}

object Common extends Rarity {
  override def weightageUpperBound: Double = 200.0
  override def weightageLowerBound: Double = 0.0
  override def gachaChance: Double = UltraRare.weightageUpperBound
}

object Uncommon extends Rarity {
  override def weightageUpperBound: Double = 300.0
  override def weightageLowerBound: Double = Common.weightageUpperBound
  override def gachaChance: Double = SuperRare.weightageUpperBound
}

object Rare extends Rarity {
  override def weightageUpperBound: Double = 400.0
  override def weightageLowerBound: Double = Uncommon.weightageUpperBound
  override def gachaChance: Double = Rare.weightageUpperBound
}

object SuperRare extends Rarity {
  override def weightageUpperBound: Double = 500.0
  override def weightageLowerBound: Double = Rare.weightageUpperBound
  override def gachaChance: Double = Uncommon.weightageUpperBound
}

object UltraRare extends Rarity {
  override def weightageUpperBound: Double = 650.0
  override def weightageLowerBound: Double = SuperRare.weightageUpperBound
  override def gachaChance: Double = Common.weightageUpperBound
}
