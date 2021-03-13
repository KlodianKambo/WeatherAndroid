package klodian.kambo.weather

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(), HasViewModelFactory {

    @Inject
    override lateinit var viewModelFactory: ViewModelProvider.Factory

    inline fun <reified VM : ViewModel> getViewModel(): VM =
        ViewModelProviders.of(this, viewModelFactory).get()
}

inline fun <reified VM : ViewModel> Fragment.getFragmentViewModel(): VM =
    ViewModelProviders.of(this, (requireActivity() as HasViewModelFactory).viewModelFactory)
        .get(VM::class.java)

inline fun <reified VM : ViewModel> Fragment.getActivityViewModel(): VM =
    requireActivity().run {
        ViewModelProviders.of(
            this,
            (this as HasViewModelFactory).viewModelFactory
        ).get(VM::class.java)
    }

inline fun <reified VM : ViewModel> FragmentActivity.getViewModel(): VM =
    ViewModelProviders.of(this, (this as HasViewModelFactory).viewModelFactory).get(VM::class.java)

interface HasViewModelFactory {
    var viewModelFactory: ViewModelProvider.Factory
}