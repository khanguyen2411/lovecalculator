package com.hola360.crushlovecalculator

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.hola360.crushlovecalculator.utils.SharedPreferenceUtils
import com.hola360.crushlovecalculator.databinding.ActivityMainBinding
import com.hola360.crushlovecalculator.ui.confirmprivacy.ConfirmPrivacyFragment
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.ToastUtils
import com.hola360.crushlovecalculator.utils.snackbar.CustomSnackBar
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var navController: NavController? = null
    private var navHostFragment: Fragment? = null
    private var dataPref: SharedPreferenceUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(null)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        dataPref = SharedPreferenceUtils.getInstance(this)
        setupNavigation()
        setupPrivacy()
    }

    private fun setupNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main)
        navController = findNavController(R.id.nav_host_fragment_main)
    }

    private fun setupPrivacy() {
        if (!dataPref!!.getBooleanValue(PRIVACY)) {
            navController!!.navigate(R.id.nav_confirm_privacy)
        }
    }

    override fun onBackPressed() {
        val fragment = navHostFragment!!.childFragmentManager.fragments[0]
        if (fragment != null) {
            when (fragment) {
                is ConfirmPrivacyFragment -> {
                    finish()
                }
                else -> {
                    super.onBackPressed()
                }
            }
        } else {
            super.onBackPressed()
        }
    }

//    override fun dispatchToumchEvent(ev: MotionEvent?): Boolean {
//        if (ev!!.action == MotionEvent.ACTION_UP) {
//            val view = currentFocus ?: return super.dispatchTouchEvent(ev)
//            val viewLocations = IntArray(2)
//            view.getLocationInWindow(viewLocations)
//            val r = Rect()
//            view.getGlobalVisibleRect(r)
//            val rawX = ev.rawX.toInt()
//            val rawY = ev.rawY.toInt()
//            if (view is EditText) {
//                if (rawY < viewLocations[1] || rawY > viewLocations[1] + view.getHeight()) {
//                    view.clearFocus()
//                    SystemUtils.hideSoftKeyboard(this, binding!!.root)
//                }
//            }
//        }
//        return super.dispatchTouchEvent(ev)
//    }

    fun showSnackBar(
        snackBarType: SnackBarType,
        title: String,
        message: String,
        anchorView: View? = null
    ) {
        CustomSnackBar.make(
            findViewById(android.R.id.content), anchorView ?: binding!!.viewBottom,
            snackBarType,
            title,
            message
        ).show()
    }

    fun showComingSoon(){
        ToastUtils.getInstance(this).showToast("ComingSoon")
    }

    override fun onDestroy() {
        super.onDestroy()
        ToastUtils.release()
    }
    companion object {
        const val PRIVACY = "privacy"
    }
}