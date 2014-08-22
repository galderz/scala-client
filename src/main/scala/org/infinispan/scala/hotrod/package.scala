package org.infinispan.scala

import io.netty.buffer.ByteBuf

import scala.annotation.tailrec

package object hotrod {

  type Bytes = Array[Byte]

  import scala.language.implicitConversions

  implicit final class ByteBufOps(val buf: ByteBuf) extends AnyVal {
    def writeVInt(i: Int): ByteBuf = { VInt.write(buf, i); buf }
    def writeVLong(l: Long): ByteBuf = { VLong.write(buf, l); buf }

    def readVInt(): Int = VInt.read(buf)
    def readVLong(): Long = VLong.read(buf)

    def writeRangedBytes(src: Array[Byte]): ByteBuf = {
      writeVInt(src.length)
      if (src.length > 0) buf.writeBytes(src)
      buf
    }
    def readRangedBytes(): Array[Byte] = {
      val length = readVInt()
      if (length > 0) {
        val array = new Array[Byte](length)
        buf.readBytes(array)
        array
      } else {
        Array[Byte]()
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
