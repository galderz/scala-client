package org.infinispan.scala.hotrod.impl

import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.{Channel, ChannelHandler, ChannelInitializer, ChannelOption}

private[impl] object Netty {

  val DefaultEventLoopGroup = new NioEventLoopGroup()

  def apply(handler: ChannelHandler): Bootstrap = new Bootstrap()
    .group(DefaultEventLoopGroup)
    .channel(classOf[NioSocketChannel])
    .option[java.lang.Boolean](ChannelOption.SO_KEEPALIVE, true)
    .handler(new ChannelInitializer[io.netty.channel.Channel] {
      def initChannel(ch: Channel) {
        ch.pipeline().addLast(
          new Decoder20(),
          new Encoder20(),
          handler
        )
      }
  })

}
