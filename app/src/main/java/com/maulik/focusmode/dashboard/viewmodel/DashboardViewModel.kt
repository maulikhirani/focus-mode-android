package com.maulik.focusmode.dashboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.maulik.focusmode.extensions.toggleDnd
import com.maulik.focusmode.extensions.toggleSilentMode
import com.maulik.focusmode.util.backgroundjob.FocusModeStartStopJob

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    fun toggleFocusMode(enable: Boolean) {
        FocusModeStartStopJob.cancelJob()
        if (enable) {
            FocusModeStartStopJob.scheduleJob(getApplication())
        } else {
            getApplication<Application>().toggleSilentMode(false)
            getApplication<Application>().toggleDnd(false)
        }
    }

    val focusModeSettingsValid: MutableLiveData<Boolean> = MutableLiveData()
    val focusModeEnabled: MutableLiveData<Boolean> = MutableLiveData()

}