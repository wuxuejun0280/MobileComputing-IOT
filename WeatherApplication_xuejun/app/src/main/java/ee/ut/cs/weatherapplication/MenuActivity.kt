package ee.ut.cs.weatherapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ee.ut.cs.adapter.MenuAdapter
import ee.ut.cs.adapter.SearchAdapter
import ee.ut.cs.bean.City
import ee.ut.cs.db.LocalCityDB
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_search.*

class MenuActivity : AppCompatActivity() {

    // adapter and list for recycler view
    private lateinit var myAdapter: MenuAdapter
    var citylist = ArrayList<City>()

    val TAG = MenuActivity::class.java.name


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        add_icon.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivityForResult(intent, 1)
        }

        // create adapter
        myAdapter = MenuAdapter(citylist, this)
        city_recycler.layoutManager = LinearLayoutManager(this)
        city_recycler.adapter = myAdapter
        getCities()
        menu_back_icon.setOnClickListener {
            finish()
        }

    }

    fun getCities(){
        val db = LocalCityDB.getInstance(applicationContext)
        val cityArray = db.getCityDao().loadCities()
        citylist.clear()
        citylist.addAll(cityArray)
        myAdapter.notifyDataSetChanged()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getCities()
        RESULT_OK
    }


}