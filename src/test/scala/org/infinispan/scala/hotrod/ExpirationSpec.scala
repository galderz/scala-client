package org.infinispan.scala.hotrod

import org.infinispan.scala.hotrod.testkit._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class ExpirationSpec extends TestKit[Int, String] {

  test("Cache client can put data that expires after lifespan has passed") {{
    for {
      () <- client.put(1 -> "v1", lifespan = 2.seconds)
      v <- client.get(1)
      () <- Future.delay(4 seconds)
      v2 <- client.get(1)
    } yield {
      v shouldBe Some("v1")
      v2 shouldBe None
    }
  }.await(10 seconds)}

  test("Cache client can put data that expires after UNIX-time-based lifespan has passed") {
    val lifespan = 2.seconds
    val current = System.currentTimeMillis()
    val expiry = (current + lifespan.toMillis).milliseconds
    (for {
      () <- client.put(1 -> "v1", lifespan = expiry)
      v <- client.get(1)
      () <- Future.delay(lifespan * 2)
      v2 <- client.get(1)
    } yield {
      v shouldBe Some("v1")
      v2 shouldBe None
    }).await(10 seconds)
  }

  test("Cache client can put data that expires after max idle time has passed") {{
    for {
      () <- client.put(1 -> "v1", maxidle = 2.seconds)
      v <- client.get(1)
      () <- Future.delay(4 seconds)
      v2 <- client.get(1)
    } yield {
      v shouldBe Some("v1")
      v2 shouldBe None
    }
  }.await(10 seconds)}


  test("Cache client can put data that expires after UNIX-time-based max idle time has passed") {
    val maxidle = 2.seconds
    val current = System.currentTimeMillis()
    val expiry = (current + maxidle.toMillis).milliseconds
    (for {
      () <- client.put(1 -> "v1", maxidle = expiry)
      v <- client.get(1)
      () <- Future.delay(maxidle * 2)
      v2 <- client.get(1)
    } yield {
      v shouldBe Some("v1")
      v2 shouldBe None
    }).await(10 seconds)
  }

}
