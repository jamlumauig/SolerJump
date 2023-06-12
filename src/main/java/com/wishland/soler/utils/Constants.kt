package com.wishland.soler.utils

import android.Manifest

class Constants {
    companion object{
        val PERMISSIONS = arrayListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}