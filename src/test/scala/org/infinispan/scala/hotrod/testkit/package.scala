package org.infinispan.scala.hotrod

import scala.async.Async
import scala.concurrent.duration.{Duration, _}
import scala.concurrent._
import scala.language.postfixOps
import scala.util.Success

package object testkit {

  implicit lazy val global: ExecutionContextExecutor = scala.concurrent.ExecutionContext.Implicits.global

  val timefactor = System.getProperty("infinispan.scala.hotrod.test.timefactor", "1.0").toDouble

  implicit final class FutureTestOps[+A](val fut: Future[A]) {
    def await(implicit d: Duration = 3.second): A = Await.result(fut, d * timefactor)
    def ready(implicit d: Duration = 3.second): Future[A] = Await.ready(fut, d * timefactor)
//    def await[](in: => Future[T], out: => Future[T], duration: Duration): A = {
//      until(duration)(in)
//      until(duration * 2)(delay(duration))
//      until(duration)(out)
//    }
  }

  implicit class FutureCompanionOps[T](val f: Future.type) {
    def delay(t: Duration): Future[Unit] = Async.async {
      Async.await[Unit] {
        val p = Promise[Unit]()
        blocking {
          Thread.sleep(t.toMillis)
          p.complete(Success())
        }
        p.future
      }
    }
  }


//  def until[T](duration: Duration = 1.seconds)(f: Future[T]): T =
//    Await.result(f, duration * timefactor)

//  def until[T](in: => Future[T], out: => Future[T], duration: Duration): T = {
//    until(duration)(in)
//    until(duration * 2)(delay(duration))
//    until(duration)(out)
//  }

//  def delay(t: Duration): Future[Unit] = async {
//    await[Unit] {
//      val p = Promise[Unit]()
//      blocking {
//        Thread.sleep(t.toMillis)
//        p.complete(Success())
//      }
//      p.future
//    }
//  }

}
