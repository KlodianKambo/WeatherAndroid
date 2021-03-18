package klodian.kambo.weather.ui.weather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import klodian.kambo.weather.R
import klodian.kambo.weather.databinding.ItemDateWeatherBinding
import klodian.kambo.weather.ui.model.UiDateWeather

class DateWeatherRecyclerViewAdapter : RecyclerView.Adapter<DateWeatherViewHolder>() {

    var data = listOf<UiDateWeather>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val viewPool = RecyclerView.RecycledViewPool()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateWeatherViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_date_weather, parent, false)
            .let { ItemDateWeatherBinding.bind(it) }
            .let { binding ->
                DateWeatherViewHolder(binding, viewPool)
            }
    }

    override fun onBindViewHolder(holder: DateWeatherViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

}


class DateWeatherViewHolder(
    private val binding: ItemDateWeatherBinding,
    private val viewPool: RecyclerView.RecycledViewPool
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(uiDateWeather: UiDateWeather) {
        binding.dayDateTextView.text = uiDateWeather.displayableDay
        binding.weatherTemperatureRecyclerView.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        binding.weatherTemperatureRecyclerView.setRecycledViewPool(viewPool)
        binding.weatherTemperatureRecyclerView.adapter =
            WeatherRecyclerViewAdapter().apply { submitList(uiDateWeather.uiWeatherTemperatureList) }
    }

}