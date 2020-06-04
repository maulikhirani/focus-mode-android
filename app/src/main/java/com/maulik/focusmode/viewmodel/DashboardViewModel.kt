package com.maulik.focusmode.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.maulik.focusmode.util.extensions.toggleDnd
import com.maulik.focusmode.util.extensions.toggleSilentMode
import com.maulik.focusmode.util.backgroundjob.FocusModeStartStopJob
import com.maulik.focusmode.util.isFocusModeEnabled
import com.maulik.focusmode.util.toggleFocusMode

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    fun toggleFocusMode(enable: Boolean) {
        FocusModeStartStopJob.cancelJob()
        getApplication<Application>().toggleFocusMode(enable)
        if (enable) {
            FocusModeStartStopJob.scheduleJob(getApplication())
        } else {
            getApplication<Application>().toggleSilentMode(false)
            getApplication<Application>().toggleDnd(false)
        }
    }

    val focusModeSettingsValid: MutableLiveData<Boolean> = MutableLiveData()
    val focusModeEnabled: MutableLiveData<Boolean> = MutableLiveData(application.isFocusModeEnabled())

}