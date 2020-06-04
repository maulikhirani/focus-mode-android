package com.maulik.focusmode.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.maulik.focusmode.R
import com.maulik.focusmode.util.TIME_FORMAT_12_HR
import com.maulik.focusmode.util.getFocusModePref
import com.maulik.focusmode.util.saveFocusModePref
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

class FocusModeSettingsViewModel(application: Application) : AndroidViewModel(application) {

    val startTimeLiveData: MutableLiveData<Date> = MutableLiveData()
    val endTimeLiveData: MutableLiveData<Date> = MutableLiveData()
    val notificationsDisabled: MutableLiveData<Boolean> = MutableLiveData()
    val silentModeEnabled: MutableLiveData<Boolean> = MutableLiveData()

    val startTimeString: LiveData<String> = Transformations.map(startTimeLiveData) {
        input: Date? -> formatTime(input)
    }

    val endTimeString: LiveData<String> = Transformations.map(endTimeLiveData) {
        input: Date? -> formatTime(input)
    }

    private fun formatTime(input: Date?): String {
        val simpleDateFormat = SimpleDateFormat(TIME_FORMAT_12_HR, Locale.getDefault())
        input?.let { return simpleDateFormat.format(it) }
        return "Select"
    }

    fun getSelectedHour(liveData: MutableLiveData<Date>, isEndTime: Boolean = false): Int {
        val calendar = getInstance()
        val date = liveData.value
        if (date != null) {
            calendar.time = date
        } else {
           return if (isEndTime) 19 else 10
        }
        return calendar[HOUR_OF_DAY]
    }

    fun getSelectedMin(liveData: MutableLiveData<Date>): Int {
        val calendar = getInstance()
        val date = liveData.value
        if (date != null) {
            calendar.time = date
        } else {
           return 0
        }
        return calendar[MINUTE]
    }

    fun validateAndGetErrorMessage(): String? {
        val simpleDateFormat = SimpleDateFormat(TIME_FORMAT_12_HR, Locale.getDefault())
        var startTime = startTimeLiveData.value
        var endTime = endTimeLiveData.value
        if (startTime != null && endTime != null) {

            startTime = simpleDateFormat.parse(simpleDateFormat.format(startTime))
            endTime = simpleDateFormat.parse(simpleDateFormat.format(endTime))

            if (endTime == startTime || endTime.before(startTime)) {
                return getApplication<Application>().getString(R.string.error_wrong_time)
            }
        } else {
            return getApplication<Application>().getString(R.string.error_select_time)
        }
        return null
    }

    fun saveSettings() {
        val application = getApplication<Application>()
        application.saveFocusModePref(startTimeString.value,
            endTimeString.value,
            notificationsDisabled.value,
            silentModeEnabled.value)
    }

    fun setDataFromPreference() {
        val application = getApplication<Application>()
        val (startTime, endTime, isNotificationDisabled, isSilentEnabled) = application.getFocusModePref()

        val simpleDateFormat = SimpleDateFormat(TIME_FORMAT_12_HR, Locale.getDefault())
        if (startTime != null && endTime != null) {
            try {
                startTimeLiveData.value = simpleDateFormat.parse(startTime)
                endTimeLiveData.value = simpleDateFormat.parse(endTime)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        notificationsDisabled.value = isNotificationDisabled
        silentModeEnabled.value = isSilentEnabled
    }
}