package org.infinispan.scala.hotrod

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

trait CacheClient[A, B] {

  def put(kv: (A, B), lifespan: Duration = 0.seconds, maxidle: Duration = 0.seconds): Future[Unit]
  def get(k: A): Future[Option[B]]
  def remove(k: A): Future[Unit]

  def stop(): Future[Unit]

}

object CacheClient {

  def apply[A, B](host: String = "localhost", port: Int = 11222)
       (implicit ec: ExecutionContext): Future[CacheClient[A, B]] = {
    CacheNettyClient(host, port)
  }

}