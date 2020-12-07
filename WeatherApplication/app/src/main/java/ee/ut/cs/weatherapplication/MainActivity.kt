package ee.ut.cs.weatherapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import java.security.Permissions


class MainActivity : AppCompatActivity() {

    val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestPermission()){
         permission -> permission
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkForPermissions()

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commitNow()
        }
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



    //Checking permissions
    private fun checkForPermissions(){
        when{
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ->{
                //TODO: Here
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