package org.infinispan.scala.hotrod.impl

import java.util

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.infinispan.scala.hotrod.Versioned

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
        case ResponseIds.Remove => appendMaybeResponse(status, out, respId)
        case ResponseIds.ContainsKey => appendMaybeResponse(status, out, respId)
        case ResponseIds.GetWithVersion =>
          val versioned = status match {
            case Constants.NotFound => None
            case Constants.Success =>
              val version = in.readLong()
              in.readMaybeRangedBytes()
                .map(marshaller.fromBytes)
                .map(Versioned(_, version))
          }
          out.add(ServerResponses.VersionedValue(respId, versioned))
        case ResponseIds.PutIfAbsent | ResponseIds.Replace | ResponseIds.ReplaceVersioned =>
          val success = status match {
            case Constants.Success => true
            case Constants.NotApplied => false
            case Constants.NotFound => false
          }
          out.add(ServerResponses.Maybe(respId, success))
        case _ =>
      }
    }
  }

  private def appendMaybeResponse(status: Byte, out: util.List[AnyRef], respId: Int): Boolean = {
    val success = status match {
      case Constants.Success => true
      case Constants.NotFound => false
    }
    out.add(ServerResponses.Maybe(respId, success))
  }

}
