package org.infinispan.scala.hotrod

import java.util

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

import scala.annotation.tailrec

class Decoder20 extends ByteToMessageDecoder {

  override def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]): Unit = {
    for {
      magic <- readFirstByte(in)
      msgid <- readVLong(in)
    }

//    if (in.readableBytes() >= 1) {
//      in.markReaderIndex()
//      in.readByte() // Magic
//    }
  }

  def readFirstByte(in: ByteBuf): Option[Byte] = {
    if (in.readableBytes() >= 1) {
      in.markReaderIndex()
      Some(in.readByte()) // Magic
    } else None
  }

  def readVLong(in: ByteBuf): Option[Long] = {
    if (in.readableBytes() >= 1) {
      in.markReaderIndex()
      val b = in.readByte
      @tailrec def read(in: ByteBuf, b: Byte, shift: Int, i: Long, count: Int): Option[Long] = {
        if ((b & 0x80) == 0) Some(i)
        else {
          if (count > 9)
            throw new IllegalStateException(
              "Stream corrupted.  A variable length long cannot be longer than 9 bytes.")

          if (in.readableBytes() >= 1) {
            val bb = in.readByte
            read(in, bb, shift + 7, i | (bb & 0x7FL) << shift, count + 1)
          } else {
            in.resetReaderIndex()
            None
          }
        }
      }
      read(in, b, 7, b & 0x7F, 1)
    } else {
      in.resetReaderIndex()
      None
    }
  }

}
