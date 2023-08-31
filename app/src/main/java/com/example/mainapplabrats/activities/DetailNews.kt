package com.example.mainapplabrats.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.fragment.findNavController

//import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.mainapplabrats.databinding.ActivityDetailNewsBinding
import com.example.mainapplabrats.model.ModelArticle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.example.mainapplabrats.MainActivity
import com.example.mainapplabrats.R

class DetailNews : AppCompatActivity() {

    private var _binding: ActivityDetailNewsBinding? = null

    companion object {
        const val DETAIL_NEWS = "DETAIL_NEWS"
    }

    var modelArticle: ModelArticle? = null
    var strNewsURL: String? = null
    var strTitle: String? = null
    var strSubTitle: String? = null
    var strAuthor: String? = null
    var strDesctiption: String? = null
    var strUrlToImg: String? = null
    var strPublishAt: String? = null

    private lateinit var toolbar: Toolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var tvTitle: TextView
    private lateinit var tvSubTitle: TextView
    private lateinit var imageShare: ImageView
    private lateinit var webView: WebView
    private lateinit var backButton : ImageView

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


//        setSupportActionBar(binding.toolbar)
//        assert(supportActionBar != null)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.progressBar.max = 100
        binding.tvSubTitle.setText("Laman Berita")
        //get data intent
        modelArticle = intent.getParcelableExtra(DETAIL_NEWS)
        if (modelArticle != null) {
            strNewsURL = modelArticle?.url
            strTitle = modelArticle?.title
            strSubTitle = modelArticle?.url
            strAuthor = modelArticle?.author
            strDesctiption = modelArticle?.description
            strUrlToImg = modelArticle?.urlToImage
            strPublishAt = modelArticle?.publishedAt
            backButton = binding.btnBackToolbar

//            binding.tvTitle.text = strTitle
//            binding.tvSubTitle.text = strSubTitle
            backButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            //share news
            binding.imageShare.setOnClickListener {
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                share.putExtra(Intent.EXTRA_TEXT, strNewsURL)
                startActivity(Intent.createChooser(share, "Bagikan ke : "))
            }

            //show news
            showWebView()
        }


    }
    private fun showWebView() {
        webView = binding.webView
        progressBar = binding.progressBar

        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        val acceptLanguageHeader = "id-ID" // Kode bahasa Indonesia
        val headers = mutableMapOf<String, String>()
        headers["Accept-Language"] = acceptLanguageHeader

        webView.loadUrl(strNewsURL!!, headers)

        progressBar.progress = 0

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, newUrl: String): Boolean {
                val headers = mutableMapOf<String, String>()
                headers["Accept-Language"] = acceptLanguageHeader
                view.loadUrl(newUrl, headers)
                progressBar.progress = 0
                return true
            }

            override fun onPageStarted(view: WebView, urlStart: String, favicon: Bitmap?) {
                strNewsURL = urlStart
                invalidateOptionsMenu()
            }

            override fun onPageFinished(view: WebView, urlPage: String) {
                progressBar.visibility = View.GONE
                invalidateOptionsMenu()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}