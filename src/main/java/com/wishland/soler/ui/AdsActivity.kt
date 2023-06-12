package com.wishland.soler.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.wishland.soler.utils.RetrofitHelper
import com.wishland.soler.utils.UiState
import com.wishland.soler.utils.isNetworkConnected

abstract class AdsActivity(private val param:String): AppCompatActivity() {

    private lateinit var adsViewModel: AdsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appInfo = applicationContext.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        RetrofitHelper.baseUrl = appInfo.metaData.getString("appDomain", "https://jamdevvv-default-rtdb.firebaseio.com/" )

        adsViewModel = ViewModelProvider(this)[AdsViewModel::class.java]

        adsViewModel.urlResponse.observe(this){ state ->
            when(state){
                is UiState.Success -> {
                    if(state.data.PackageName == this.packageName){
                        Log.d("JumpCode", state.data.JumpURL ?: "")
                        if(state.data.Status)  onAdsLinkLoaded(state.data.JumpURL ?:"")
                        else onAdsFallback()
                    }else state.data.PackageName?.let { onAdsError(it) }
                }
                is UiState.Error -> onAdsError(state.exception.localizedMessage ?: "")
            }
        }

        if(isNetworkConnected()) adsViewModel.getJumpUrl(param)
        else toNoInternetActivity()
    }

    abstract fun onAdsLinkLoaded(adsLink: String)

    abstract fun onAdsFallback()

    private fun onAdsError(msg: String) {
        Log.d("Error", msg)
        onAdsFallback()
    }

    private fun toNoInternetActivity() {
        startActivity(NoInternetActivity.createIntent(this))
        finish()
    }
}