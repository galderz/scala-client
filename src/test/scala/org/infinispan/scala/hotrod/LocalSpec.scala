package org.infinispan.scala.hotrod

import org.infinispan.scala.hotrod.testkit._

class LocalSpec extends TestKit[Int, String] {

  test("Cache client can do a put/get/remove cycle on a single key") {
    await {
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

  test("Cache client can do a putIfAbsent") {
    await {
      for {
        ret1 <- client.putIfAbsent(1 -> "v1")
        v <- client.get(1)
        ret2 <- client.putIfAbsent(1 -> "v2")
        v2 <- client.get(1)
        () <- client.remove(1)
      } yield {
        ret1 shouldBe true
        v shouldBe Some("v1")
        ret2 shouldBe false
        v2 shouldBe Some("v1")
      }
    }
  }

}
