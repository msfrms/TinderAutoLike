package com.autolikes.service.like

import com.twitter.finagle.http.{Method, Request}

import com.typesafe.config.ConfigFactory

object LikeRequest {

  def host: String = {
    val config = ConfigFactory.load()
    config.getString("tinder.host")
  }

  def byId(userId: String, token: String): Request = {
    val request = Request(Method.Post, s"/like/$userId")
    request.host = host
    request.userAgent =
      "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.98 Mobile Safari/537.36"
    request.headerMap.add("x-auth-token", token)
    request.setContentTypeJson()
    request
  }
}
