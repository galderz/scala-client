package org.infinispan.scala

package object hotrod {

  type Bytes = Array[Byte]

  import scala.language.implicitConversions

  implicit object ExpiryLifespan extends Param[Lifespan] {
    val default = Lifespan(Never)
  }

  implicit object ExpiryMaxIdle extends Param[MaxIdle] {
    val default = MaxIdle(Never)
  }

  implicit object EntryVersion extends Param[Version] {
    val default = Version(None)
  }

}
