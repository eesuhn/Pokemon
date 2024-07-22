package pokemon.macros

import scala.reflect.macros.blackbox.Context

object Macros {

  /**
    * Register and return all subclasses of a given type in a given package
    *
    * @param staticPackage
    * @return
    */
  def registerSubclasses[T](staticPackage: String): List[Class[_ <: T]] = macro registerSubclassesMacro[T]

  /**
    * Macro: Register all subclasses of a given type in a given package
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
          .filter(sym =>
            sym.asClass.baseClasses.contains(baseType.typeSymbol) &&
            sym.asClass != baseType.typeSymbol
          )
          .map(_.asClass)

        val subclassesExpr = subclasses.map { subclass =>
          q"classOf[${subclass.toType}]"
        }

        c.Expr[List[Class[_ <: T]]](q"List(..$subclassesExpr)")

      case _ =>
        c.abort(c.enclosingPosition, "staticPackage must be a string literal")
    }
  }

  /**
    * Register and return all instances of a given type in a given package
    *
    * @param staticPackage
    * @return
    */
  def registerInstances[T](staticPackage: String): List[T] = macro registerInstancesMacro[T]

  /**
    * Macro: Register all instances of a given type in a given package
    *
    * @param c
    * @param staticPackage
    * @return
    */
  def registerInstancesMacro[T: c.WeakTypeTag](c: Context)(staticPackage: c.Expr[String]): c.Expr[List[T]] = {
    import c.universe._

    staticPackage.tree match {
      case Literal(Constant(packageName: String)) =>
        val baseType = weakTypeOf[T]
        val instances = c.mirror.staticPackage(packageName).typeSignature.members
          .filter(_.isModule)
          .filter(_.typeSignature <:< baseType)
          .map(_.asModule)

        val instancesExpr = instances.map { instance =>
          q"${instance}"
        }

        c.Expr[List[T]](q"List(..$instancesExpr)")

      case _ =>
        c.abort(c.enclosingPosition, "staticPackage must be a string literal")
    }
  }
}
