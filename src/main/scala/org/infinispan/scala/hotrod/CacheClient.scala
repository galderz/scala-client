package org.infinispan.scala.hotrod

import scala.concurrent.{ExecutionContext, Future}

trait CacheClient[A, B] {

  def put(kv: (A, B)): Future[Unit]
  def get(k: A): Future[Option[B]]

  def stop(): Future[Unit]

}

object CacheClient {

  def apply[A, B](host: String = "localhost", port: Int = 11222)
       (implicit ec: ExecutionContext): Future[CacheClient[A, B]] = {
    CacheNettyClient(host, port)
  }

}