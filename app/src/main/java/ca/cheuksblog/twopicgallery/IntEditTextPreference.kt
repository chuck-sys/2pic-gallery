package ca.cheuksblog.twopicgallery

import android.content.Context
import androidx.preference.EditTextPreference

class IntEditTextPreference : EditTextPreference {
    constructor(context: Context?) : super(context)

    override fun getPersistedString(defaultReturnValue: String): String {
        return getPersistedInt(-1).toString()
    }

    override fun persistString(value: String): Boolean {
        return persistInt(Integer.valueOf(value))
    }
}