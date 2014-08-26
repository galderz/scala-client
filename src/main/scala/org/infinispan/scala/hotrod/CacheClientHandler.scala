package org.infinispan.scala.hotrod

import java.util.concurrent.atomic.AtomicInteger

import io.netty.channel.{Channel, ChannelHandlerContext, SimpleChannelInboundHandler}

import scala.concurrent.{ExecutionContext, Promise, Future}
import scala.util.Success

class CacheClientHandler extends SimpleChannelInboundHandler[ServerResponse] {
  import CacheClientHandler._

  private val id = new AtomicInteger()
  private val promises = new Array[Promise[ServerResponse]](IdRingSize)

  def nextId(): Int = {
    id.getAndIncrement % IdRingSize
  }

  def write(ch: Channel, req: ClientRequest)(implicit ec: ExecutionContext): Future[ServerResponse] = {
    val promise = Promise[ServerResponse]()
    promises(req.id) = promise
    ch.writeAndFlush(req).onFailure {
      case e: Throwable => promise.tryFailure(e)
    }
    promise.future
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: ServerResponse): Unit = {
    // TODO: Deal with error responses
    promises(msg.id.toInt).complete(Success(msg))
  }

}

object CacheClientHandler {
  val IdRingSize = 128
}
