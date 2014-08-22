package org.infinispan.scala.hotrod

import scala.concurrent.Future

trait RemoteCache[A, B] {

  def put(kv: (A, B)): Future[Unit]
  def get(k: A): Future[Option[B]]

}
