package klodian.kambo.weather.matchers

import android.view.View
import android.widget.EditText
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description

class ImeOptionMatcher(private val imeOption: Int) :
    BoundedMatcher<View, EditText>(EditText::class.java) {
    override fun describeTo(description: Description?) {

    }

    override fun matchesSafely(item: EditText?): Boolean {
        return item?.imeOptions == imeOption
    }
}