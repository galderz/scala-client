package org.infinispan.scala.hotrod.testkit

import org.infinispan.scala.hotrod.CacheClient
import org.infinispan.server.hotrod.test.HotRodTestingUtil._
import org.infinispan.test.TestingUtil._
import org.infinispan.test.fwk.TestCacheManagerFactory._
import org.scalatest.{Matchers, FunSuiteLike, BeforeAndAfterAll, Suite}

abstract class TestKit[A, B] extends TestKitBase[A, B]

trait TestKitBase[A, B] extends FunSuiteLike
  with Matchers
  with BeforeAndAfterAll { this: Suite =>

  val server = startHotRodServer(createCacheManager(hotRodCacheConfiguration()))
  val client = until()(CacheClient[A, B](port = 12311))

  override protected def afterAll(): Unit = {
    val manager = server.getCacheManager
    if (server != null) server.stop
    killCacheManagers(manager)
  }
}
