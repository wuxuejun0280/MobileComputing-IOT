package ee.ut.cs.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import ee.ut.cs.weatherapplication.weatherapplication.WeatherItem
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread
import java.util.*

class WeatherService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        handleData("59.44", "24.75") //coordinates for Tallinn
    }

    private fun handleData(lat: String, lon: String) : WeatherItem{ //currently a bit of a useless class
        thread {
            val json = getWeather(lat, lon)
            val weatherItem = jsonProcessing(json)
        }

        return WeatherItem("empty")
    }

    private fun jsonProcessing(json: JSONObject) : WeatherItem{
        // val json = JSONObject("{\"coord\":{\"lon\":24.75,\"lat\":59.44},\"weather\":[{\"id\":600,\"main\":\"Snow\",\"description\":\"light snow\",\"icon\":\"13n\"}],\"base\":\"stations\",\"main\":{\"temp\":-3.38,\"feels_like\":-6.64,\"temp_min\":-4,\"temp_max\":-2.78,\"pressure\":1018,\"humidity\":92},\"visibility\":10000,\"wind\":{\"speed\":1,\"deg\":130},\"clouds\":{\"all\":90},\"dt\":1607875201,\"sys\":{\"type\":1,\"id\":1330,\"country\":\"EE\",\"sunrise\":1607843471,\"sunset\":1607865579},\"timezone\":7200,\"id\":588409,\"name\":\"Tallinn\",\"cod\":200}")
        val weather = json.getJSONArray("weather")
        val curWeather = weather.getJSONObject(0).getString("main")
        val weatherDesc = weather.getJSONObject(0).getString("description")
        val place = json.getString("name")

        val temps = json.getJSONObject("main")
        val temp = temps.getString("temp")
        val mintemp = temps.getString("temp_min")
        val maxtemp = temps.getString("temp_max")

        val humidity = temps.getString("humidity")
        val wind = json.getJSONObject("wind").getString("speed")

        val sunrise = json.getJSONObject("sys").getString("sunrise").toLong()
        val sdf = java.text.SimpleDateFormat("HH:mm")
        val sunrisetime = sdf.format(Date(sunrise * 1000))

        val weatheritem = WeatherItem(place)
        weatheritem.tempmin = mintemp.toDouble()
        weatheritem.tempmax = maxtemp.toDouble()
        weatheritem.temperature = temp.toDouble()
        weatheritem.weather = curWeather
        weatheritem.weatherdesc = weatherDesc
        weatheritem.humidity = humidity.toInt()
        weatheritem.wind = wind.toDouble()
        weatheritem.sunrise = sunrisetime

        Log.i("jsonstuff", weatheritem.toString()) //what gets written to the weatheritem item

        return weatheritem
    }

    private fun getWeather(lat: String, lon: String) : JSONObject{
        val key = "625fb971cf63d7b9e7c8ff84909c55ac"
        val data = URL("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$key&units=metric").readText()
        Log.i("DATA",data)
        return JSONObject(data)
    }

    private fun getForecast(city: String) : JSONObject{ // to do
        val key = "625fb971cf63d7b9e7c8ff84909c55ac"
        val data = URL("https://api.openweathermap.org/data/2.5/forecast?q=$city&appid=$key").readText()
        return JSONObject(data)
    }
}