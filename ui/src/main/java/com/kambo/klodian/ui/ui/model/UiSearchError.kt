package com.kambo.klodian.ui.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.kambo.klodian.ui.R

sealed class UiSearchError(
    @StringRes val errorMessageResId: Int,
    @DrawableRes val iconResId: Int? = null
) {
    object FieldCannotBeNull : UiSearchError(R.string.search_input_error_empty)
    object Only3ParamsAreAllowed : UiSearchError(R.string.search_input_error_too_many_params)
    object PleaseInsertTheCity : UiSearchError(R.string.search_input_error_no_param_found)

    object NoInternet :
        UiSearchError(R.string.search_error_no_internet, R.drawable.ic_baseline_cloud_off)

    data class WeatherNotFound(val searchValue: String) :
        UiSearchError(R.string.search_error_not_found, R.drawable.ic_baseline_live_help)


    object PermissionsDenied :
        UiSearchError(R.string.search_error_permission_denied, R.drawable.ic_baseline_error_outline)

    object Generic :
        UiSearchError(R.string.search_error_generic, R.drawable.ic_baseline_error_outline)
}