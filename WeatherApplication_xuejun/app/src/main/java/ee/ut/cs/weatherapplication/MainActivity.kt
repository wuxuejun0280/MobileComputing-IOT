package ee.ut.cs.weatherapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import ee.ut.cs.bean.City
import ee.ut.cs.bean.Setting
import ee.ut.cs.db.LocalCityDB
import ee.ut.cs.homework7.LocationHelper
import kotlinx.android.synthetic.main.activity_main.*
import java.security.Permissions
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    lateinit var db: LocalCityDB
    lateinit var setting: Setting

    val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestPermission()){
         permission -> permission
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        menu_icon.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        db = LocalCityDB.getInstance(applicationContext)

        // init setting if not exist
        val settingTemp = db.getSettingDao().loadSetting()
        if (settingTemp.isEmpty()){
            db.getSettingDao().replaceSetting(Setting(1,0,"","",""))
        }
        setting = db.getSettingDao().loadSetting()[0]


        checkForPermissions()
        initDate()

//        if(savedInstanceState == null){
//            supportFragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commitNow()
//        }
    }

    //Creating menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.settings, menu)
        return true
    }

    //Selected menu item event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.id_settings -> { true }
            else -> {true}
        }
    }


    fun initDate(){
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd")).toInt()
        day_2.text = (date+1).toString()
        day_3.text = (date+2).toString()
        day_4.text = (date+3).toString()
        day_5.text = (date+4).toString()
    }

    fun getCurrentLocation(){
        val helper = LocationHelper(applicationContext)
        val location = helper.getCurrentLocationUsingGPS()
        location?.apply {
            matchCity(location.latitude, location.longitude)
        }

    }

    fun matchCity(latitude:Double, longitude:Double){
        val cities = LocalCityDB.getInstance(applicationContext).getCityDao().loadCities()
        for (city in cities){
            if (city.latitude.toDouble()-latitude<1 && city.longitude.toDouble()-longitude<1){
                setting.cityName = city.cityName
                setting.latitude = city.latitude
                setting.longitude = city.longitude
                LocalCityDB
                    .getInstance(applicationContext)
                    .getSettingDao()
                    .replaceSetting(setting)
                return
            }
        }
        getCityName(latitude,longitude)
    }

    fun getCityName(latitude: Double, longitude: Double){
        val googleAPIKey = getString(R.string.google_maps_key)
        Ion.with(applicationContext)
            .load(
                "https://maps.googleapis.com/maps/api/geocode/json?" +
                        "latlng=${latitude.toString()},${longitude.toString()}" +
                        "&language=en" +
                        "&result_type=administrative_area_level_2" +
                        "&key=$googleAPIKey"
            )
            .asJsonObject()
            .setCallback { e, result ->
                result?.apply { addNewCity(result)  }
            }
    }

    fun addNewCity(result:JsonObject){
        val cityName = result
            .get("results").asJsonArray
            .get(0).asJsonObject
            .get("formatted_address").asString
        val latitude = result
            .get("results").asJsonArray
            .get(0).asJsonObject
            .get("geometry").asJsonObject
            .get("location").asJsonObject
            .get("lat").asString
        val longitude = result
            .get("results").asJsonArray
            .get(0).asJsonObject
            .get("geometry").asJsonObject
            .get("location").asJsonObject
            .get("lng").asString
        db.getCityDao().insertCity(City(cityName, latitude.toString(), longitude.toString()))
        setting.cityName = cityName
        setting.latitude = latitude
        setting.longitude = longitude
        db.getSettingDao().replaceSetting(setting)

    }


    //Checking permissions
    private fun checkForPermissions(){
        when{
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ->{
                getCurrentLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                //This case means user previously denied permission. Explain and ask permission again
                showPermissionRequestExplanation(Manifest.permission.ACCESS_FINE_LOCATION, "We really do need location permission!"){
                    requestPermissions.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
            else -> requestPermissions.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    fun Context.showPermissionRequestExplanation(permission: String, message: String, retry: (() -> Unit)? = null){
        AlertDialog.Builder(this).apply {
            setTitle("Note from devops!")
            setMessage(message)
            setPositiveButton("OK") {_, _ -> retry?.invoke()}
        }.show()
    }


}