package org.infinispan.scala.hotrod

import java.util.concurrent.atomic.{AtomicLong, AtomicInteger}

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

import scala.annotation.switch

private[hotrod] class Encoder20 extends MessageToByteEncoder[ClientRequest] {

  val marshaller = new JavaMarshaller

  override def encode(ctx: ChannelHandlerContext, msg: ClientRequest, out: ByteBuf): Unit = {
    // Alternatively, match on msg.code with @switch and call asInstanceOf
    msg match {
      case kv: ClientRequests.KeyValue => encodeKeyValue(out, kv)
      case k: ClientRequests.Key => encodeKey(out, k)
    }

//    (msg.code: @switch) match {
//      case RequestOps.Put => encodeKeyValue(out, msg.asInstanceOf[ClientRequests.Put[_, _]])
//      case RequestOps.Get => encodeGet(out, msg.asInstanceOf[ClientRequests.Get[_]])
//    }
//    (msg.code: @switch) match {
//      case RequestOps.Put => encodeKeyValue(out, msg.asInstanceOf[ClientRequests.Put[_, _]])
//      case RequestOps.Get => encodeGet(out, msg.asInstanceOf[ClientRequests.Get[_]])
//    }
  }

  private def encodeKeyValue(buf: ByteBuf, msg: ClientRequests.KeyValue): Unit = {
    val k = marshaller.toBytes(msg.kv._1)
    val v = marshaller.toBytes(msg.kv._2)
    buf
      .writeByte(Constants.Req) // Magic
      .writeVLong(msg.id) // Message ID
      .writeByte(Constants.V20) // Version
      .writeByte(msg.code.value) // Operation code
      .writeByte(0) // Cache name length
      .writeVInt(0) // Flags
      .writeByte(Constants.ClientBasic) // Client intelligence
      .writeVInt(0) // Topology ID
      .writeRangedBytes(k) // Key length + Key
      .writeVInt(msg.lifespan.toSeconds.toInt) // Lifespan
      .writeVInt(msg.maxidle.toSeconds.toInt) // Max Idle
      .writeRangedBytes(v) // Value length + Value
  }

  private def encodeKey(buf: ByteBuf, msg: ClientRequests.Key): Unit = {
    val k = marshaller.toBytes(msg.k)
    buf
      .writeByte(Constants.Req) // Magic
      .writeVLong(msg.id) // Message ID
      .writeByte(Constants.V20) // Version
      .writeByte(msg.code.value) // Operation code
      .writeByte(0) // Cache name length
      .writeVInt(0) // Flags
      .writeByte(Constants.ClientBasic) // Client intelligence
      .writeVInt(0) // Topology ID
      .writeRangedBytes(k) // Key length + Key
  }

}
