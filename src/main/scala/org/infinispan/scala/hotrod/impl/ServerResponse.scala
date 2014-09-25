package org.infinispan.scala.hotrod.impl

import org.infinispan.scala.hotrod.Versioned

private[impl] sealed abstract class ServerResponse(val id: Int)

private[impl] object ServerResponses {
  case class Empty(override val id: Int) extends ServerResponse(id)
  case class Maybe(override val id: Int, success: Boolean) extends ServerResponse(id)
  case class Failure(override val id: Int, t: Throwable) extends ServerResponse(id)
  case class Value[B](override val id: Int, v: Option[B]) extends ServerResponse(id)
  case class VersionedValue[B](override val id: Int, v: Option[Versioned[B]]) extends ServerResponse(id)
}
