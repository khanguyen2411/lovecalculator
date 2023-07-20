package com.hola360.crushlovecalculator.ui.confirmprivacy

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.MailTo
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basefragment.AbsBaseFragment
import com.hola360.crushlovecalculator.databinding.LayoutPrivacyTermOfServiceBinding


class PrivacyAndTermOfService : AbsBaseFragment<LayoutPrivacyTermOfServiceBinding>() {

    private val args: PrivacyAndTermOfServiceArgs by lazy {
        PrivacyAndTermOfServiceArgs.fromBundle(requireArguments())
    }
    private val toolbarTitle: String by lazy {
        if (args.isPrivacy) {
            resources.getString(R.string.privacy_policy)
        } else {
            resources.getString(R.string.term_of_service)
        }
    }

    override fun initView() {
        binding!!.toolbar.setNavigationIcon(R.drawable.ic_toolbar_back)
        binding!!.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding!!.toolbar.title = toolbarTitle
        val htmlUrl = if (args.isPrivacy) {
            "file:///android_asset/privacypolicy/policy.html"
        } else {
            "file:///android_asset/privacypolicy/term_of_service.html"
        }

        binding!!.policyWeb.loadUrl(htmlUrl)
        binding!!.policyWeb.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                binding!!.progressBar.visibility = View.GONE
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (request!!.url.toString().startsWith("mailto:")) {
                    val i: Intent = newEmailIntent( request.url.toString().substringAfter(":"))
                    mainActivity.startActivity(i)
                    view!!.reload()
                    return true
                } else {
                    view!!.loadUrl(request.url.toString())
                }
                return true
            }
        }
        binding!!.policyWeb.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onStart() {
        super.onStart()
        mainActivity.supportActionBar?.title = toolbarTitle
    }

    override fun getLayout(): Int {
        return R.layout.layout_privacy_term_of_service
    }

    private fun newEmailIntent(address: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
        intent.type = "message/rfc822"
        return intent
    }
}