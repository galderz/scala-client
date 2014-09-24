package org.infinispan.scala

import io.netty.buffer.ByteBuf
import io.netty.channel.{Channel, ChannelFuture, ChannelFutureListener}

import scala.annotation.tailrec
import scala.concurrent.{Future, Promise}

package object hotrod {

  type Bytes = Array[Byte]

  import scala.language.implicitConversions

  implicit object ExpiryLifespan extends Param[Lifespan] {
    val default = Lifespan(Never)
  }

  implicit object ExpiryMaxIdle extends Param[MaxIdle] {
    val default = MaxIdle(Never)
  }

}
