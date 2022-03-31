package com.autolikes.service.auth

import com.twitter.finagle.http.{Method, Request}

import com.typesafe.config.ConfigFactory

object AuthRequest {
  def host: String = {
    val config = ConfigFactory.load()
    config.getString("tinder.host")
  }

  def auth: Request = {

    val headers = Map(
      "sec-ch-ua"                 -> """" Not;A Brand";v="99", "Google Chrome";v="97", "Chromium";v="97"""",
      "accept-language"           -> "ru",
      "app-session-time-elapsed"  -> "11033558",
      "user-session-time-elapsed" -> "60287737",
      "Save-Data"                 -> "on",
      "sec-ch-ua-platform"        -> """"Android"""",
      "x-supported-image-formats" -> "webp,jpeg",
      "funnel-session-id"         -> "ff668c66916613bb",
      "persistent-device-id"      -> "03665729-40fe-4d0f-a7ba-d7a9a5ef7239",
      "is-created-as-guest"       -> "false",
      "is-created-as-guest"       -> "false",
      "tinder-version"            -> "3.21.0",
      "sec-ch-ua-mobile"          -> "?1",
      "Content-Type"              -> "application/x-google-protobuf",
      "user-session-id"           -> "c6b709cd-bd7f-4153-845d-584fa748ad24",
      "Accept"                    -> "application/json",
      "platform"                  -> "lite",
      "app-session-id"            -> "735142eb-e310-4abe-8335-a379b2ab9232",
      "app-version"               -> "1032100",
      "Origin"                    -> "https://lite.tinder.com",
      "Sec-Fetch-Site"            -> "cross-site",
      "Sec-Fetch-Mode"            -> "cors",
      "Sec-Fetch-Dest"            -> "empty",
      "Referer"                   -> "https://lite.tinder.com/",
      "Accept-Encoding"           -> "gzip, deflate, br"
    )
    val request = Request(Method.Post, "/v3/auth/login?locale=ru")
    request.host = host
    request.userAgent =
      "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.98 Mobile Safari/537.36"

    headers.foreach { case (key, value) =>
      request.headerMap.add(key, value)
    }

    request.contentString =
      "RR\nPeyJhbGciOiJIUzI1NiJ9.Nzk3NzQyODM5MDU.4iCeRPqI60O50qYV0BuPdJjxlxFseS_Pr3qgwNfP5Og"
    request
  }
}
