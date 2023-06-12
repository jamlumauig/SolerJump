package com.wishland.soler.data.data_source

import com.wishland.soler.data.dto.ResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AdsService {
   @GET("{url}.json?auth=N2yrjEAczZMT6sLSfNViEjRh7EaIo0nuZOQ5KjJz")
   suspend fun getJumpCode(@Path("url") url: String): Response<ResponseModel>
}

