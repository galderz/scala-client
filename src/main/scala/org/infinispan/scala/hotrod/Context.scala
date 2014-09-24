package org.infinispan.scala.hotrod

// Inspired by Finagle

/**
 * A parameter map.
 */
trait Context {
  /**
   * Get the current value of the P-typed parameter.
   */
  def apply[P: Param]: P

  /**
   * Returns true if there is a non-default value for
   * the P-typed parameter.
   */
  def contains[P: Param]: Boolean

  /**
   * Produce a new parameter map, overriding any previous
   * `P`-typed value.
   */
  def +[P: Param](p: P): Context
}

object Context {
  private case class Ctx(map: Map[Param[_], Any]) extends Context {
    def apply[P](implicit param: Param[P]): P =
      map.get(param) match {
        case Some(v) => v.asInstanceOf[P]
        case None => param.default
      }

    def contains[P](implicit param: Param[P]): Boolean =
      map.contains(param)

    def +[P](p: P)(implicit param: Param[P]): Context =
      copy(map + (param -> p))
  }

  /**
   * The empty parameter map.
   */
  val empty: Context = Ctx(Map.empty)
}

/**
 * A typeclass representing P-typed elements, eligible as
 * parameters for stack configuration. Note that the typeclass
 * instance itself is used as the key in parameter maps; thus
 * typeclasses should be persistent:
 *
 * {{{
 * case class Multiplier(i: Int)
 * implicit object Multiplier extends Stack.Param[Multiplier] {
 *   val default = Multiplier(123)
 * }
 * }}}
 */
trait Param[P] {
  def default: P
}

case class Lifespan(expiry: Expiry)
case class MaxIdle(expiry: Expiry)