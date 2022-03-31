package com.autolikes.service

import com.autolikes.service.auth.AuthService
import com.autolikes.service.core.{RandomDelayFilter, RepeatFilter}
import com.autolikes.service.like.LikeService
import com.autolikes.service.recommendation.{
  NotAuthException,
  RecommendationService
}
import com.autolikes.service.recommendation.RecommendationService.UserId

import com.twitter.conversions.DurationOps.RichDuration
import com.twitter.finagle.Service
import com.twitter.util.{Await, Future}

object App {
  var token: String = ""

  def main(args: Array[String]): Unit = {

    val mainService = Service.mk[Unit, Unit](_ =>
      for {
        _ <- Service
          .mk[Unit, Unit](_ =>
            if (token.isEmpty)
              AuthService.auth
                .apply(())
                .map { token =>
                  println(s"setup new token: $token")
                  this.token = token
                  ()
                }
            else
              Future.Unit
          )
          .apply(())
        userIds <- Service
          .mk[Unit, List[UserId]](_ =>
            RecommendationService().apply(token).rescue {
              case NotAuthException =>
                AuthService.auth
                  .apply(())
                  .flatMap { token =>
                    this.token = token
                    RecommendationService()
                      .apply(token)
                      .map(ids => ids)
                  }
              case e: Throwable => Future.exception(e)
            }
          )
          .apply(())
        _ <- Future
          .traverseSequentially(userIds)(id =>
            RandomDelayFilter(from = 5.seconds, to = 15.seconds)
              .andThen(LikeService.byId(id))
              .apply(token)
              .onSuccess(println)
              .onFailure(println)
          )
      } yield ()
    )

    val appFuture =
      RepeatFilter(delay = 5.seconds).andThen(mainService).apply(())

    Await.ready(appFuture)
  }
}
