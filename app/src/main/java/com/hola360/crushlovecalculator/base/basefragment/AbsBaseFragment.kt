package com.hola360.crushlovecalculator.base.basefragment

import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hola360.crushlovecalculator.utils.SharedPreferenceUtils
import com.hola360.crushlovecalculator.MainActivity

abstract class AbsBaseFragment<V : ViewDataBinding?> : Fragment() {
    protected lateinit var dataPref: SharedPreferenceUtils
    protected val mainActivity by lazy {
        requireActivity() as MainActivity
    }
    protected var binding: V? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataPref = SharedPreferenceUtils.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
        binding!!.lifecycleOwner = this
        initView()
        return binding!!.root
    }

    protected abstract fun initView()
    protected abstract fun getLayout(): Int

    override fun onDestroyView() {
        super.onDestroyView()
        if (binding != null) {
            binding!!.unbind()
        }
    }
}