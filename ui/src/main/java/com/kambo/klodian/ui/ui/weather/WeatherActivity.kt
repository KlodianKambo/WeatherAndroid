package com.kambo.klodian.ui.ui.weather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

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

            binding.location.setOnClickListener {
                if (hasGeolocPermissions()) {
                    viewModel.fetchByCurrentLocation(Locale.getDefault())
                } else {
                    requestLocationPermissions()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                // Permissions granted, start location updates
                viewModel.fetchByCurrentLocation(Locale.getDefault())
            } else {
                // Permissions denied, handle accordingly (e.g., show an error message)
                hideResults()
                setWelcomeEnabled(false)
                showError(SearchError.PermissionsDenied)
            }
        }
    }


    // Private fun
    private fun showError(error: SearchError) {
        when (error) {
            SearchError.FieldCannotBeNull,
            SearchError.Only3ParamsAreAllowed,
            SearchError.PleaseInsertTheCity -> showSearchValidationError(error.errorMessageResId)
            SearchError.NoInternet,
            SearchError.Generic,
            SearchError.PermissionsDenied -> showSearchResultError(
                error.iconResId,
                getString(error.errorMessageResId)
            )
            is SearchError.WeatherNotFound -> {
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

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun hasGeolocPermissions(): Boolean =
        !(ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
}