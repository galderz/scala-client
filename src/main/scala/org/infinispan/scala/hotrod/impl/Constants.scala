package org.infinispan.scala.hotrod.impl

private[impl] object Constants {
  // Magic
  val Req = 0xA0
  val Res = 0xA1
  // Version
  val V20 = 20
  // Status
  val Success = 0x00
  val NotApplied = 0x01
  val NotFound = 0x02
  // Client intelligence
  val ClientBasic = 0x01
}

private[impl] case class Id(id: Int) extends AnyVal

// Client request codes
private[impl] object RequestIds {
  final val Put = Id(0x01)
  final val Get = Id(0x03)
  final val PutIfAbsent = Id(0x05)
  final val Replace = Id(0x07)
  final val Remove = Id(0x0B)
}

// Server response codes
private[impl] object ResponseIds {
  // Keep these as final Int values to enable @switch pattern matching
  final val Put = 0x02
  final val Get = 0x04
  final val PutIfAbsent = 0x06
  final val Replace = 0x08
  final val Remove = 0x0C
//  final val Error = 0x50
}
