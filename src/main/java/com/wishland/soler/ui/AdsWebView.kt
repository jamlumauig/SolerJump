package com.wishland.soler.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Message
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wishland.soler.databinding.CustomJumpWebViewBinding
import java.util.*

class AdsWebView(context: Context, attrs: AttributeSet): WebView(context,attrs) {

    private val binding: CustomJumpWebViewBinding =
        CustomJumpWebViewBinding.inflate(LayoutInflater.from(context), this, true)

    private val permissionChecker by lazy {
        PermissionHelper(context)
    }

    private val downloadListener = DownloadListener { p0, _, _, _, _ ->
        val uri = Uri.parse(p0)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    init {
        init()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {

        with(binding.wvContent){
            with(settings){
                javaScriptEnabled = true
                defaultTextEncodingName = "UTF-8"
                cacheMode = WebSettings.LOAD_NO_CACHE
                useWideViewPort = true
                pluginState = WebSettings.PluginState.ON
                domStorageEnabled = true
                builtInZoomControls = false
                layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                loadWithOverviewMode = true
                blockNetworkImage = true
                loadsImagesAutomatically = true
                setSupportZoom(false)
                setSupportMultipleWindows(true)
            }
            requestFocusFromTouch()
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            setDownloadListener(downloadListener)
        }

        val webseting: WebSettings = binding.wvContent.settings
        with(webseting){
            val appCacheDir = context.getDir("cache",
                AppCompatActivity.MODE_PRIVATE
            ).path
            domStorageEnabled = true
            allowFileAccess = true
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        binding.wvContent.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress == 100) {
                    binding.wvContent.settings.blockNetworkImage = false
                    binding.pbLoading.visibility = View.GONE
                }else  binding.pbLoading.visibility = View.VISIBLE
            }

            override fun onCreateWindow(
                view: WebView,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message
            ): Boolean {
                val newWebView = WebView(context)
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                newWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        binding.wvContent.loadUrl(url)
                        return true
                    }
                }
                return true
            }
        }

        val settings: WebSettings = binding.wvContent.settings
        settings.javaScriptEnabled = true
        binding.wvContent.setOnLongClickListener { v: View ->
            val result = (v as WebView).hitTestResult
            val type = result.type
            if (type == HitTestResult.UNKNOWN_TYPE) return@setOnLongClickListener false
            when (type) {
                HitTestResult.PHONE_TYPE -> {}
                HitTestResult.EMAIL_TYPE -> {}
                HitTestResult.GEO_TYPE -> {}
                HitTestResult.SRC_IMAGE_ANCHOR_TYPE -> {}
                HitTestResult.IMAGE_TYPE -> {
                    dialogList()
                }
                else -> {}
            }
            true
        }

        binding.wvContent.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                binding.pbLoading.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                binding.pbLoading.visibility = View.GONE
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
            }

            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError
            ) {
                val builder = AlertDialog.Builder(context)
                var message = "SSL Certificate error."
                when (error.primaryError) {
                    SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                    SslError.SSL_EXPIRED -> message = "The certificate has expired."
                    SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                    SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
                }
                message += " Do you want to continue anyway?"
                builder.setTitle("SSL Certificate Error")
                builder.setMessage(message)
                builder.setPositiveButton(
                    "Continue"
                ) { _: DialogInterface?, _: Int -> handler.proceed() }
                builder.setNegativeButton(
                    "Cancel"
                ) { _: DialogInterface?, _: Int -> handler.cancel() }
                val dialog = builder.create()
                dialog.show()
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("http") || url.startsWith("https")) {
                    return super.shouldOverrideUrlLoading(view, url)
                } else if (url.startsWith("intent:")) {
                    val urlSplit = url.split("/").toTypedArray()
                    var send = ""
                    if (urlSplit[2] == "user") {
                        send = "https://m.me/" + urlSplit[3]
                    } else if (urlSplit[2] == "ti") {
                        val data = urlSplit[4]
                        val newSplit = data.split("#").toTypedArray()
                        send = "https://line.me/R/" + newSplit[0]
                    } // showToast(url);
                    val newInt = Intent(Intent.ACTION_VIEW, Uri.parse(send))
                    context.startActivity(newInt)
                } else {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        when(e){
                            is ActivityNotFoundException -> handleUrlException(url)
                            else -> Toast.makeText(context, "Jump failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                return true
            }
        }

        binding.wvContent.setOnKeyListener { _: View?, i: Int, keyEvent: KeyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_BACK && binding.wvContent.canGoBack()) {
                    binding.wvContent.goBack()
                    return@setOnKeyListener true
                }
            }
            false
        }
    }

    private fun handleUrlException(url: String) {
        try{
            val parsedUrl = Uri.parse(url)
            val builder = Uri.Builder()
            builder.scheme("https")
            builder.authority(parsedUrl.authority)
            builder.path(parsedUrl.path)
            val intent = Intent(Intent.ACTION_VIEW, builder.build())
            context.startActivity(intent)
        }catch (e: Exception){
            Toast.makeText(context, "Link failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dialogList() {
        val items = arrayOf("Save Picture", "Cancel")
        val builder = AlertDialog.Builder(context, 3)
        builder.setItems(items) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    override fun canGoBack(): Boolean {
       return binding.wvContent.canGoBack()
    }

    override fun goBack() {
        binding.wvContent.goBack()
    }

    override fun loadUrl(url: String) {
        binding.wvContent.loadUrl(url)
    }

    override fun loadUrl(url: String, additionalHttpHeaders: MutableMap<String, String>) {
        binding.wvContent.loadUrl(url, additionalHttpHeaders)
    }
}