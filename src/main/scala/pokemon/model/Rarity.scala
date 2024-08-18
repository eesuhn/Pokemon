package pokemon.model

abstract class Rarity {
  def weightageUpperBound: Double
  def weightageLowerBound: Double
}

object Common extends Rarity {
  override def weightageUpperBound: Double = 200.0
  override def weightageLowerBound: Double = 0.0
}

object Uncommon extends Rarity {
  override def weightageUpperBound: Double = 300.0
  override def weightageLowerBound: Double = Common.weightageUpperBound
}

object Rare extends Rarity {
  override def weightageUpperBound: Double = 400.0
  override def weightageLowerBound: Double = Uncommon.weightageUpperBound
}

object SuperRare extends Rarity {
  override def weightageUpperBound: Double = 500.0
  override def weightageLowerBound: Double = Rare.weightageUpperBound
}

object UltraRare extends Rarity {
  override def weightageUpperBound: Double = 650.0
  override def weightageLowerBound: Double = SuperRare.weightageUpperBound
}
