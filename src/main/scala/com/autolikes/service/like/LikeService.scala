package com.autolikes.service.like

import com.autolikes.service.core.HttpClientService
import com.autolikes.service.recommendation.RecommendationService.UserId

import com.twitter.finagle.Service
import com.twitter.util.Future

import io.circe.generic.auto._
import io.circe.parser.decode

final case class LikeStatus(status: Int)

object LikeService {

  def byId: Service[UserId, (UserId, Int)] = Service.mk(userId =>
    HttpClientService.general
      .withTls(LikeRequest.host)
      .withTlsWithoutValidation
      .newService(s"${LikeRequest.host}:443", "tinder.like")
      .apply(LikeRequest.byId(userId))
      .flatMap { rep =>
        val result = decode[LikeStatus](rep.contentString)

        result match {
          case Left(error)  => Future.exception(error)
          case Right(value) => Future.value((userId, value.status))
        }
      }
  )
}
