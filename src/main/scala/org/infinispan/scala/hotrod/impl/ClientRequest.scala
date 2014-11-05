package org.infinispan.scala.hotrod.impl

import org.infinispan.scala.hotrod.{Version, Context}

private[impl] sealed abstract class ClientRequest(val id: Int, code: Id)

private[impl] object ClientRequests {
  case class KeyValue(override val id: Int, code: Id, kv: (Any, Any), ctx: Context)
    extends ClientRequest(id, code)

  case class Key(override val id: Int, code: Id, k: Any, ctx: Context)
    extends ClientRequest(id, code)
}

