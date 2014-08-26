package org.infinispan.scala.hotrod

import java.util

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

import scala.annotation.switch

private[hotrod] class Decoder20 extends ByteToMessageDecoder {

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
        case ResponseOps.Put.value => out.add(ServerResponses.Put(respId))
        case ResponseOps.Get.value =>
          val value =
            status match {
              case Constants.NotFound => None
              case Constants.Success => in.readMaybeRangedBytes().map(marshaller.fromBytes)
            }
          out.add(ServerResponses.Get(respId, value))
      }
    }
  }

}
