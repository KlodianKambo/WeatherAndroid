package klodian.kambo.weather

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import klodian.kambo.weather.adapter.WeatherRecyclerViewAdapter
import klodian.kambo.weather.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    lateinit var viewModel: MainViewModel
    private val weatherAdapter = WeatherRecyclerViewAdapter()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = getViewModel()

        binding.weatherRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.weatherRecyclerView.adapter = weatherAdapter

        binding.weatherSearch.setOnClickListener {
            viewModel.getWeather(binding.cityEditText.text.toString()).observe(this) {
                weatherAdapter.submitList(it)
            }
        }
    }
}