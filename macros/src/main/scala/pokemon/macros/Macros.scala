package pokemon.macros

import scala.reflect.macros.blackbox.Context

object Macros {
  def registerSubclasses[T](staticPackage: String): List[Class[_ <: T]] = macro registerSubclassesMacro[T]

  /**
    * Register all subclasses of a given type in a given package
    *
    * @param c
    * @param staticPackage
    * @return
    */
  def registerSubclassesMacro[T: c.WeakTypeTag](c: Context)(staticPackage: c.Expr[String]): c.Expr[List[Class[_ <: T]]] = {
    import c.universe._

    staticPackage.tree match {
      case Literal(Constant(packageName: String)) =>
        val baseType = weakTypeOf[T]
        val subclasses = c.mirror.staticPackage(packageName).typeSignature.members
          .filter(_.isClass)
          .filter(_.asClass.baseClasses.contains(baseType.typeSymbol))
          .map(_.asClass)

        val subclassesExpr = subclasses.map { subclass =>
          q"classOf[${subclass.toType}]"
        }

        c.Expr[List[Class[_ <: T]]](q"List(..$subclassesExpr)")

      case _ =>
        c.abort(c.enclosingPosition, "staticPackage must be a string literal")
    }
  }
}
