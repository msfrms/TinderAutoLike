package com.autolikes.service.core

import com.twitter.conversions.DurationOps.RichDuration
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.util.DefaultTimer
import com.twitter.util.{Duration, Future, Timer}

final class RandomDelayFilter[Req, Rep](from: Duration, to: Duration)
    extends SimpleFilter[Req, Rep] {

  override def apply(request: Req, service: Service[Req, Rep]): Future[Rep] = {
    val start                 = from.inSeconds
    val end                   = to.inSeconds
    val random                = new scala.util.Random
    val delay                 = (start + random.nextInt(end - start + 1)).toLong.seconds
    implicit val timer: Timer = DefaultTimer
    service(request).delayed(delay)
  }
}

object RandomDelayFilter {

  def apply[Req, Rep](from: Duration, to: Duration) =
    new RandomDelayFilter[Req, Rep](from, to)
}
