package klodian.kambo.data

import javax.inject.Inject

class GetIconPath @Inject constructor()  {
    operator fun invoke(iconName: String): String {
         return "https://openweathermap.org/img/wn/$iconName@2x.png"
    }
}