package com.hola360.crushlovecalculator.ui.lovediary.eventdiarylovedialog.weather

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.model.weather.WeatherModel
import com.hola360.crushlovecalculator.databinding.ItemViewBinding
import com.hola360.crushlovecalculator.ui.lovetime.emoijconnect.EmojiAdapter
import com.hola360.crushlovecalculator.utils.Constants

class WeatherAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listWeather = mutableListOf<WeatherModel>()

    fun updateData(newData: List<WeatherModel>) {
        listWeather.clear()
        if (!newData.isNullOrEmpty()) {
            listWeather.addAll(newData)
        }
        listWeather.size
        notifyDataSetChanged()
    }

    inner class WeatherViewHolder(private val binding: ItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
//        val image : ImageView = itemView.findViewById(R.id.image)
//        val text : TextView = itemView.findViewById(R.id.text)

        fun bind(position: Int) {
//            binding.image.let {
//                Glide.with(it)
//                    .load("https://files.daily4love.com/content-phase2/weather/Sunny.svg")
//                    .placeholder(R.drawable.ic_loading_event2)
//                    .error(R.drawable.ic_loading_event2).into(it)
//                Log.d("askjhasd","size:"+listWeather[position].icon)
            binding.weather =Constants.WEATHER_SUB+listWeather[position].icon
//            }
            Constants.WEATHER_SUB
            Log.d("asdasf", ""+Constants.WEATHER_SUB+listWeather[position].icon)
            Log.d("asdasf2", ""+listWeather.size)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listWeather.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is WeatherViewHolder) {
            holder.bind(position)
        }
    }

//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        Glide.with(holder.image).load(Constants.WEATHER_SUB+listWeather[position].img).into(holder.image)
//        holder.text.text = listWeather.get(position).text
//        Log.d("ggg",""+Constants.WEATHER_SUB+listWeather[position].img + "size" + listWeather.size)
//    }

//    fun itemChanged() {
//        this.listWeather[0] = (WeatherModel(R.drawable.ic_sad, "Thi is cool",0))
//        notifyItemChanged(0)
//
//    }
}