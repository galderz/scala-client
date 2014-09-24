package org.infinispan.scala.hotrod

private[hotrod] sealed abstract class ClientRequest(val id: Int, code: Id)

private[hotrod] object ClientRequests {
  case class KeyValue(override val id: Int, code: Id, kv: (Any, Any), ctx: Context)
    extends ClientRequest(id, code)

  case class Key(override val id: Int, code: Id, k: Any) extends ClientRequest(id, code)
}

