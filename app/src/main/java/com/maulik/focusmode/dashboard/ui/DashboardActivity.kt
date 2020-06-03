package com.maulik.focusmode.dashboard.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.maulik.focusmode.R
import com.maulik.focusmode.dashboard.viewmodel.DashboardViewModel
import com.maulik.focusmode.databinding.ActivityDashboardBinding
import com.maulik.focusmode.databinding.FocusModeSettingsBinding
import com.maulik.focusmode.extensions.startActivity
import com.maulik.focusmode.focusmodesettings.ui.FocusModeSettingsActivity
import com.maulik.focusmode.focusmodesettings.viewmodel.FocusModeSettingsViewModel
import com.maulik.focusmode.util.getFocusModePref

class DashboardActivity : AppCompatActivity(), DashboardEventHandler {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel by viewModels<DashboardViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        window.statusBarColor = ContextCompat.getColor(this, R.color.secondaryLightColor)
        binding.viewModel = viewModel
        binding.eventHandler = this
        binding.lifecycleOwner = this

        viewModel.focusModeEnabled.observe(this, Observer {
            if (it != null) viewModel.toggleFocusMode(it)
        })
    }

    override fun onResume() {
        super.onResume()
        val focusModePref = getFocusModePref()
        viewModel.focusModeSettingsValid.value =
            !(focusModePref.startTime.isNullOrEmpty() || focusModePref.endTime.isNullOrEmpty())
    }

    override fun onSettingsClick() {
        startActivity<FocusModeSettingsActivity>()
    }
}