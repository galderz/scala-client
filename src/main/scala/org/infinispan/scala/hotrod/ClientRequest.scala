package org.infinispan.scala.hotrod

private[hotrod] sealed abstract class ClientRequest(val code: RequestOp, val id: Int)

private[hotrod] object ClientRequests {
  case class Put[A, B](override val id: Int, kv: (A, B))
    extends ClientRequest(RequestOps.Put, id)
  case class Get[A](override val id: Int, k: A)
    extends ClientRequest(RequestOps.Get, id)
}

