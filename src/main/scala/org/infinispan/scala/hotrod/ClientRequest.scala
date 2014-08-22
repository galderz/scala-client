package org.infinispan.scala.hotrod

sealed abstract class ClientRequest(val code: Code)

case class Put[A, B](kv: (A, B)) extends ClientRequest(Codes.Put)

case class Get[A](k: A) extends ClientRequest(Codes.Get)