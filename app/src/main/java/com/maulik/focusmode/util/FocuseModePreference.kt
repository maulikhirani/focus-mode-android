package com.maulik.focusmode.util

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

data class FocusModePreference(
    val startTime: String?,
    val endTime: String?,
    val isNotificationDisabled: Boolean,
    val isSilentEnabled: Boolean
)

fun Context.saveFocusModePref(startTime: String?,
                              endTime:String?,
                              isNotificationDisabled: Boolean?,
                              isSilentEnabled: Boolean?) {
    val preferenceHelper = PreferenceManager.getDefaultSharedPreferences(this)
    preferenceHelper.edit {
        putString(PREF_START_TIME, startTime)
        putString(PREF_END_TIME, endTime)
        if (isNotificationDisabled != null) {
            putBoolean(PREF_NOTIFICATION_DISABLED, isNotificationDisabled)
        }
        if (isSilentEnabled != null) {
            putBoolean(PREF_SILENT_ENABLED, isSilentEnabled)
        }
    }
}

fun Context.getFocusModePref() : FocusModePreference {
    val preferenceHelper = PreferenceManager.getDefaultSharedPreferences(this)
    return FocusModePreference(
        preferenceHelper.getString(PREF_START_TIME, ""),
        preferenceHelper.getString(PREF_END_TIME,""),
        preferenceHelper.getBoolean(PREF_NOTIFICATION_DISABLED, false),
        preferenceHelper.getBoolean(PREF_SILENT_ENABLED, false)
    )
}