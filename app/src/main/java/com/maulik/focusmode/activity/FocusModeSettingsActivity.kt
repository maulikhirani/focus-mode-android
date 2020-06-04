package com.maulik.focusmode.activity

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import com.maulik.focusmode.R
import com.maulik.focusmode.databinding.FocusModeSettingsBinding
import com.maulik.focusmode.util.extensions.isNotificationListenerPermissionAllowed
import com.maulik.focusmode.util.extensions.isSilentModePermissionAllowed
import com.maulik.focusmode.util.extensions.showToast
import com.maulik.focusmode.eventhandler.FocusModeSettingsEventHandler
import com.maulik.focusmode.viewmodel.FocusModeSettingsViewModel
import java.util.*
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE


class FocusModeSettingsActivity : AppCompatActivity(),
    FocusModeSettingsEventHandler {

    private lateinit var binding: FocusModeSettingsBinding
    private val viewModel by viewModels<FocusModeSettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.focus_mode_settings)
        binding.eventHandler = this
        binding.settingsViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.setDataFromPreference()

        viewModel.startTimeString.observe(
            this,
            androidx.lifecycle.Observer {
                binding.tvStartTime.text = it
            }
        )

        viewModel.endTimeString.observe(
            this,
            androidx.lifecycle.Observer {
                binding.tvEndTime.text = it
            }
        )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            binding.switchNotifications.visibility = View.GONE
        } else {
            setupNotifications()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isSilentModePermissionAllowed()) {
            viewModel.silentModeEnabled.value = false
        }
        if (!isNotificationListenerPermissionAllowed()) {
            viewModel.notificationsDisabled.value = false
        }
    }

    private fun setupNotifications() {

        viewModel.notificationsDisabled.observe(this, androidx.lifecycle.Observer {
            binding.switchNotifications.isChecked = it
        })
        viewModel.silentModeEnabled.observe(this, androidx.lifecycle.Observer {
            binding.switchRing.isChecked = it
        })

        class OnCheckedChangeListener : CompoundButton.OnCheckedChangeListener {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                val switch = buttonView as SwitchCompat
                var permissionRequired = false
                if (isChecked) {
                    if (!isSilentModePermissionAllowed() || !isNotificationListenerPermissionAllowed()) {
                        permissionRequired = true
                        switch.setOnCheckedChangeListener(null)
                        showToast(getString(R.string.error_notifications), true)
                        if (switch.id == R.id.switchNotifications) {
                            viewModel.notificationsDisabled.value = false
                        } else {
                            viewModel.silentModeEnabled.value = false
                        }
                        switch.setOnCheckedChangeListener(this)

                        if (!isSilentModePermissionAllowed()) {
                            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                            startActivityForResult(
                                intent,
                                if (switch.id == R.id.switchNotifications) 1 else 2
                            )
                        } else if (!isNotificationListenerPermissionAllowed()) {
                            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                            startActivityForResult(intent, 1)
                        }
                    }

                    if (!permissionRequired) {
                        if (switch.id == R.id.switchNotifications) {
                            viewModel.notificationsDisabled.value = isChecked
                        } else {
                            viewModel.silentModeEnabled.value = isChecked
                        }
                    }

                }
            }
        }

        binding.switchNotifications.setOnCheckedChangeListener(OnCheckedChangeListener())
        binding.switchRing.setOnCheckedChangeListener(OnCheckedChangeListener())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && isNotificationListenerPermissionAllowed()) {
            viewModel.notificationsDisabled.value = true
        } else if (requestCode == 2 && isSilentModePermissionAllowed()) {
            viewModel.silentModeEnabled.value = true
        }
    }

    override fun onStartTimeClick() {
        val timePicker = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val calendar = Calendar.getInstance()
                calendar[HOUR_OF_DAY] = hourOfDay
                calendar[MINUTE] = minute
                viewModel.startTimeLiveData.value = calendar.time
            }, viewModel.getSelectedHour(viewModel.startTimeLiveData),
            viewModel.getSelectedMin(viewModel.startTimeLiveData), false
        )
        timePicker.show()
    }

    override fun onEndTimeClick() {
        if (viewModel.startTimeLiveData.value != null) {
            val timePicker = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    val calendar = Calendar.getInstance()
                    calendar[HOUR_OF_DAY] = hourOfDay
                    calendar[MINUTE] = minute
                    viewModel.endTimeLiveData.value = calendar.time
                }, viewModel.getSelectedHour(viewModel.endTimeLiveData, true),
                viewModel.getSelectedMin(viewModel.endTimeLiveData), false
            )
            timePicker.show()
        } else {
            showToast(getString(R.string.error_select_start_time))
        }
    }

    override fun onSaveClick() {
        val errorMessage = viewModel.validateAndGetErrorMessage()
        if (!TextUtils.isEmpty(errorMessage)) {
            showToast(errorMessage.toString())
        } else {
            viewModel.saveSettings()
            showToast("Preference saved successfully")
            finish()
        }
    }
}