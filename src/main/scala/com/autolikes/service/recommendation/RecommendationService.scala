package com.autolikes.service.recommendation

import com.autolikes.service.core.HttpClientService

import com.twitter.finagle.Service
import com.twitter.util.Future

import io.circe.Json
import io.circe.generic.auto._
import io.circe.parser.parse

final case class User(_id: String)

final case class Recommendation(user: User)

case object NotAuthException extends Throwable

object RecommendationService {

  type UserId = String

  def apply(): Service[String, List[UserId]] = Service.mk(token =>
    HttpClientService.general
      .withTls(RecommendationRequest.host)
      .withTlsWithoutValidation
      .newService(s"${RecommendationRequest.host}:443", "tinder.recommendation")
      .apply(RecommendationRequest.list(token))
      .flatMap { rep =>
        val isSuccessful = (200 until 300).contains(rep.statusCode)
        if (isSuccessful) {
          val doc: Json = parse(rep.contentString).getOrElse(Json.Null)

          val result = doc.hcursor
            .downField("data")
            .downField("results")
            .as[List[Recommendation]]

          result match {
            case Left(error)  => Future.exception(error)
            case Right(value) => Future.value(value.map(_.user._id))
          }
        } else if (rep.statusCode == 401) {
          Future.exception(NotAuthException)
        } else {
          Future.exception(new Throwable(s"""
                                            |status code: ${rep.statusCode}
                                            |response: ${rep.contentString}
                                            |""".stripMargin))
        }
      }
  )
}
