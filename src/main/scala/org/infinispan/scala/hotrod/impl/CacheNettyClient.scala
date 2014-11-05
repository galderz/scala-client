package org.infinispan.scala.hotrod.impl

import io.netty.channel.Channel
import org.infinispan.scala.hotrod.CacheClient.StoppedException
import org.infinispan.scala.hotrod._
import org.infinispan.scala.hotrod.impl.ServerResponses._

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Success

private[impl] class CacheNettyClient[A, B](
    ch: Channel, handler: CacheClientHandler
) (implicit ec: ExecutionContext) extends CacheClient[A, B] {

  @volatile private var stopped = false

  override def put(kv: (A, B))(implicit ctx: Context): Future[Unit] = allowed {
    val req = ClientRequests.KeyValue(handler.nextId(), RequestIds.Put, kv, ctx)
    handler.write[Empty](ch, req).map(_ => ())
  }

  override def get(k: A): Future[Option[B]] = allowed {
    val req = ClientRequests.Key(handler.nextId(), RequestIds.Get, k, Context.empty)
    handler.write[Value[B]](ch, req).map(r => r.v)
  }

  def contains(k: A): Future[Boolean] = {
    val req = ClientRequests.Key(handler.nextId(), RequestIds.ContainsKey, k, Context.empty)
    handler.write[Maybe](ch, req).map(r => r.success)
  }

  def versioned(k: A): Future[Option[Versioned[B]]] = {
    val req = ClientRequests.Key(handler.nextId(), RequestIds.GetWithVersion, k, Context.empty)
    handler.write[VersionedValue[B]](ch, req).map(r => r.v)
  }

  override def remove(k: A)(implicit ctx: Context): Future[Boolean] = allowed {
    val reqid = if (ctx.contains[Version]) RequestIds.RemoveVersioned else RequestIds.Remove
    val req = ClientRequests.Key(handler.nextId(), reqid, k, ctx)
    handler.write[Maybe](ch, req).map(_.success)
  }

  override def putIfAbsent(kv: (A, B))(implicit ctx: Context): Future[Boolean] = allowed {
    val req = ClientRequests.KeyValue(handler.nextId(), RequestIds.PutIfAbsent, kv, ctx)
    handler.write[Maybe](ch, req).map(_.success)
  }

  override def replace(kv: (A, B))(implicit ctx: Context = Context.empty): Future[Boolean] = allowed {
    val reqid = if (ctx.contains[Version]) RequestIds.ReplaceVersioned else RequestIds.Replace
    val req = ClientRequests.KeyValue(handler.nextId(), reqid, kv, ctx)
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

  def apply[A, B](host: String, port: Int)(implicit ec: ExecutionContext): Future[CacheNettyClient[A, B]] = {
    val handler = new CacheClientHandler()
    val netty = Netty(handler)
    netty.connect(host, port).map(ch => new CacheNettyClient[A, B](ch, handler))
  }
}
