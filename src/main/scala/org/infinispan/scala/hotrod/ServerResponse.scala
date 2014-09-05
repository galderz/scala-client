package org.infinispan.scala.hotrod

private[hotrod] sealed abstract class ServerResponse(val id: Int)

private[hotrod] object ServerResponses {
  case class Empty(override val id: Int) extends ServerResponse(id)
  case class Maybe(override val id: Int, success: Boolean) extends ServerResponse(id)
  case class Failure(override val id: Int, t: Throwable) extends ServerResponse(id)
  case class Value[B](override val id: Int, v: Option[B]) extends ServerResponse(id)
}
