package org.infinispan.scala.hotrod

import java.util.concurrent.atomic.AtomicInteger

import io.netty.channel.{Channel, ChannelHandlerContext, SimpleChannelInboundHandler}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Promise, Future}
import scala.util.Success

class CacheClientHandler extends SimpleChannelInboundHandler[ServerResponse] {
  import CacheClientHandler._

  private val id = new AtomicInteger()
  private val promises = new mutable.ArraySeq[Promise[_ <: ServerResponse]](IdRingSize)

  def nextId(): Int = {
    id.getAndIncrement % IdRingSize
  }

  def write[T <: ServerResponse](ch: Channel, req: ClientRequest)(implicit ec: ExecutionContext): Future[T] = {
    val promise = Promise[T]()
    promises(req.id) = promise
    ch.writeAndFlush(req).onFailure {
      case e: Throwable => promise.tryFailure(e)
    }
    promise.future
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: ServerResponse): Unit = {
    // TODO: Deal with error responses
    val promises1 = promises(msg.id.toInt).asInstanceOf[Promise[ServerResponse]]
    promises1.complete(Success(msg))
  }

}

object CacheClientHandler {
  val IdRingSize = 128
}
