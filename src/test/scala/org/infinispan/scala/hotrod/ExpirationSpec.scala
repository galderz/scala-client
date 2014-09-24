package org.infinispan.scala.hotrod

import org.infinispan.scala.hotrod.testkit._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class ExpirationSpec extends TestKit[Int, String] {

  implicit val expiryAwaitTimeout: Duration = 10.seconds

  test("Cache client can put data that expires after lifespan has passed") {
    implicit val ctx = Context.empty + Lifespan(Relative(2 seconds))
    expiringStore(client.put(1 -> "v1")).await
  }

  test("Cache client can put data that expires after UNIX-time-based lifespan has passed") {
    unixTimeExpiringStore { l =>
      client.put(1 -> "v1")(Context.empty + Lifespan(AbsoluteUnix(l)))
    }.await
  }

  test("Cache client can put data that expires after max idle time has passed") {
    implicit val ctx = Context.empty + MaxIdle(Relative(2 seconds))
    expiringStore(client.put(1 -> "v1")).await
  }

  test("Cache client can put data that expires after UNIX-time-based max idle time has passed") {
    unixTimeExpiringStore { m =>
      client.put(1 -> "v1")(Context.empty + MaxIdle(AbsoluteUnix(m)))
    }.await
  }

  private def expiringStore[T](store: => Future[T]): Future[Unit] = {
    for {
      () <- store // store function with expiration time (either w/ lifespan or maxidle)
      v <- client.get(1)
      () <- Future.delay(4 seconds)
      v2 <- client.get(1)
    } yield {
      v shouldBe Some("v1")
      v2 shouldBe None
    }
  }

  private def unixTimeExpiringStore[T](store: Long => Future[T]): Future[Unit] = {
    val lifespan = 2.seconds
    val current = System.currentTimeMillis()
    val expiry = current + lifespan.toMillis
    for {
      // store function with UNIX-time-based expiration time (either w/ lifespan or maxidle)
      () <- store(expiry)
      v <- client.get(1)
      () <- Future.delay(lifespan * 2)
      v2 <- client.get(1)
    } yield {
      v shouldBe Some("v1")
      v2 shouldBe None
    }
  }

}
