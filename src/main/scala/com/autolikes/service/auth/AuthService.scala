package com.autolikes.service.auth

import com.autolikes.service.core.HttpClientService

import com.twitter.finagle.Service

object AuthService {
  def auth: Service[Unit, String] = Service.mk(_ =>
    HttpClientService.general
      .withTls(AuthRequest.host)
      .withTlsWithoutValidation
      .newService(s"${AuthRequest.host}:443", "tinder.auth")
      .apply(AuthRequest.auth)
      .map { rep =>
        val data  = rep.contentString
        val start = data.indexOf("$")
        val end   = data.indexOf("\"")
        data.substring(start, end).replace("$", "")
      }
  )
}
