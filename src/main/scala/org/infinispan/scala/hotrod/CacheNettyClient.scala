package org.infinispan.scala.hotrod

import io.netty.channel.Channel
import org.infinispan.scala.hotrod.ServerResponses._

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Success

private[hotrod] class CacheNettyClient[A, B](
    ch: Channel, handler: CacheClientHandler
) (implicit ec: ExecutionContext) extends CacheClient[A, B] {
  import org.infinispan.scala.hotrod.CacheNettyClient._

  @volatile private var stopped = false

  override def put(kv: (A, B))(implicit ctx: Context): Future[Unit] = allowed {
    val req = ClientRequests.KeyValue(handler.nextId(), RequestIds.Put, kv, ctx)
    handler.write[Empty](ch, req).map(_ => ())
  }

  override def get(k: A): Future[Option[B]] = allowed {
    val req = ClientRequests.Key(handler.nextId(), RequestIds.Get, k)
    handler.write[Value[B]](ch, req).map(r => r.v)
  }

  override def remove(k: A): Future[Unit] = allowed {
    val req = ClientRequests.Key(handler.nextId(), RequestIds.Remove, k)
    handler.write[Empty](ch, req).map(_ => ())
  }

  override def putIfAbsent(kv: (A, B))(implicit ctx: Context): Future[Boolean] = allowed {
    val req = ClientRequests.KeyValue(handler.nextId(), RequestIds.PutIfAbsent, kv, ctx)
    handler.write[Maybe](ch, req).map(_.success)
  }

  override def stop(): Future[Unit] = {
    stopped = true
    val p = Promise[Unit]()
    ch.close().map(_ => p.complete(Success(Unit)))
    p.future
  }

  def allowed[T](operation: => Future[T]): Future[T] = {
    if (stopped) {
      val p = Promise[T]()
      p.failure(new StoppedException())
      p.future
    } else {
      operation
    }
  }

}

private[hotrod] object CacheNettyClient {
  val IdRingSize = 128

  class StoppedException() extends IllegalStateException("Stopped, no further operations allowed")

  def apply[A, B](host: String, port: Int)(implicit ec: ExecutionContext): Future[CacheNettyClient[A, B]] = {
    val handler = new CacheClientHandler()
    val netty = Netty(handler)
    netty.connect(host, port).map(ch => new CacheNettyClient[A, B](ch, handler))
  }
}
