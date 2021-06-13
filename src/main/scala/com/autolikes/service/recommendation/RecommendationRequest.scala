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
    request.userAgent = "AutoLike"
    request.headerMap
      .add("x-auth-token", config.getString("tinder.token"))
    request.setContentTypeJson()
    request
  }
}
