package com.wishland.soler.data.dto

import androidx.annotation.Keep

@Keep
data class ResponseModel(
    val Button1: String?,
    val Button2: String?,
    val PackageName: String?,
    val Status: Boolean,
    val JumpURL: String?
)
