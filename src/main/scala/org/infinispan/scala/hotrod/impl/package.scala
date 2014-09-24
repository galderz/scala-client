package org.infinispan.scala.hotrod

import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelFutureListener, Channel, ChannelFuture}

import scala.annotation.tailrec
import scala.concurrent.{Promise, Future}

package object impl {

  implicit def toFuture(channelFuture: ChannelFuture): Future[Channel] = {
    val promise = Promise[Channel]()
    channelFuture.addListener(new ChannelFutureListener {
      def operationComplete(future: ChannelFuture) {
        if (future.isSuccess ) promise.success(future.channel())
        else promise.failure(future.cause())
      }
    })
    promise.future
  }

  implicit final class ByteBufOps(val buf: ByteBuf) extends AnyVal {
    def writeVInt(i: Int): ByteBuf = { VInt.write(buf, i); buf }
    def writeVLong(l: Long): ByteBuf = { VLong.write(buf, l); buf }
    def writeRangedBytes(src: Array[Byte]): ByteBuf = {
      writeVInt(src.length)
      if (src.length > 0) buf.writeBytes(src)
      buf
    }

    def readMaybeByte(): Option[Byte] = {
      if (buf.readableBytes() >= 1) {
        buf.markReaderIndex()
        Some(buf.readByte()) // Magic
      } else {
        buf.resetReaderIndex()
        None
      }
    }

    def readMaybeVLong(): Option[Long] = {
      if (buf.readableBytes() >= 1) {
        buf.markReaderIndex()
        val b = buf.readByte
        @tailrec def read(buf: ByteBuf, b: Byte, shift: Int, i: Long, count: Int): Option[Long] = {
          if ((b & 0x80) == 0) Some(i)
          else {
            if (count > 9)
              throw new IllegalStateException(
                "Stream corrupted.  A variable length long cannot be longer than 9 bytes.")

            if (buf.readableBytes() >= 1) {
              val bb = buf.readByte
              read(buf, bb, shift + 7, i | (bb & 0x7FL) << shift, count + 1)
            } else {
              buf.resetReaderIndex()
              None
            }
          }
        }
        read(buf, b, 7, b & 0x7F, 1)
      } else {
        buf.resetReaderIndex()
        None
      }
    }

    def readMaybeVInt(): Option[Int] = {
      if (buf.readableBytes() >= 1) {
        buf.markReaderIndex()
        val b = buf.readByte
        @tailrec def read(buf: ByteBuf, b: Byte, shift: Int, i: Int, count: Int): Option[Int] = {
          if ((b & 0x80) == 0) Some(i)
          else {
            if (count > 5)
              throw new IllegalStateException(
                "Stream corrupted.  A variable length integer cannot be longer than 5 bytes.")

            if (buf.readableBytes() >= 1) {
              val bb = buf.readByte
              read(buf, bb, shift + 7, i | ((bb & 0x7FL) << shift).toInt, count + 1)
            } else {
              buf.resetReaderIndex()
              None
            }
          }
        }
        read(buf, b, 7, b & 0x7F, 1)
      } else {
        buf.resetReaderIndex()
        None
      }
    }

    def readMaybeRangedBytes(): Option[Bytes] = {
      for {
        length <- readMaybeVInt()
        if buf.readableBytes() >= length
      } yield {
        if (length > 0) {
          val array = new Array[Byte](length)
          buf.readBytes(array)
          array
        } else {
          Array[Byte]()
        }
      }
    }
  }

  object VInt {
    @tailrec def write(out: ByteBuf, i: Int) {
      if ((i & ~0x7F) == 0) out.writeByte(i.toByte)
      else {
        out.writeByte(((i & 0x7f) | 0x80).toByte)
        write(out, i >>> 7)
      }
    }

    def read(in: ByteBuf): Int = {
      val b = in.readByte
      @tailrec def read(in: ByteBuf, b: Byte, shift: Int, i: Int, count: Int): Int = {
        if ((b & 0x80) == 0) i
        else {
          if (count > 5)
            throw new IllegalStateException(
              "Stream corrupted.  A variable length integer cannot be longer than 5 bytes.")

          val bb = in.readByte
          read(in, bb, shift + 7, i | ((bb & 0x7FL) << shift).toInt, count + 1)
        }
      }
      read(in, b, 7, b & 0x7F, 1)
    }
  }

  object VLong {
    @tailrec def write(out: ByteBuf, i: Long) {
      if ((i & ~0x7F) == 0) out.writeByte(i.toByte)
      else {
        out.writeByte(((i & 0x7f) | 0x80).toByte)
        write(out, i >>> 7)
      }
    }

    def read(in: ByteBuf): Long = {
      val b = in.readByte
      @tailrec def read(in: ByteBuf, b: Byte, shift: Int, i: Long, count: Int): Long = {
        if ((b & 0x80) == 0) i
        else {
          if (count > 9)
            throw new IllegalStateException(
              "Stream corrupted.  A variable length long cannot be longer than 9 bytes.")

          val bb = in.readByte
          read(in, bb, shift + 7, i | (bb & 0x7FL) << shift, count + 1)
        }
      }
      read(in, b, 7, b & 0x7F, 1)
    }
  }

}
