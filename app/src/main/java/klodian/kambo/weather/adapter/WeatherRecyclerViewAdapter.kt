package klodian.kambo.weather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import klodian.kambo.weather.R
import klodian.kambo.weather.model.UiWeather
import klodian.kambo.weather.databinding.ItemWeatherBinding

class WeatherRecyclerViewAdapter : ListAdapter<UiWeather, WeatherViewHolder>(WeatherDiffUtils) {
    object WeatherDiffUtils : DiffUtil.ItemCallback<UiWeather>() {
        override fun areItemsTheSame(oldItem: UiWeather, newItem: UiWeather): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UiWeather, newItem: UiWeather): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather, parent, false)
            .let { ItemWeatherBinding.bind(it) }
            .let(::WeatherViewHolder)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}


class WeatherViewHolder(private val binding: ItemWeatherBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(uiWeather: UiWeather) {
        binding.itemWeatherTitle.text = uiWeather.title
        binding.itemWeatherDescription.text = uiWeather.description
        Picasso.get().load(uiWeather.iconPath)
            .into(binding.itemWeatherIconImageView)
    }
}