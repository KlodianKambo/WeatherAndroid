package klodian.kambo.weather

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import klodian.kambo.weather.adapter.WeatherRecyclerViewAdapter
import klodian.kambo.weather.databinding.ActivityMainBinding
import klodian.kambo.weather.extensions.hideKeyboard
import klodian.kambo.weather.model.UiCompleteWeatherInfo
import klodian.kambo.weather.model.UiTemperature
import klodian.kambo.weather.model.UiWeather
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
            weatherRecyclerView.layoutManager = LinearLayoutManager(this.root.context)
            weatherRecyclerView.adapter = weatherAdapter

            viewModel.getWeatherResult().observe(this@MainActivity) { result ->
                result.fold(
                    ifLeft = { showError(it) },
                    ifRight = { showResult(it) })
            }

            viewModel.isWelcomeEnabled().observe(this@MainActivity) { isWelcomeEnabled ->
                setWelcomeEnabled(isWelcomeEnabled)
            }

            viewModel.isLoading().observe(this@MainActivity) { isLoading ->
                if (isLoading) {
                    cityEditText.hideKeyboard()
                }
                showLoading(isLoading)
            }

            viewModel.getTemperature().observe(this@MainActivity) {
                mainDegreeFab.setImageResource(it.iconResId)
            }

            mainDegreeFab.setOnClickListener {
                viewModel.toggleTemperature()
            }

            cityEditText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideResults()
                    clearErrors()
                    viewModel.getWeather(cityEditText.text.toString(), Locale.getDefault())
                    return@OnEditorActionListener true
                }
                false
            })
        }
    }

    // Private fun
    private fun showError(error: MainViewModel.SearchError) {
        when (error) {
            MainViewModel.SearchError.FieldCannotBeNull,
            MainViewModel.SearchError.Only3ParamsAreAllowed,
            MainViewModel.SearchError.PleaseInsertTheCity -> {
                showSearchValidationError(error.errorMessageResId)
            }
            MainViewModel.SearchError.NoInternet,
            MainViewModel.SearchError.Generic -> {
                showSearchResultError(error.iconResId, getString(error.errorMessageResId))
            }
            is MainViewModel.SearchError.WeatherNotFound -> {
                val composedStringError = getString(error.errorMessageResId, error.searchValue)
                showSearchResultError(error.iconResId, composedStringError)
            }
        }
    }

    private fun clearErrors() {
        with(binding) {
            searchErrorConstraintLayout.isVisible = false
            textInputLayout.error = null
        }
    }

    private fun showSearchResultError(@DrawableRes icon: Int?, message: String) {
        with(binding) {
            weatherRecyclerView.isVisible = false
            searchErrorConstraintLayout.isVisible = true
            searchErrorImageView.setImageResource(icon ?: R.drawable.ic_baseline_error_outline)
            searchErrorMessageTextView.text = message
        }
    }

    private fun showSearchValidationError(@StringRes error: Int) {
        binding.textInputLayout.error = getString(error)
    }

    private fun showResult(completeWeatherInfo: UiCompleteWeatherInfo) {
        with(binding) {
            showWeather(completeWeatherInfo.weather)
            showTemperature(completeWeatherInfo.temperature)
            resultCityTextView.text = completeWeatherInfo.cityNameResult
            resultDateTextView.text = completeWeatherInfo.displayableTimeStamp
        }
    }

    private fun showWeather(weatherList: List<UiWeather>) {
        with(binding) {
            resultsContainer.isVisible = true
            weatherAdapter.submitList(weatherList)
            weatherRecyclerView.isVisible = true
        }
    }

    private fun hideResults() {
        with(binding) {
            resultsContainer.isVisible = false
            weatherAdapter.submitList(emptyList())
            weatherRecyclerView.isVisible = false
        }
    }

    private fun showLoading(isEnabled: Boolean) {
        binding.searchLoadingConstraintLayout.isVisible = isEnabled
    }

    private fun setWelcomeEnabled(isEnabled: Boolean) {
        binding.welcomeConstraintLayout.isVisible = isEnabled
    }

    private fun showTemperature(uiTemperature: UiTemperature) {
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