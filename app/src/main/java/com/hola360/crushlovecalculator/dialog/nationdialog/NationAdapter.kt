package com.hola360.crushlovecalculator.dialog.nationdialog

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.databinding.ItemNationBinding

class NationAdapter(val context: Context, val callback: NationOnclick) :
    RecyclerView.Adapter<BaseViewHolder>(), Filterable {
    var nations = mutableListOf<NationModel>()
    private val filterNation = mutableListOf<NationModel>()
    private var selectPosition = 84

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: MutableList<NationModel>) {
        nations.clear()
        nations.addAll(newData)
        filterNation.clear()
        filterNation.addAll(newData)
        notifyDataSetChanged()
    }

    fun setSelectPosition(selectPos: Int) {
        selectPosition = selectPos
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = ItemNationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return filterNation.size
    }

    inner class NationViewHolder(val binding: ItemNationBinding) : BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            binding.nation = filterNation[position]
            binding.nationLayout.setOnClickListener {
                if (filterNation[position].nationInformation?.name != context.resources.getString(R.string.no_result)) {
                    callback.onclick(filterNation[position].nationInformation?.code!!)
                }
            }
            if (selectPosition != -1) {
                if(nations[selectPosition] == filterNation[position]){
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
                filterNation.clear()
                if (filterName.isEmpty()) {
                    filterNation.addAll(nations)
                } else {
                    val filter = mutableListOf<NationModel>()
                    filter.addAll(nations.filter {
                        it.nationInformation?.name?.lowercase()?.contains(filterName)!!
                    })
                    if (filter.isEmpty()) {
                        filter.add(NationUtils.generateEmptyNation(context))
                    } else {
                        filter.sortBy {
                            it.nationInformation!!.name!!.lowercase().indexOf(filterName)
                        }
                    }
                    filterNation.addAll(filter)
                }
                val filterResults = FilterResults()
                filterResults.values = filterNation
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                notifyDataSetChanged()
            }
        }
    }

    interface NationOnclick {
        fun onclick(nationCode: String)
    }

}