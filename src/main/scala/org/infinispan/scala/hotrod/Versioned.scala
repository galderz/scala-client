package org.infinispan.scala.hotrod

case class Versioned[B](value: B, version: EntryVersion)

case class EntryVersion(version: Long) extends AnyVal
