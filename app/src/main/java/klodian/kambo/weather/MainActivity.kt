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
                    ifRight = { showResults(it) })
            }

            cityEditText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    v.hideKeyboard()
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

    private fun showResults(weatherList: List<UiWeather>) {
        with(binding) {
            weatherAdapter.submitList(weatherList)
            weatherRecyclerView.isVisible = true
        }
    }

    private fun hideResults() {
        with(binding) {
            weatherAdapter.submitList(emptyList())
            weatherRecyclerView.isVisible = false
        }
    }
}