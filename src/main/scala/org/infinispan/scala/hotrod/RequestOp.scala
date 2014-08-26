package org.infinispan.scala.hotrod

private[hotrod] case class RequestOp(value: Byte) extends AnyVal

private[hotrod] object RequestOps {
  val Put = RequestOp(0x01)
  val Get = RequestOp(0x03)
}