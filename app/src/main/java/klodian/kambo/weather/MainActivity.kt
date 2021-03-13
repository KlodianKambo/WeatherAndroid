package klodian.kambo.weather

import android.os.Bundle
import timber.log.Timber

class MainActivity : BaseActivity() {
    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = getViewModel()

        viewModel.getWeather("Rome").observe(this){
            it.forEach { weather ->
                Timber.d("--- $weather")
            }
        }
    }
}