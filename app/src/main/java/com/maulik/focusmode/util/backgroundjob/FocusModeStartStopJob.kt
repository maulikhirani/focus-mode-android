package com.maulik.focusmode.util.backgroundjob

import android.content.Context
import android.util.Log
import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.maulik.focusmode.extensions.toggleDnd
import com.maulik.focusmode.extensions.toggleSilentMode
import com.maulik.focusmode.util.getFocusModePref
import java.text.SimpleDateFormat
import java.util.*

class FocusModeStartStopJob: Job() {

    companion object {
        const val TAG = "focus_mode_start_stop_job"
        fun cancelJob() {
            JobManager.instance().cancelAllForTag(TAG);
        }
        fun scheduleJob(context: Context) {
            val focusModePref = context.getFocusModePref()
            try {
                val currentTime = Date()

                val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val calendarStartTime = Calendar.getInstance()
                calendarStartTime.time = simpleDateFormat.parse(focusModePref.startTime)
                val calendarEndTime = Calendar.getInstance()
                calendarEndTime.time = simpleDateFormat.parse(focusModePref.endTime)

                val calendarCurrent = Calendar.getInstance()
                calendarCurrent[Calendar.HOUR_OF_DAY] = calendarStartTime[Calendar.HOUR_OF_DAY]
                calendarCurrent[Calendar.MINUTE] = calendarStartTime[Calendar.MINUTE]

                val interval: Long
                val isStartOfFocusMode: Boolean?

                if (currentTime.before(calendarCurrent.time)) {
                    //schedule start for focus mode
                    isStartOfFocusMode = true
                } else {
                    calendarCurrent[Calendar.HOUR_OF_DAY] = calendarEndTime[Calendar.HOUR_OF_DAY]
                    calendarCurrent[Calendar.MINUTE] = calendarEndTime[Calendar.MINUTE]

                    if (currentTime.before(calendarCurrent.time)) {
                        //schedule end for focus mode
                        isStartOfFocusMode = false

                        context.toggleDnd(focusModePref.isNotificationDisabled)
                        context.toggleSilentMode(focusModePref.isSilentEnabled)

                    } else {
                        //schedule start for focus mode next day
                        calendarCurrent[Calendar.HOUR_OF_DAY] = calendarStartTime[Calendar.HOUR_OF_DAY]
                        calendarCurrent[Calendar.MINUTE] = calendarStartTime[Calendar.MINUTE]
                        calendarCurrent.add(Calendar.DATE, 1)

                        isStartOfFocusMode = true
                    }

                }

                calendarCurrent[Calendar.SECOND] = 0
                interval = calendarCurrent.timeInMillis - currentTime.time
                Log.d("JOB", "scheduleJob: ${calendarCurrent.time}")

                val bundle = PersistableBundleCompat()
                bundle.putBoolean("shouldStart", isStartOfFocusMode)

                JobRequest.Builder(TAG)
                    .setExact(interval)
                    .addExtras(bundle)
                    .build()
                    .schedule()

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onRunJob(params: Params): Result {
        val shouldStart = params.extras.getBoolean("shouldStart", true)
        val focusModePref = context.getFocusModePref()

        context.toggleDnd(if (shouldStart) focusModePref.isNotificationDisabled else false)
        context.toggleSilentMode(if (shouldStart) focusModePref.isSilentEnabled else false)

        scheduleJob(context)
        
        return Result.SUCCESS
    }


}