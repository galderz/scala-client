package org.infinispan.scala.hotrod

private[hotrod] object Constants {
  // Magic
  val Req = 0xA0
  val Res = 0xA1
  // Version
  val V20 = 20
  // Status
  val Success = 0x00
  val NotFound = 0x02
  // Client intelligence
  val ClientBasic = 0x01
}

// Client request codes
object RequestOps {
  final val Put = 0x01
  final val Get = 0x03
  final val Remove = 0x0B
}

// Server response codes
object ResponseOps {
  final val Put = 0x02
  final val Get = 0x04
  final val Remove = 0x0C
}
