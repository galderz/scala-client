package org.infinispan.scala.hotrod

import org.infinispan.scala.hotrod.testkit._

import scala.concurrent.duration._
import scala.language.postfixOps

class LocalSpec extends TestKit[Int, String] {

  test("Cache client can do a put/get/remove cycle on a single key") {
    until() {
      for {
        () <- client.put(1 -> "v1")
        v <- client.get(1)
        () <- client.remove(1)
        v2 <- client.get(1)
      } yield {
        v shouldBe Some("v1")
        v2 shouldBe None
      }
    }
  }

  test("Cache client can put data that expires after lifespan has passed") {
    val lifespan = 2.seconds
    val timebuffer = lifespan * 2
    until(duration = 10 seconds) {
      for {
        () <- client.put(1 -> "v1", lifespan)
      } yield {
        until(
          client.get(1).map(_ shouldBe Some("v1")),
          client.get(1).map(_ shouldBe None),
          timebuffer
        )
      }
    }
  }

  test("Cache client can put data that expires after max idle time has passed") {
    val maxidle = 2.seconds
    val timebuffer = maxidle * 2
    until(duration = 10 seconds) {
      for {
        () <- client.put(1 -> "v1", maxidle)
      } yield {
        until(
          client.get(1).map(_ shouldBe Some("v1")),
          client.get(1).map(_ shouldBe None),
          timebuffer
        )
      }
    }
  }

}
