package org.infinispan.scala.hotrod

import scala.async.Async._
import scala.concurrent.duration.{Duration, _}
import scala.concurrent._
import scala.language.postfixOps
import scala.util.Success

package object testkit {

  implicit lazy val global: ExecutionContextExecutor = scala.concurrent.ExecutionContext.Implicits.global

  val timefactor = System.getProperty("hotrod.test.timefactor", "1.0").toDouble

  def until[T](duration: Duration = 1.seconds)(f: Future[T]): T =
    Await.result(f, duration * timefactor)

  def until[T](in: => Future[T], out: => Future[T], duration: Duration): T = {
    until(duration)(in)
    until(duration * 2)(delay(duration))
    until(duration)(out)
  }

  def delay(t: Duration): Future[Unit] = async {
    await[Unit] {
      val p = Promise[Unit]()
      blocking {
        Thread.sleep(t.toMillis)
        p.complete(Success())
      }
      p.future
    }
  }

}
