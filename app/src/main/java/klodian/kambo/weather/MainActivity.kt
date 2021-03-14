package klodian.kambo.weather

import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import klodian.kambo.weather.adapter.WeatherRecyclerViewAdapter
import klodian.kambo.weather.databinding.ActivityMainBinding
import java.util.*

class MainActivity : BaseActivity() {
    lateinit var viewModel: MainViewModel
    private val weatherAdapter = WeatherRecyclerViewAdapter()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = getViewModel()

        with(binding) {
            weatherRecyclerView.layoutManager =
                LinearLayoutManager(this.root.context, LinearLayoutManager.HORIZONTAL, false)

            weatherRecyclerView.adapter = weatherAdapter

            viewModel.getWeatherResult().observe(this@MainActivity) { result ->
                result.fold(
                    ifLeft = { showError(it) },
                    ifRight = { weatherAdapter.submitList(it) })
            }

            weatherSearch.setOnClickListener {
                viewModel.getWeather(cityEditText.text.toString(), Locale.getDefault())
            }

            cityEditText.doAfterTextChanged {
                textInputLayout.error = null
            }
        }
    }

    // Private fun
    private fun showError(error: MainViewModel.SearchError) {
        when (error) {
            MainViewModel.SearchError.FieldCannotBeNull,
            MainViewModel.SearchError.Only3ParamsAreAllowed,
            MainViewModel.SearchError.PleaseInsertTheCity -> {
                binding.textInputLayout.error = getString(error.errorMessageResId)
            }

            MainViewModel.SearchError.NoInternet -> {
                // TODO
            }
            is MainViewModel.SearchError.WeatherNotFound -> {
                // TODO
            }
            MainViewModel.SearchError.Generic -> {
                // TODO
            }
        }

    }
}