package com.wishland.soler.data.repository

import com.wishland.soler.data.data_source.AdsService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.wishland.soler.data.dto.ResponseModel
import com.wishland.soler.domain.repository.AdsRepo
import com.wishland.soler.utils.RetrofitHelper

class AdsRepoImp : AdsRepo {
    private val service: AdsService = RetrofitHelper.service()

    override suspend fun getJumpCodeUrl(param: String): Flow<ResponseModel> = callbackFlow {
        try {
            val response = service.getJumpCode(param)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    trySend(body)
                }
            }
        } catch (e: Exception) {
            //trySend(ResponseModel(e.localizedMessage))
        } finally {
            awaitClose()
        }
    }
}
