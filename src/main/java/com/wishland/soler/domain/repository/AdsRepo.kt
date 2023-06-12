package com.wishland.soler.domain.repository

import com.wishland.soler.data.dto.ResponseModel
import kotlinx.coroutines.flow.Flow

interface AdsRepo {
    suspend fun getJumpCodeUrl(param: String): Flow<ResponseModel>
}