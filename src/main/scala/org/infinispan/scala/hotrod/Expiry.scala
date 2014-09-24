package org.infinispan.scala.hotrod

import scala.concurrent.duration.Duration
import scala.concurrent.duration._

sealed abstract class Expiry {
  def toSeconds: Int
}

// Never expire
case object Never extends Expiry {
  override def toSeconds: Int = 0
}

// Expire according to the server's configuration
case object Server extends Expiry {
  override def toSeconds: Int = -1
}

// Expiry relative in time
case class Relative(timeout: Duration) extends Expiry {
  override def toSeconds: Int = {
    // TODO: Validate that it's less than 30 days...
    timeout.toSeconds.toInt
  }
}

// Absolute in time:
// If using JDK8, calling java.time.chrono.ChronoLocalDateTime.toInstant(ZoneOffset.UTC)
// transforms a LocalDateTime into an Instant and then you can call Instant.toEpochMilli
case class AbsoluteUnix(unixtime: Long) extends Expiry {
  override def toSeconds: Int = {
    // TODO: Validate is more than 30 days...
    MILLISECONDS.toSeconds(unixtime).toInt
  }
}