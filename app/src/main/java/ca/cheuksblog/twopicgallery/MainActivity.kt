package ca.cheuksblog.twopicgallery

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private var preferences: AppPreferenceScreen? = null
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.context_menu, menu)
        return true
    }

    private val isPreferencesOpen: Boolean
        get() {
            val fm = supportFragmentManager
            return fm.findFragmentById(preferences!!.id) != null
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.context_settings) {
            val fm = supportFragmentManager
            if (!isPreferencesOpen) {
                fm.beginTransaction()
                        .replace(R.id.fragmentContainerView, preferences!!)
                        .addToBackStack(null)
                        .commit()
            } else {
                fm.popBackStack()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN -> if (isPreferencesOpen) {
                super.onKeyUp(keyCode, event)
            } else {
                preferences!!.main!!.clickImage()
                true
            }
            else -> super.onKeyUp(keyCode, event)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (isNotGrantedPermissions) {
            requestPermission()
        }
        preferences = AppPreferenceScreen()
        preferences!!.main = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as MainFragment?
    }

    private val isNotGrantedPermissions: Boolean
        get() {
            val result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            return result != PackageManager.PERMISSION_GRANTED
        }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, R.string.app_perms_issue, Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}