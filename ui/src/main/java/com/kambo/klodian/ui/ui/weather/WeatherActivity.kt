package com.kambo.klodian.ui.ui.weather

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kambo.klodian.ui.R
import com.kambo.klodian.ui.databinding.ActivityWeatherBinding
import com.kambo.klodian.ui.ui.extensions.hideKeyboard
import com.kambo.klodian.ui.ui.model.UiCompleteWeatherInfo
import com.kambo.klodian.ui.ui.model.UiDateWeather
import com.kambo.klodian.ui.ui.weather.adapter.DateWeatherRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class WeatherActivity : AppCompatActivity() {

    private val viewModel: WeatherViewModel by viewModels()
    private val weatherAdapter = DateWeatherRecyclerViewAdapter()
    private lateinit var binding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            weatherRecyclerView.layoutManager = LinearLayoutManager(this.root.context)
            weatherRecyclerView.adapter = weatherAdapter

            lifecycleScope.launch {
                viewModel.getWeatherResult().collect { result ->
                    result.fold(
                        ifLeft = { showError(it) },
                        ifRight = { uiCompleteWeatherInfo ->
                            uiCompleteWeatherInfo?.let { showResult(it) }
                        })
                }
            }

            lifecycleScope.launch {
                viewModel.isWelcomeEnabled().collect { isWelcomeEnabled ->
                    setWelcomeEnabled(isWelcomeEnabled)
                }
            }

            lifecycleScope.launch {
                viewModel.isLoading().collect { isLoading ->
                    if (isLoading) {
                        cityEditText.hideKeyboard()
                    }
                    showLoading(isLoading)
                }
            }

            lifecycleScope.launch {
                viewModel.getTemperature().collect {
                    mainDegreeFab.setImageResource(it.iconResId)
                }
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
    private fun showError(error: WeatherViewModel.SearchError) {
        when (error) {
            WeatherViewModel.SearchError.FieldCannotBeNull,
            WeatherViewModel.SearchError.Only3ParamsAreAllowed,
            WeatherViewModel.SearchError.PleaseInsertTheCity -> {
                showSearchValidationError(error.errorMessageResId)
            }
            WeatherViewModel.SearchError.NoInternet,
            WeatherViewModel.SearchError.Generic -> {
                showSearchResultError(error.iconResId, getString(error.errorMessageResId))
            }
            is WeatherViewModel.SearchError.WeatherNotFound -> {
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

    private fun showResult(uiCompleteWeatherInfo: UiCompleteWeatherInfo) {
        binding.resultCityTextView.text = uiCompleteWeatherInfo.cityNameResult
        binding.resultDateTextView.text = uiCompleteWeatherInfo.displayableTimeStamp
        showWeather(uiCompleteWeatherInfo.uiDateWeather)
    }

    private fun showWeather(weatherListUi: List<UiDateWeather>) {
        with(binding) {
            weatherRecyclerView.scrollToPosition(0)
            resultsContainer.isVisible = true
            weatherAdapter.data = weatherListUi
            weatherRecyclerView.isVisible = true
        }
    }

    private fun hideResults() {
        with(binding) {
            resultsContainer.isVisible = false
            weatherAdapter.data = emptyList()
            weatherRecyclerView.isVisible = false
        }
    }

    private fun showLoading(isEnabled: Boolean) {
        binding.searchLoadingConstraintLayout.isVisible = isEnabled
    }

    private fun setWelcomeEnabled(isEnabled: Boolean) {
        binding.welcomeConstraintLayout.isVisible = isEnabled
    }


}