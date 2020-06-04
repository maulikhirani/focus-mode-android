package com.maulik.focusmode.application

import android.app.Application
import com.evernote.android.job.JobManager
import com.maulik.focusmode.util.backgroundjob.FocusModeJobCreator

class FocusModeApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        JobManager.create(this).addJobCreator(FocusModeJobCreator())
    }
}