package org.infinispan.scala.hotrod

import java.util.concurrent.atomic.{AtomicLong, AtomicInteger}

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

import scala.annotation.switch

private[hotrod] class Encoder20 extends MessageToByteEncoder[ClientRequest] {

  val marshaller = new JavaMarshaller

  override def encode(ctx: ChannelHandlerContext, msg: ClientRequest, out: ByteBuf): Unit = {
    (msg.code: @switch) match {
      case RequestOps.Put => encodePut(out, msg.asInstanceOf[ClientRequests.Put[_, _]])
      case RequestOps.Get => encodeGet(out, msg.asInstanceOf[ClientRequests.Get[_]])
    }
  }

  private def encodePut(buf: ByteBuf, msg: ClientRequests.Put[_, _]): Unit = {
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
      .writeVInt(0) // Lifespan
      .writeVInt(0) // Max Idle
      .writeRangedBytes(v) // Value length + Value
  }

  private def encodeGet(buf: ByteBuf, msg: ClientRequests.Get[_]): Unit = {
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
