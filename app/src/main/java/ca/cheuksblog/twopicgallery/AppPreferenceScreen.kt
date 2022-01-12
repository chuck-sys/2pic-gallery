package ca.cheuksblog.twopicgallery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class AppPreferenceScreen : PreferenceFragmentCompat() {
    private var passImage: Preference? = null
    private var idImage: Preference? = null
    private var aboutApp: Preference? = null
    private var howToUse: Preference? = null
    private var getPassImage: ActivityResultLauncher<Array<String>>? = null
    private var getIdImage: ActivityResultLauncher<Array<String>>? = null
    var main: MainFragment? = null
    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        passImage = findPreference("settings_pass_img")
        idImage = findPreference("settings_id_img")
        aboutApp = findPreference("settings_about_btn")
        howToUse = findPreference("settings_instructions_btn")
        passImage!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            getPassImage!!.launch(LAUNCH_FILTER)
            true
        }
        idImage!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            getIdImage!!.launch(LAUNCH_FILTER)
            true
        }
        aboutApp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            AlertDialog.Builder(requireActivity())
                    .setMessage(R.string.app_about)
                    .setTitle(R.string.settings_about)
                    .create()
                    .show()
            true
        }
        howToUse!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            AlertDialog.Builder(requireActivity())
                    .setMessage(R.string.app_help)
                    .setTitle(R.string.settings_help)
                    .create()
                    .show()
            true
        }
        getPassImage = registerForActivityResult(
                OpenDocument()
        ) { uri: Uri? -> handleIntentReturn(PICK_PASS_IMG, uri) }
        getIdImage = registerForActivityResult(
                OpenDocument()
        ) { uri: Uri? -> handleIntentReturn(PICK_ID_IMG, uri) }
    }

    private fun handleIntentReturn(requestCode: Int, selectedImage: Uri?) {
        if (selectedImage == null) {
            // When you cancel it
            return
        }
        val prefs = preferenceManager.sharedPreferences

        // Revoke previous permissions on file to stop app having too many files with permissions
        if (requestCode == PICK_PASS_IMG) {
            try {
                requireActivity().revokeUriPermission(getPassImage(this), Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (ignored: NullPointerException) {
            }
            prefs.edit().putString(PASS_KEY, selectedImage.toString()).apply()
        } else {
            try {
                requireActivity().revokeUriPermission(getIdImage(this), Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (ignored: NullPointerException) {
            }
            prefs.edit().putString(ID_KEY, selectedImage.toString()).apply()
        }

        // Give new permissions to file here to prevent it from being removed (if it is the same image as earlier)
        requireActivity().grantUriPermission(requireActivity().packageName, selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        requireActivity().contentResolver.takePersistableUriPermission(selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        main!!.update()
    }

    companion object {
        const val PICK_PASS_IMG = 1
        const val PICK_ID_IMG = 2
        const val PASS_KEY = "settings_pass_img"
        private const val PASS_ROT_KEY = "settings_pass_rot"
        const val ID_KEY = "settings_id_img"
        private const val ID_ROT_KEY = "settings_id_rot"
        const val PASS_SCALE_KEY = "settings_pass_scale"
        const val ID_SCALE_KEY = "settings_id_scale"
        val LAUNCH_FILTER = arrayOf(
                "image/*"
        )

        fun getPassImage(f: Fragment): Uri {
            val prefs = PreferenceManager.getDefaultSharedPreferences(f.context)
            return Uri.parse(prefs.getString(PASS_KEY, null))
        }

        fun getIdImage(f: Fragment): Uri {
            val prefs = PreferenceManager.getDefaultSharedPreferences(f.context)
            return Uri.parse(prefs.getString(ID_KEY, null))
        }

        fun getPassRotation(f: Fragment): Int {
            val prefs = PreferenceManager.getDefaultSharedPreferences(f.context)
            return prefs.getInt(PASS_ROT_KEY, 0)
        }

        fun getIdRotation(f: Fragment): Int {
            val prefs = PreferenceManager.getDefaultSharedPreferences(f.context)
            return prefs.getInt(ID_ROT_KEY, 0)
        }
    }
}