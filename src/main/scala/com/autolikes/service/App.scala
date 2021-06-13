package com.autolikes.service

import com.autolikes.service.core.RepeatFilter
import com.autolikes.service.like.LikeService
import com.autolikes.service.recommendation.RecommendationService

import com.twitter.finagle.Service
import com.twitter.finagle.util.DefaultTimer
import com.twitter.util.{Await, Future, Timer}
import com.twitter.conversions.DurationOps.RichDuration

object App {

  def main(args: Array[String]): Unit = {

    implicit val timer: Timer = DefaultTimer

    val mainService = Service.mk[Unit, Unit](_ =>
      for {
        userIds <- RecommendationService().apply(())
        _ <- Future
          .traverseSequentially(userIds)(LikeService.byId)
          .delayed(5.seconds)
          .onSuccess(println)
      } yield ()
    )

    val appFuture = RepeatFilter().andThen(mainService).apply(())

    Await.ready(appFuture)
  }
}
