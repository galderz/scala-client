package org.infinispan.scala.hotrod.impl

import java.util

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

import scala.annotation.switch

private[impl] class Decoder20 extends ByteToMessageDecoder {

  val marshaller = new JavaMarshaller

  override def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]): Unit = {
    for {
      magic <- in.readMaybeByte()
      id <- in.readMaybeVLong()
      op <- in.readMaybeByte()
      status <- in.readMaybeByte()
      topochange <- in.readMaybeByte()
    } yield {
      val respId = id.toInt
      (op: @switch) match {
        case ResponseIds.Put => out.add(ServerResponses.Empty(respId))
        case ResponseIds.Get =>
          val value = status match {
            case Constants.NotFound => None
            case Constants.Success => in.readMaybeRangedBytes().map(marshaller.fromBytes)
          }
          out.add(ServerResponses.Value(respId, value))
        case ResponseIds.Remove => out.add(ServerResponses.Empty(respId))
        case ResponseIds.PutIfAbsent =>
          val success = status match {
            case Constants.Success => true
            case Constants.NotApplied => false
          }
        out.add(ServerResponses.Maybe(respId, success))
        case _ =>
      }
    }
  }

}
