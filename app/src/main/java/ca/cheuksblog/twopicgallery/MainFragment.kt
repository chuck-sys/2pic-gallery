package ca.cheuksblog.twopicgallery

import ca.cheuksblog.twopicgallery.R
import android.widget.SeekBar
import android.os.Bundle
import android.widget.SeekBar.OnSeekBarChangeListener
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import ca.cheuksblog.twopicgallery.AppPreferenceScreen
import java.lang.NullPointerException

class MainFragment : Fragment(R.layout.fragment_main) {
    private var ivc: ImageView? = null
    private var scaleBar: SeekBar? = null
    private var passImg: Uri? = null
    private var idImg: Uri? = null
    private var isViewingPass = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivc = requireView().findViewById(R.id.ivMain)
        scaleBar = requireView().findViewById(R.id.scaleBar)
        update()
        ivc!!.setOnClickListener {
            isViewingPass = !isViewingPass
            setIVC()
        }
        scaleBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val scale = progress / 100.0f + 0.5f
                ivc!!.scaleX = scale
                ivc!!.scaleY = scale
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                saveScale()
            }
        })
    }

    fun clickImage() {
        ivc!!.performClick()
    }

    private fun loadScale() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val progress = prefs.getInt(if (isViewingPass) AppPreferenceScreen.PASS_SCALE_KEY else AppPreferenceScreen.ID_SCALE_KEY, 49)
        val scale = progress / 100.0f + 0.5f
        ivc!!.scaleX = scale
        ivc!!.scaleY = scale
        scaleBar!!.progress = progress
    }

    private fun saveScale() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        if (isViewingPass) {
            prefs.edit().putInt(AppPreferenceScreen.PASS_SCALE_KEY, scaleBar!!.progress).apply()
        } else {
            prefs.edit().putInt(AppPreferenceScreen.ID_SCALE_KEY, scaleBar!!.progress).apply()
        }
    }

    private fun setIVC() {
        if (isViewingPass) {
            ivc!!.rotation = AppPreferenceScreen.getPassRotation(this).toFloat()
            ivc!!.setImageURI(passImg)
        } else {
            ivc!!.rotation = AppPreferenceScreen.getIdRotation(this).toFloat()
            ivc!!.setImageURI(idImg)
        }
        loadScale()
    }

    fun onSetPassImage() {
        try {
            passImg = AppPreferenceScreen.getPassImage(this)
            setIVC()
        } catch (e: NullPointerException) {
            Log.v("onSetPassImage", "Couldn't get pass image")
        }
    }

    fun onSetIdImage() {
        try {
            idImg = AppPreferenceScreen.getIdImage(this)
            setIVC()
        } catch (e: NullPointerException) {
            Log.v("onSetIdImage", "Couldn't get id image")
        }
    }

    fun update() {
        onSetPassImage()
        onSetIdImage()
    }
}