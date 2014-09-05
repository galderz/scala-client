package org.infinispan.scala.hotrod

import scala.concurrent.duration.Duration

private[hotrod] sealed abstract class ClientRequest(val code: Int, val id: Int)

private[hotrod] object ClientRequests {
  case class KeyValue(override val code: Int, override val id: Int,
      kv: (Any, Any), lifespan: Duration, maxidle: Duration)
    extends ClientRequest(code, id)

  case class Key(override val code: Int, override val id: Int, k: Any)
    extends ClientRequest(code, id)
}

