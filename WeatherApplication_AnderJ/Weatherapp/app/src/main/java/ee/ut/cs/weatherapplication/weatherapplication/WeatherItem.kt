package ee.ut.cs.weatherapplication.weatherapplication

data class WeatherItem(val place: String){
    var temperature: Double = 0.0
    var tempmax: Double = 0.0
    var tempmin: Double = 0.0
    var weather: String = "N/A"
    var weatherdesc: String = "N/A"
    var sunrise: String = "N/A"
    var humidity: Int = 0
    var wind: Double = 0.0

    override fun toString(): String {
        return "Weather in $place: $temperature C, max $tempmax C, min $tempmin C \n" +
                "Weather: $weather($weatherdesc), sunrise at: $sunrise, humidity: $humidity%, wind: $wind m/s"
    }
}
