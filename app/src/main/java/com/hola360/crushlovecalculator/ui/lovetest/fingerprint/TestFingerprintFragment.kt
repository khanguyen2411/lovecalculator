package com.hola360.crushlovecalculator.ui.lovetest.fingerprint

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.databinding.FragmentTestFingerprintBinding
import com.hola360.crushlovecalculator.ui.lovetest.LoveTestFragment
import com.hola360.crushlovecalculator.ui.lovetest.base.BaseLoveTest
import com.hola360.crushlovecalculator.ui.lovetest.photo.TestPhotoFragment
import com.hola360.crushlovecalculator.utils.LoveTestUtils
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import kotlin.math.abs

class TestFingerprintFragment : BaseLoveTest<FragmentTestFingerprintBinding>(),
    View.OnClickListener {
    private var yourRequireScanTime = 0
    private var crushRequireScanTime = 0
    private var youScanDone = false
    private var crushScanDone = false
    private var yourTouchTime = 0L
    private var crushTouchTime = 0L
    private var saveYourTouchTime = false
    private var saveCrushTouchTime = false
    private var youTouch = false
    private var crushTouch = false
    private var vibrator: Vibrator? = null
    private var yourDeltaProgress = 0f
    private var yourAlpha = BASE_ALPHA
    private var crushDeltaProgress = 0f
    private var crushAlpha = BASE_ALPHA

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        getTestStringResult = false
        resetFingerPrint()
        binding!!.crushScanDone = crushScanDone
        binding!!.yourScanDone = youScanDone
        binding!!.error = false
        binding!!.toolbar.back.setOnClickListener(this)
        binding!!.toolbar.toolbarTitle.text = getString(R.string.love_test_fingerprint_title)
        binding!!.startTest.setOnClickListener(this)
        setupProfile()
        binding!!.yourFingerLayout.outlineProvider = ViewOutlineProvider.BACKGROUND
        binding!!.yourFingerLayout.clipToOutline = true
        binding!!.yourScan.constraintLayout.addTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                val delta = abs(progress - yourDeltaProgress)
                yourAlpha += (delta * 0.8 / 3).toFloat()
                binding!!.yourPhoto.alpha = yourAlpha
                yourDeltaProgress = progress
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (currentId == R.id.start) {
                    binding!!.yourScan.constraintLayout.transitionToEnd()
                }
                if (currentId == R.id.end) {
                    binding!!.yourScan.constraintLayout.transitionToStart()
                }
                yourRequireScanTime += SCAN_SEGMENT_TIME
                if (yourRequireScanTime >= REQUIRE_SCAN_TIME) {
                    if (!youScanDone) {
                        startVibrate()
                        youScanDone = true
                        binding!!.yourScanDone = true
                        youCancelScan()
                    }
                }
            }

            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }
        })
        binding!!.yourBorderView.setOnTouchListener { v, event ->
            event?.let {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        youTouch = true
                        binding!!.scroll.setScrollable(false)
                        SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
                        if (!saveYourTouchTime) {
                            yourTouchTime = System.currentTimeMillis()
                        }
                        if (!youScanDone) {
                            binding!!.yourScan.constraintLayout.visibility = View.VISIBLE
                            binding!!.yourScan.constraintLayout.transitionToEnd()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        youTouch = false
                        if (!crushTouch) {
                            binding!!.scroll.setScrollable(true)
                        }
                        youCancelScan()
                        if (youScanDone) {
                            if (!saveYourTouchTime) {
                                saveYourTouchTime = true
                                yourTouchTime = System.currentTimeMillis() - crushTouchTime
                            }
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (event.x < 0 || event.y < 0 || event.x > v.measuredWidth || event.y > v.measuredHeight) {
                            youTouch = false
                            if (!crushTouch) {
                                binding!!.scroll.setScrollable(true)
                            }
                            youCancelScan()
                        }
                    }
                }
            }
            true
        }
        binding!!.crushFingerLayout.outlineProvider = ViewOutlineProvider.BACKGROUND
        binding!!.crushFingerLayout.clipToOutline = true
        binding!!.crushScan.constraintLayout.addTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                val delta = abs(progress - crushDeltaProgress)
                crushAlpha += (delta * (1 - BASE_ALPHA) / 3)
                binding!!.crushPhoto.alpha = crushAlpha
                crushDeltaProgress = progress
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (currentId == R.id.start) {
                    binding!!.crushScan.constraintLayout.transitionToEnd()
                }
                if (currentId == R.id.end) {
                    binding!!.crushScan.constraintLayout.transitionToStart()
                }
                crushRequireScanTime += SCAN_SEGMENT_TIME
                if (crushRequireScanTime >= REQUIRE_SCAN_TIME) {
                    if (!crushScanDone) {
                        startVibrate()
                        crushScanDone = true
                        binding!!.crushScanDone = true
                        crushCancelScan()
                    }
                }
            }

            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }
        })
        binding!!.crushBorderView.setOnTouchListener { v, event ->
            event?.let {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        crushTouch = true
                        binding!!.scroll.setScrollable(false)
                        SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
                        if (!saveCrushTouchTime) {
                            crushTouchTime = System.currentTimeMillis()
                        }
                        if (!crushScanDone) {
                            binding!!.crushScan.constraintLayout.visibility = View.VISIBLE
                            binding!!.crushScan.constraintLayout.transitionToEnd()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        crushTouch = false
                        if (!youTouch) {
                            binding!!.scroll.setScrollable(true)
                        }
                        crushCancelScan()
                        if (crushScanDone) {
                            if (!saveCrushTouchTime) {
                                saveCrushTouchTime = true
                                crushTouchTime = System.currentTimeMillis() - crushTouchTime
                            }
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (event.x < 0 || event.y < 0 || event.x > v.measuredWidth || event.y > v.measuredHeight) {
                            crushTouch = false
                            if (!youTouch) {
                                binding!!.scroll.setScrollable(true)
                            }
                            crushCancelScan()
                        }
                    }
                }
            }
            true
        }
        vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        binding!!.edtYourName.addTextChangedListener(edtYourNameChangeListener)
        binding!!.edtCrushName.addTextChangedListener(edtCrushNameChangeListener)
        if (youScanDone) {
            binding!!.yourPhoto.alpha = 1f
        }
        if (crushScanDone) {
            binding!!.crushPhoto.alpha = 1f
        }
        KeyboardVisibilityEvent.setEventListener(mainActivity, this) {
            if (it) {
                binding!!.appBarLayout.setExpanded(false)
            } else {
                binding!!.appBarLayout.setExpanded(true)
            }
        }
    }

    private fun resetFingerPrint() {
        yourRequireScanTime = 0
        crushRequireScanTime = 0
        youScanDone = false
        crushScanDone = false
        yourTouchTime = 0L
        crushTouchTime = 0L
        saveYourTouchTime = false
        saveCrushTouchTime = false
    }

    override fun updateYourProfile() {}

    override fun updateCrushProfile() {}

    private fun youCancelScan() {
        binding!!.yourScan.constraintLayout.visibility = View.GONE
        binding!!.yourScan.constraintLayout.progress = 0f
        if (!youScanDone) {
            yourRequireScanTime = 0
            yourDeltaProgress = 0f
            yourAlpha = BASE_ALPHA
            binding!!.yourPhoto.alpha = yourAlpha
        }
    }

    private fun crushCancelScan() {
        binding!!.crushScan.constraintLayout.visibility = View.GONE
        binding!!.crushScan.constraintLayout.progress = 0f
        if (!crushScanDone) {
            crushRequireScanTime = 0
            crushDeltaProgress = 0f
            crushAlpha = BASE_ALPHA
            binding!!.crushPhoto.alpha = crushAlpha
        }
    }

    private fun setupProfile() {
        if (yourProfileModel.name != null) {
            binding!!.edtYourName.setText(yourProfileModel.name)
        }
    }

    private fun startVibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    VIBRATION_DURATION,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            );
        } else {
            //deprecated in API 26
            vibrator?.vibrate(VIBRATION_DURATION);
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> {
                findNavController().popBackStack()
                SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
            }
            R.id.startTest -> {
                checkBeforeStartTest()
            }

        }
    }

    private fun checkBeforeStartTest() {
        if (!youScanDone || !crushScanDone) {
            mainActivity.showSnackBar(
                SnackBarType.Error,
                getString(R.string.love_test_photo_error),
                getString(R.string.love_test_fingerprint_error)
            )
            binding!!.error = true

            return
        }
        if (SystemUtils.checkConnection(requireContext())) {
            testResult = LoveTestUtils.getFingerprintMatch(yourTouchTime, crushTouchTime)
            SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
            navigateToAnimation(LoveTestFragment.FINGERPRINT_TYPE)
        } else {
            showNetworkWarning()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_test_fingerprint
    }

    companion object {
        private const val SCAN_SEGMENT_TIME = 1000
        private const val REQUIRE_SCAN_TIME = 3000
        private const val VIBRATION_DURATION = 200L
        private const val BASE_ALPHA = 0.2f
    }
}