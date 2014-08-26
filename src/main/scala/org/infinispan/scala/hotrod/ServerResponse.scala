package org.infinispan.scala.hotrod

private[hotrod] sealed abstract class ServerResponse(val id: Int)

private[hotrod] object ServerResponses {
  case class Put(override val id: Int) extends ServerResponse(id)
  case class Get[B](override val id: Int, v: Option[B]) extends ServerResponse(id)
}
