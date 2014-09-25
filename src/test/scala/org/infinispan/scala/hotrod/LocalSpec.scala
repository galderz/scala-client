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

  test("Cache client can put a key if absent") {
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

  test("Cache client can replace a key") {
    await {
      for {
        ret1 <- client.replace(1 -> "v1")
        v <- client.get(1)
        ret2 <- client.putIfAbsent(1 -> "v2")
        v2 <- client.get(1)
        ret3 <- client.replace(1 -> "v3")
        v3 <- client.get(1)
        () <- client.remove(1)
      } yield {
        ret1 shouldBe false
        v shouldBe None
        ret2 shouldBe true
        v2 shouldBe Some("v2")
        ret3 shouldBe true
        v3 shouldBe Some("v3")
      }
    }
  }

  test("Cache client can check if key is present") {
    await {
      for {
        b <- client.containsKey(1)
        () <- client.put(1 -> "v1")
        b2 <- client.containsKey(1)
        () <- client.remove(1)
      } yield {
        b shouldBe false
        b2 shouldBe true
      }
    }
  }

}
