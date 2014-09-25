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
        r1 <- client.replace(1 -> "v1")
        v <- client.get(1)
        r2 <- client.putIfAbsent(1 -> "v2")
        v2 <- client.get(1)
        r3 <- client.replace(1 -> "v3")
        v3 <- client.get(1)
        () <- client.remove(1)
      } yield {
        r1 shouldBe false
        v shouldBe None
        r2 shouldBe true
        v2 shouldBe Some("v2")
        r3 shouldBe true
        v3 shouldBe Some("v3")
      }
    }
  }

  test("Cache client can check if key is present") {
    await {
      for {
        b <- client.contains(1)
        () <- client.put(1 -> "v1")
        b2 <- client.contains(1)
        () <- client.remove(1)
      } yield {
        b shouldBe false
        b2 shouldBe true
      }
    }
  }

  test("Cache client can replace a key if the version matches") {
    await {
      for {
        r1 <- client.replaceVersioned(1 -> "v99", EntryVersion(123))
        v1 <- client.versioned(1)
        r2 <- client.putIfAbsent(1 -> "v1")
        r3 <- client.replaceVersioned(1 -> "v2", EntryVersion(123))
        v2 <- client.versioned(1)
        r4 <- client.replaceVersioned(1 -> "v2", v2.get.version)
        v3 <- client.versioned(1)
      } yield {
        r1 shouldBe false
        v1 shouldBe None
        r2 shouldBe true
        r3 shouldBe false
        v2.get.value shouldBe "v1"
        v2.get.version.version should be > 0L
        r4 shouldBe true
        v3.get.value shouldBe "v2"
        v3.get.version.version should be > 0L
      }
    }
  }

}
