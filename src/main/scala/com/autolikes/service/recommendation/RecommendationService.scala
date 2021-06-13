package com.autolikes.service.recommendation

import com.autolikes.service.core.HttpClientService

import com.twitter.finagle.Service
import com.twitter.util.Future

import io.circe.Json
import io.circe.parser.parse
import io.circe.generic.auto._

final case class User(_id: String)

final case class Recommendation(user: User)

object RecommendationService {

  type UserId = String

  def apply(): Service[Unit, List[UserId]] = Service.mk(_ =>
    HttpClientService.general
      .withTls(RecommendationRequest.host)
      .withTlsWithoutValidation
      .newService(s"${RecommendationRequest.host}:443", "tinder.recommendation")
      .apply(RecommendationRequest.list)
      .flatMap { rep =>
        val doc: Json = parse(rep.contentString).getOrElse(Json.Null)

        val result = doc.hcursor
          .downField("data")
          .downField("results")
          .as[List[Recommendation]]

        result match {
          case Left(error)  => Future.exception(error)
          case Right(value) => Future.value(value.map(_.user._id))
        }
      }
  )
}
