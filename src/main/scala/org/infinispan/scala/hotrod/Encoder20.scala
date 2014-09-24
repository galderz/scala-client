package org.infinispan.scala.hotrod

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

private[hotrod] class Encoder20 extends MessageToByteEncoder[ClientRequest] {

  val marshaller = new JavaMarshaller

  override def encode(ctx: ChannelHandlerContext, msg: ClientRequest, out: ByteBuf): Unit = {
    // Alternatively, match on msg.code with @switch and call asInstanceOf
    msg match {
      case kv: ClientRequests.KeyValue => encodeKeyValue(out, kv)
      case k: ClientRequests.Key => encodeKey(out, k)
    }
  }

  private def encodeKeyValue(buf: ByteBuf, msg: ClientRequests.KeyValue): Unit = {
    val k = marshaller.toBytes(msg.kv._1)
    val v = marshaller.toBytes(msg.kv._2)
    buf
      .writeByte(Constants.Req) // Magic
      .writeVLong(msg.id) // Message ID
      .writeByte(Constants.V20) // Version
      .writeByte(msg.code.id) // Operation code
      .writeByte(0) // Cache name length
      .writeVInt(0) // Flags
      .writeByte(Constants.ClientBasic) // Client intelligence
      .writeVInt(0) // Topology ID
      .writeRangedBytes(k) // Key length + Key
      .writeVInt(msg.ctx[Lifespan].expiry.toSeconds) // Lifespan
      .writeVInt(msg.ctx[MaxIdle].expiry.toSeconds) // Max Idle
      .writeRangedBytes(v) // Value length + Value
  }

  private def encodeKey(buf: ByteBuf, msg: ClientRequests.Key): Unit = {
    val k = marshaller.toBytes(msg.k)
    buf
      .writeByte(Constants.Req) // Magic
      .writeVLong(msg.id) // Message ID
      .writeByte(Constants.V20) // Version
      .writeByte(msg.code.id) // Operation code
      .writeByte(0) // Cache name length
      .writeVInt(0) // Flags
      .writeByte(Constants.ClientBasic) // Client intelligence
      .writeVInt(0) // Topology ID
      .writeRangedBytes(k) // Key length + Key
  }

}
