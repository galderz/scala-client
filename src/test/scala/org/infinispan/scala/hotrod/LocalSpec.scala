package org.infinispan.scala.hotrod

import org.infinispan.scala.hotrod.testkit._
import org.scalatest.BeforeAndAfterAll

class LocalSpec extends TestKit[Int, String]()

  with BeforeAndAfterAll {

  test("Cache client can put data in and retrieve it via get") {
    until {
      for {
        () <- client.put(1 -> "v1")
        v <- client.get(1)
      } yield {
        v shouldBe Some("v1")
      }
    }
  }
}
