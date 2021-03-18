package klodian.kambo.weather

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

interface HasViewModelFactory {
    var viewModelFactory: ViewModelProvider.Factory
}