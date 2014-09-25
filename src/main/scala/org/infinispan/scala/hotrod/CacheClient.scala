package org.infinispan.scala.hotrod

import org.infinispan.scala.hotrod.impl.CacheNettyClient

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

trait CacheClient[A, B] {

  // TODO: Move contents to separate traits...

  // CRUD operations //
  def put(kv: (A, B))(implicit ctx: Context = Context.empty): Future[Unit]
  def get(k: A): Future[Option[B]]
  def remove(k: A): Future[Unit]

  def contains(k: A): Future[Boolean]
  def versioned(k: A): Future[Option[Versioned[B]]]

//  // Return previous operations //
//  def getAndPut(kv: (A, B))(implicit ctx: Context = Context.empty): Future[B]
//  def getAndReplace(kv: (A, B))(implicit ctx: Context = Context.empty): Future[B]
//  def getAndRemove(k: A)(implicit ctx: Context = Context.empty): Future[B]

  // Conditional operations //
  def putIfAbsent(kv: (A, B))(implicit ctx: Context = Context.empty): Future[Boolean]
  def replace(kv: (A, B))(implicit ctx: Context = Context.empty): Future[Boolean]
  def replaceVersioned(kv: (A, B), v: EntryVersion)(implicit ctx: Context = Context.empty): Future[Boolean]

  // Lifecycle //
  def stop(): Future[Unit]

}

object CacheClient {

  class StoppedException() extends IllegalStateException("Stopped, no further operations allowed")

  def apply[A, B](host: String = "localhost", port: Int = 11222)
       (implicit ec: ExecutionContext): Future[CacheClient[A, B]] = {
    CacheNettyClient(host, port)
  }

}