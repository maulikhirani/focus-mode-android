package com.maulik.focusmode.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.maulik.focusmode.R
import com.maulik.focusmode.eventhandler.DashboardEventHandler
import com.maulik.focusmode.viewmodel.DashboardViewModel
import com.maulik.focusmode.databinding.ActivityDashboardBinding
import com.maulik.focusmode.util.extensions.startActivity
import com.maulik.focusmode.util.getFocusModePref
import render.animations.Bounce
import render.animations.Render

class DashboardActivity : AppCompatActivity(),
    DashboardEventHandler {

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
            if (it != null) {
                viewModel.toggleFocusMode(it)
                binding.focusYoga.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val renderer = Render(this)
                    renderer.setAnimation(Bounce().In(binding.focusYoga))
                    renderer.start()
                }
            }
        })
        val renderer = Render(this)
        renderer.setAnimation(Bounce().InUp(binding.tvKotlin))
        renderer.start()
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