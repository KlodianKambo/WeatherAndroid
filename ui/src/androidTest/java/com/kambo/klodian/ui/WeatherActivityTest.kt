package com.kambo.klodian.ui

import android.view.inputmethod.EditorInfo
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import com.kambo.klodian.ui.matchers.ImeOptionMatcher
import com.kambo.klodian.ui.ui.weather.WeatherActivity
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// FIXME test temporary broken due to module division

@RunWith(AndroidJUnit4::class)
class WeatherActivityTest {

    @get:Rule
    val testRules = ActivityTestRule(WeatherActivity::class.java)
    private var activity: WeatherActivity? = null

    private val device: UiDevice = UiDevice.getInstance(getInstrumentation())

    @Before
    fun setUp() {
        activity = testRules.activity
        device.setOrientationNatural()
    }

    @After
    fun tearDown() {
        activity = null
    }

    @Test
    fun when_started_then_show_welcome() {
        onView(withId(R.id.welcome_constraint_layout))
            .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun has_search_ime_option_when_keyboard_shown() {
        onView(withId(R.id.city_edit_text))
            .perform(typeText("Marostica"))
            .check(matches(ImeOptionMatcher(EditorInfo.IME_ACTION_SEARCH)))
    }

    @Test
    fun when_started_and_rotated_then_show_welcome() {
        onView(withId(R.id.welcome_constraint_layout))
            .check(matches(isCompletelyDisplayed()))

        device.setOrientationLeft()

        onView(withId(R.id.welcome_constraint_layout))
            .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun when_search_text_not_empty_then_show_clear() {
        onView(withId(R.id.city_edit_text))
            .perform(typeText("Marostica"))

        onView(withId(R.id.text_input_end_icon))
            .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun when_search_text_not_empty_and_rotated_then_keep_clear_button() {
        onView(withId(R.id.city_edit_text))
            .perform(typeText("Marostica"))

        onView(withId(R.id.text_input_end_icon))
            .check(matches(isCompletelyDisplayed()))

        device.setOrientationLeft()

        onView(withId(R.id.text_input_end_icon))
            .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun when_empty_search_text_then_show_error_after_search_pressed() {
        onView(withId(R.id.city_edit_text))
            .perform(typeText(""))
            .perform(pressImeActionButton())

        onView(withId(R.id.text_input_error_icon))
            .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun when_error_shown_then_clear_after_correct_search() {
        onView(withId(R.id.city_edit_text))
            .perform(typeText(""))
            .perform(pressImeActionButton())

        onView(withId(R.id.text_input_error_icon))
            .check(matches(isCompletelyDisplayed()))

        onView(withId(R.id.city_edit_text))
            .perform(typeText("Marostica"))
            .perform(pressImeActionButton())

        onView(withId(R.id.text_input_error_icon))
            .check(matches(not(isCompletelyDisplayed())))
    }

    @Test
    fun when_comma_only_search_text_then_show_error_after_search_pressed() {
        onView(withId(R.id.city_edit_text))
            .perform(typeText(",,,,,,,"))
            .perform(pressImeActionButton())

        onView(withId(R.id.text_input_error_icon))
            .check(matches(isCompletelyDisplayed()))
    }
}