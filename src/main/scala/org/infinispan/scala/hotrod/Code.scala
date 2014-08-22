package org.infinispan.scala.hotrod

case class Code(value: Byte) extends AnyVal

object Codes {
  val Put = Code(0x01)
  val Get = Code(0x03)
}