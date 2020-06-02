package com.maulik.focusmode.focusmodesettings.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import java.text.SimpleDateFormat
import java.time.Instant
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
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        input?.let { return simpleDateFormat.format(it) }
        return "Select"
    }

    fun getSelectedHour(liveData: MutableLiveData<Date>): Int {
        val calendar = getInstance()
        val date = liveData.value
        if (date != null) {
            calendar.time = date
        } else {
           return 10
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
}