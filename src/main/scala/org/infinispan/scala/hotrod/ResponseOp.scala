package org.infinispan.scala.hotrod

private[hotrod] case class ResponseOp(value: Byte) extends AnyVal

private[hotrod] object ResponseOp {
//  def apply(value: Byte):
//
//  def unapply(value: Byte): Option[ResponseOp] = {
//    (value: @switch) match {
//      case ResponseOps.Put.value => Some(ResponseOps.Put)
//      case ResponseOps.Get.value => Some(ResponseOps.Get)
//      case _ => None
//    }
//  }
//  def apply(value: Byte): ResponseOp = {
//    (value: @switch) match {
//      case ResponseOps.Put.value => ResponseOps.Put
//      case ResponseOps.Get.value => ResponseOps.Get
//    }
//  }
}

object ResponseOps {
  val Put = ResponseOp(0x02)
  val Get = ResponseOp(0x04)
}