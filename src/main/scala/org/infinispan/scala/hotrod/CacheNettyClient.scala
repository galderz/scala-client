package org.infinispan.scala.hotrod

import java.util.concurrent.atomic.AtomicInteger

import io.netty.channel.{Channel, ChannelHandlerContext, SimpleChannelInboundHandler}

import scala.concurrent.{ExecutionContext, Promise, Future}
import scala.util.Success

private[hotrod] class CacheNettyClient[A, B](
    ch: Channel, handler: CacheClientHandler
) (implicit ec: ExecutionContext) extends CacheClient[A, B] {
  import CacheNettyClient._

  @volatile private var stopped = false

//  private val netty = Netty(this)
//  private val id = new AtomicInteger()
//  private val promises = new Array[Promise[ServerResponse]](IdRingSize)

//  private val connectPromise = Promise[CacheClient]()
//  private var disconnectFuture: Future[CacheClient] = _
//
//  private var currentContext: ChannelHandlerContext = _
//  private var commandPromise: Promise[ServerResponse] = _

  override def put(kv: (A, B)): Future[Unit] = allowed {
    val req = ClientRequests.Put(handler.nextId(), kv)
    handler.write(ch, req).map(_ => ())
  }

  override def get(k: A): Future[Option[B]] = allowed {
    val req = ClientRequests.Get[A](handler.nextId(), k)
    handler.write(ch, req).map(r => r.asInstanceOf[ServerResponses.Get[B]].v)
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


  //  private def write(request: ClientRequest): Future[ServerResponse] = {
//
//
////    promises()
////
////    // TODO: Synchronized not needed - keep track of id <-> request/response (maybe an indexed array?)
////    this.synchronized {
////      if (this.commandPromise != null)
////        throw new BusyClientException
////
////      val result = Promise[ServerResponse]()
////
////      this.currentContext.writeAndFlush(request).onFailure {
////        case e: Throwable => result.tryFailure(e)
////      }
////
////      this.commandPromise = result
////      this.commandPromise.future
////    }
//    null
//  }
//
//  override def channelRead0(ctx: ChannelHandlerContext, msg: ServerResponse): Unit = {
//    promises(msg.msgid.toInt).complete(Success(msg))
//  }
//
//
////  def connect(): Future[CacheClient] = {
////    netty.connect(host, port).onFailure {
////      case e : Throwable => connectPromise.failure(e)
////    }
////
////    connectPromise.future
////  }
////
////  def close(): Future[CacheClient] = {
////    if (currentContext != null && currentContext.channel().isActive && disconnectFuture == null) {
////      this.disconnectFuture = this.currentContext.close().map(v => this)
////    }
////
////    if (disconnectFuture == null) {
////      Promise.successful[CacheClient](this).future
////    } else {
////      disconnectFuture
////    }
////  }
//  override def channelActive(ctx: ChannelHandlerContext): Unit = super.channelActive(ctx)
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
