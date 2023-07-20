package com.hola360.crushlovecalculator.ui.lovetest.result.resultanimation

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.NavMainGraphDirections
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basefragment.AbsBaseFragment
import com.hola360.crushlovecalculator.databinding.FragmentTestResultAnimationBinding
import com.hola360.crushlovecalculator.ui.lovetest.LoveTestFragment
import com.hola360.crushlovecalculator.ui.lovetest.base.BaseLoveTest
import java.util.*

class TestResultAnimation:AbsBaseFragment<FragmentTestResultAnimationBinding>() {

    private val args: TestResultAnimationArgs by lazy {
        TestResultAnimationArgs.fromBundle(requireArguments())
    }
    private val animationViewModel: TestAnimationViewModel by viewModels()
    private var resultString:String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        animationViewModel.resultStringLiveData.observe(this){
            if(it != null){
                resultString= it
            }
        }
    }

    override fun initView() {
        val number= Random().nextInt(NUMBER_ANIMATION)+1
        val animationResId= resources.getIdentifier("testing_$number", "raw", requireContext().packageName)
        binding!!.animationView.setAnimation(animationResId)
        binding!!.animationView.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                findNavController().popBackStack()
                if(resultString!= null){
                    navigateToResult(args.testType)
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animationViewModel.getResultString(args.testResult,
            args.yourProfile.nation?.nationInformation?.code ?: BaseLoveTest.ENGLAND_CODE)
    }

    private fun navigateToResult(type:Int){
        when(type){
            LoveTestFragment.PHOTO_TYPE->{
                findNavController().navigate(NavMainGraphDirections.actionToNavTestResultPhoto(args.yourProfile, args.crushProfile, args.testType, args.testResult, resultString!!))
            }
            LoveTestFragment.NAME_TYPE->{
                findNavController().navigate(NavMainGraphDirections.actionToNavTestResultName(args.yourProfile, args.crushProfile, args.testType, args.testResult, resultString!!))
            }
            LoveTestFragment.FINGERPRINT_TYPE->{
                findNavController().navigate(NavMainGraphDirections.actionToNavTestResultFingerprint(args.yourProfile, args.crushProfile, args.testType, args.testResult, resultString!!))
            }
            LoveTestFragment.BIRTHDAY_TYPE->{
                findNavController().navigate(NavMainGraphDirections.actionToNavTestResultBirthday(args.yourProfile, args.crushProfile, args.testType, args.testResult, resultString!!))
            }
            LoveTestFragment.NATIONALITY_TYPE->{
                findNavController().navigate(NavMainGraphDirections.actionToNavTestResultNation(args.yourProfile, args.crushProfile, args.testType, args.testResult, resultString!!))
            }
            LoveTestFragment.BMI_TYPE->{
                findNavController().navigate(NavMainGraphDirections.actionToNavTestResultBmi(args.yourProfile, args.crushProfile, args.testType, args.testResult, resultString!!))
            }
            else->{
                findNavController().navigate(NavMainGraphDirections.actionToNavTestResultJob(args.yourProfile, args.crushProfile, args.testType, args.testResult, resultString!!))
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_test_result_animation
    }

    companion object{
        private const val NUMBER_ANIMATION=7
    }
}