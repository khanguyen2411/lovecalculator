package com.hola360.crushlovecalculator.dialog.jobdialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.databinding.ItemJobBinding

class JobAdapter(val context:Context, val callback: JobOnclick):RecyclerView.Adapter<BaseViewHolder>(), Filterable {
    var jobs= mutableListOf<String>()
    private val filterJobs= mutableListOf<String>()
    private var selectPosition= 1

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData:MutableList<String>){
        jobs.clear()
        jobs.addAll(newData)
        filterJobs.clear()
        filterJobs.addAll(newData)
        notifyDataSetChanged()
    }

    fun setSelectPosition(selectPos:Int){
        selectPosition= selectPos
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding= ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return  filterJobs.size
    }

    inner class JobViewHolder(val binding: ItemJobBinding):BaseViewHolder(binding.root){
        override fun onBind(position: Int) {
            val jobName= filterJobs[position].substring(0,1).uppercase()+ filterJobs[position].substring(1)
            binding.jobName.text= jobName
            binding.jobLayout.setOnClickListener {
                if(binding.jobName.text != context.resources.getString(R.string.no_result)){
                    callback.onclick(jobName)
                }
            }
            if (selectPosition != -1) {
                if(jobs[selectPosition] == filterJobs[position]){
                    binding.selectIcon.visibility = View.VISIBLE
                } else {
                    binding.selectIcon.visibility = View.GONE
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterName = constraint.toString().trim().lowercase()
                filterJobs.clear()
                if (filterName.isEmpty()) {
                    filterJobs.addAll(jobs)
                } else {
                    val filter= mutableListOf<String>()
                    filter.addAll(jobs.filter { it.lowercase().contains(filterName)})
                    if(filter.isEmpty()){
                        filter.add(context.getString(R.string.no_result))
                    }else{
                        filter.sortBy { it.lowercase().indexOf(filterName) }
                    }
                    filterJobs.addAll(filter)
                }
                val filterResults = FilterResults()
                filterResults.values = filterJobs
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                notifyDataSetChanged()
            }
        }
    }

    interface JobOnclick{
        fun onclick(job:String)
    }

}