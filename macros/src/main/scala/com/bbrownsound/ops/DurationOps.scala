package com.bbrownsound.ops

import java.time.Duration

//https://stackoverflow.com/questions/3471397/how-can-i-pretty-print-a-duration-in-java
final class DurationOps(self: Duration) {
  def toPretty: String = {
    self.toString
      .substring(2)
      .replaceAll("(\\d[HMS])(?!$)", "$1 ")
      .toLowerCase()
  }
}

trait ToDurationOps {
  implicit def toDurationOpsFromDuration(d: Duration): DurationOps = new DurationOps(d)
}
