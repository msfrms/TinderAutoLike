package com.autolikes.service.core

import com.twitter.conversions.DurationOps.RichDuration
import com.twitter.conversions.StorageUnitOps.RichStorageUnit
import com.twitter.finagle.Http

object HttpClientService {

  def general: Http.Client = Http.client.configuredParams(
    Http.client
      .withMaxResponseSize(5.megabytes)
      .withTransport
      .connectTimeout(3.minute)
      .withSessionQualifier
      .noFailFast
      .params
  )
}
