package com.maulik.focusmode.util.backgroundjob

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator

class FocusModeJobCreator: JobCreator {

    override fun create(tag: String): Job? {
        if (tag == FocusModeStartStopJob.TAG) {
            return FocusModeStartStopJob()
        }
        return null
    }
}