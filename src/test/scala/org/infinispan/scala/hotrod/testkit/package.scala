package org.infinispan.scala.hotrod

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{ExecutionContextExecutor, Await, Future}
import scala.language.postfixOps

package object testkit {

  implicit lazy val global: ExecutionContextExecutor = scala.concurrent.ExecutionContext.Implicits.global

  val timefactor = System.getProperty("hotrod.test.timefactor", "1.0").toDouble

  def until[T](f: Future[T], duration: Duration = 1.seconds): T =
    Await.result(f, duration * timefactor)

}
