package com.autolikes.service.like

import com.twitter.finagle.http.{Method, Request}

import com.typesafe.config.ConfigFactory

object LikeRequest {

  def host: String = {
    val config = ConfigFactory.load()
    config.getString("tinder.host")
  }

  def byId(userId: String): Request = {

    val config = ConfigFactory.load()

    val request = Request(Method.Post, s"/like/$userId")
    request.host = host
    request.userAgent = "AutoLike"
    request.headerMap
      .add("x-auth-token", config.getString("tinder.token"))
    request.setContentTypeJson()
    request
  }
}
