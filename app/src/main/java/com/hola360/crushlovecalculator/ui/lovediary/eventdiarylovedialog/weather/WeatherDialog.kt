package com.hola360.crushlovecalculator.ui.lovediary.eventdiarylovedialog.weather

import android.util.Log
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseViewModelDialogFragment
import com.hola360.crushlovecalculator.data.model.weather.WeatherModel
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.databinding.LayoutDialogBinding
import com.jackandphantom.carouselrecyclerview.CarouselLayoutManager

class WeatherDialog :  BaseViewModelDialogFragment<LayoutDialogBinding>() {
    private lateinit var viewModel: WeatherViewModel
    var adapterWeather = WeatherAdapter()
    override fun initView() {
        binding!!.btnClose.setOnClickListener(){
            dismiss()
        }
        binding!!.recyleview.apply {
            this.adapter = adapterWeather
            set3DItem(true)
            setAlpha(true)
            setItemSelectListener(object  : CarouselLayoutManager.OnSelected{
                override fun onItemSelected(position: Int) {
                }
            })
        }
        viewModel.fetchData()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setDimAmount(0.8f)
    }

    override fun getLayout(): Int {
        return R.layout.layout_dialog
    }

    override fun initViewModel() {
        val factory  = WeatherViewModel.Factory()
        viewModel = ViewModelProvider(this,factory)[WeatherViewModel::class.java]
        viewModel.uiData.observe(this){
            it?.let{
                when(it.loadDataStatus){
                    LoadDataStatus.SUCCESS ->{
                        val listWeather = (it as DataResponse.DataSuccessResponse).body
                        adapterWeather.updateData(listWeather)
                        Log.d("asfasf",""+listWeather.size)
                    }
                }
            }
        }
viewModel.fetchData()
    }

    companion object{
        fun create() : WeatherDialog {
            val dialog = WeatherDialog()
            return dialog
        }
    }
}