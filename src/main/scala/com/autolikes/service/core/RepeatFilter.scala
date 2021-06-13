package com.autolikes.service.core

import com.twitter.conversions.DurationOps.RichDuration
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.{Await, Duration, Future, FuturePool}

import com.typesafe.scalalogging.LazyLogging

final class RepeatFilter[Req, Rep](delay: Duration)
    extends SimpleFilter[Req, Rep]
    with LazyLogging {

  override def apply(request: Req, service: Service[Req, Rep]): Future[Rep] =
    FuturePool.unboundedPool {
      while (true) {
        try {
          Thread.sleep(delay.inMillis)
          Await.result(service(request), 10.minute)
        } catch {
          case e: Throwable =>
            logger.info(s"exception: $e")
        }
      }
      throw new Throwable(s"interrupt RepeatFilter")
    }
}

object RepeatFilter {
  def apply[Req, Rep](delay: Duration = 1.second) =
    new RepeatFilter[Req, Rep](delay)
}
