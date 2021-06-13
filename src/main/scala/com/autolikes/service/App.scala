package com.autolikes.service

import com.autolikes.service.core.{RandomDelayFilter, RepeatFilter}
import com.autolikes.service.like.LikeService
import com.autolikes.service.recommendation.RecommendationService

import com.twitter.conversions.DurationOps.RichDuration
import com.twitter.finagle.Service
import com.twitter.util.{Await, Future}

object App {

  def main(args: Array[String]): Unit = {

    val mainService = Service.mk[Unit, Unit](_ =>
      for {
        userIds <- RecommendationService().apply(())
        _ <- Future
          .traverseSequentially(userIds)(id =>
            RandomDelayFilter(from = 5.seconds, to = 15.seconds)
              .andThen(LikeService.byId)
              .apply(id)
          )
          .onSuccess(println)
      } yield ()
    )

    val appFuture =
      RepeatFilter(delay = 1.minute).andThen(mainService).apply(())

    Await.ready(appFuture)
  }
}
