package klodian.kambo.weather.ui.weather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import klodian.kambo.weather.R
import klodian.kambo.weather.databinding.ItemWeatherBinding
import klodian.kambo.weather.ui.model.UiTemperature
import klodian.kambo.weather.ui.model.UiWeatherTemperature

class WeatherRecyclerViewAdapter(private val viewPool: RecyclerView.RecycledViewPool) :
    ListAdapter<UiWeatherTemperature, WeatherViewHolder>(WeatherDiffUtils) {

    object WeatherDiffUtils : DiffUtil.ItemCallback<UiWeatherTemperature>() {
        override fun areItemsTheSame(
            oldItem: UiWeatherTemperature,
            newItem: UiWeatherTemperature
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: UiWeatherTemperature,
            newItem: UiWeatherTemperature
        ): Boolean {
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

    fun bind(uiWeather: UiWeatherTemperature) {
        with(uiWeather.weather) {
            binding.itemWeatherTitle.text = title
            binding.itemWeatherDescription.text = description
            Picasso.get().load(iconPath).into(binding.itemWeatherIconImageView)
        }

        bindTemperature(uiWeather.temperature)
    }

    private fun bindTemperature(uiTemperature: UiTemperature) {
        with(binding) {
            temperatureTextView.text = uiTemperature.displayableTemperature
            temperatureMaxTextView.text = uiTemperature.displayableMaxTemperature
            temperatureMinTextView.text = uiTemperature.displayableMinTemperature
            temperatureFeltTextView.text = uiTemperature.displayableFeelsLike
            humidityTextView.text = uiTemperature.humidity
            pressureTextView.text = uiTemperature.pressure
        }
    }
}