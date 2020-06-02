package com.maulik.focusmode.focusmodesettings.ui

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.maulik.focusmode.R
import com.maulik.focusmode.databinding.FocusModeSettingsBinding
import com.maulik.focusmode.extensions.isNotificationPermissionAllowed
import com.maulik.focusmode.extensions.showToast
import com.maulik.focusmode.focusmodesettings.viewmodel.FocusModeSettingsViewModel
import java.util.*
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE


class FocusModeSettingsActivity : AppCompatActivity(), FocusModeSettingsEventHandler {

    private lateinit var binding: FocusModeSettingsBinding
    private val viewModel by viewModels<FocusModeSettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.focus_mode_settings)
        binding.eventHandler = this
        binding.settingsViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.startTimeString.observe(
            this,
            androidx.lifecycle.Observer {
                binding.tvStartTime.setText(it)
            }
        )

        viewModel.endTimeString.observe(
            this,
            androidx.lifecycle.Observer {
                binding.tvEndTime.setText(it)
            }
        )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            binding.switchNotifications.visibility = View.GONE
        } else {
            setupNotifications()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupNotifications() {
        binding.switchNotifications.setOnClickListener {
            val isChecked = binding.switchNotifications.isChecked
            val isPermissionAllowed = isNotificationPermissionAllowed()

            if (isChecked && !isPermissionAllowed) {
                showToast(getString(R.string.error_notifications))
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivityForResult(intent, 1)
                binding.switchNotifications.isChecked = false
            }
        }
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && isNotificationPermissionAllowed()) {
            binding.switchNotifications.isChecked = true
        }
    }

    override fun onStartTimeClick() {
        val timePicker = TimePickerDialog(this,
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                val calendar = Calendar.getInstance()
                calendar[HOUR_OF_DAY] = hourOfDay
                calendar[MINUTE] = minute
                viewModel.startTimeLiveData.value = calendar.time
            }, viewModel.getSelectedHour(viewModel.startTimeLiveData), 
            viewModel.getSelectedMin(viewModel.startTimeLiveData), false)
        timePicker.show()
    }

    override fun onEndTimeClick() {
        if (viewModel.startTimeLiveData.value != null) {
            val timePicker = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    val calendar = Calendar.getInstance()
                    calendar[HOUR_OF_DAY] = hourOfDay
                    calendar[MINUTE] = minute
                    viewModel.endTimeLiveData.value = calendar.time
                }, viewModel.getSelectedHour(viewModel.endTimeLiveData),
                viewModel.getSelectedMin(viewModel.endTimeLiveData), false
            )
            timePicker.show()
        } else {
            showToast(getString(R.string.error_select_start_time))
        }
    }

    override fun onSaveClick() {

    }
}