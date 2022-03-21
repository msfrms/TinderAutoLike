package com.autolikes.service.recommendation

import com.twitter.finagle.http.{Method, Request}

import com.typesafe.config.ConfigFactory

object RecommendationRequest {

  def host: String = {
    val config = ConfigFactory.load()
    config.getString("tinder.host")
  }

  def list: Request = {

    val config = ConfigFactory.load()

    val request = Request(Method.Get, "/v2/recs/core?locale=ru")
    request.host = host
    request.userAgent =
      "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.98 Mobile Safari/537.36"
    request.headerMap
      .add("x-auth-token", config.getString("tinder.token"))
    request.setContentTypeJson()
    request
  }
}
