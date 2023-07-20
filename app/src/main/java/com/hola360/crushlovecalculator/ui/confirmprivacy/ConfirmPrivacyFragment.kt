package com.hola360.crushlovecalculator.ui.confirmprivacy

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.MainActivity
import com.hola360.crushlovecalculator.NavMainGraphDirections
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basefragment.AbsBaseFragment
import com.hola360.crushlovecalculator.databinding.FragmentConfirmPrivacyBinding

class ConfirmPrivacyFragment: AbsBaseFragment<FragmentConfirmPrivacyBinding>() {

    override fun getLayout(): Int {
        return R.layout.fragment_confirm_privacy
    }
    override fun initView() {
        binding!!.confirmCancel.setOnClickListener {
            mainActivity.finish()
        }
        binding!!.agree.setOnClickListener {
            dataPref!!.putBooleanValue(MainActivity.PRIVACY, true)
            findNavController().popBackStack()
        }
        setupTextViewClick()
    }

    private fun setupTextViewClick(){
        val termClick= object : ClickableSpan(){
            override fun onClick(widget: View) {
                findNavController().navigate(NavMainGraphDirections.actionToNavPrivacyTermOfService(false))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireContext(), R.color.black)
                ds.isUnderlineText = true
            }
        }
        val privacyClick= object : ClickableSpan(){
            override fun onClick(widget: View) {
                findNavController().navigate(NavMainGraphDirections.actionToNavPrivacyTermOfService(true))
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireContext(), R.color.black)
                ds.isUnderlineText = true
            }
        }
        val string= resources.getString(R.string.privacy_policy_text)
        val strTerm= resources.getString(R.string.term_of_service)
        val strPrivacy= resources.getString(R.string.privacy_policy)
        val termStartIndex= string.indexOf(strTerm)
        val privacyStartIndex= string.indexOf(strPrivacy)
        val spannableString= SpannableStringBuilder(string)
        spannableString.setSpan(termClick, termStartIndex, termStartIndex+ strTerm.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(privacyClick, privacyStartIndex, privacyStartIndex+ strPrivacy.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding!!.privacyText.text= spannableString
        binding!!.privacyText.movementMethod= LinkMovementMethod.getInstance()
    }
}