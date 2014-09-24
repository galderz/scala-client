package org.infinispan.scala.hotrod

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

trait CacheClient[A, B] {

  // CRUD operations //
  def put(kv: (A, B))(implicit ctx: Context = Context.empty): Future[Unit]
  def get(k: A): Future[Option[B]]
  def remove(k: A): Future[Unit]

//  // Return previous operations //
//  def getAndPut(kv: (A, B))(implicit ctx: Context = Context.empty): Future[B]

  // Conditional operations //
  def putIfAbsent(kv: (A, B))(implicit ctx: Context = Context.empty): Future[Boolean]

  // Lifecycle //
  def stop(): Future[Unit]

}

object CacheClient {

  def apply[A, B](host: String = "localhost", port: Int = 11222)
       (implicit ec: ExecutionContext): Future[CacheClient[A, B]] = {
    CacheNettyClient(host, port)
  }

}