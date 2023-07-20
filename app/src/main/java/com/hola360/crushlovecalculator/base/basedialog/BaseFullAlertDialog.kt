package com.hola360.crushlovecalculator.base.basedialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.hola360.crushlovecalculator.MainActivity
import com.hola360.crushlovecalculator.R


abstract class BaseFullAlertDialog<V : ViewDataBinding?> : DialogFragment() {
    protected var binding: V? = null
    protected lateinit var mainActivity: MainActivity
    protected var curOwnerId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        mainActivity = (requireActivity() as MainActivity)
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

    fun setDialogCurOwnerId(ownerId: Int) {
        this.curOwnerId = ownerId
    }

    protected abstract fun initView()
    protected abstract fun getLayout(): Int

    override fun onDestroy() {
        super.onDestroy()
        if (binding != null) {
            binding!!.unbind()
        }
    }
}