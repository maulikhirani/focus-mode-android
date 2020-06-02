package com.maulik.focusmode

import android.animation.Animator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.maulik.focusmode.databinding.LayoutSplashBinding
import com.maulik.focusmode.extensions.startActivityAndFinish
import com.maulik.focusmode.focusmodesettings.ui.FocusModeSettingsActivity
import render.animations.Bounce
import render.animations.Render

class SplashActivity : AppCompatActivity() {

    lateinit var binding: LayoutSplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_splash)

        binding.logoAnim.addAnimatorListener(object : Animator.AnimatorListener {

            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                startActivityAndFinish<FocusModeSettingsActivity>()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })

        val renderer = Render(this)
        renderer.setAnimation(Bounce().In(binding.appTitle))
        renderer.start()

    }
}