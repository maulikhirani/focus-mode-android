package com.maulik.focusmode.extensions

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.AnimRes
import androidx.core.app.NotificationManagerCompat
import com.maulik.focusmode.util.disableNotifications

inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    block(intent)
    startActivity(intent)
}

inline fun <reified T : Activity> Activity.startActivityAndFinish(block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    block(intent)
    startActivity(intent)
    finish()
}

private class AnimationListener(
    private val onAnimationRepeat: () -> Unit,
    private val onAnimationStart: () -> Unit,
    private val onAnimationEnd: () -> Unit
) : Animation.AnimationListener {
    override fun onAnimationRepeat(p0: Animation?) = onAnimationRepeat()
    override fun onAnimationStart(p0: Animation?) = onAnimationStart()
    override fun onAnimationEnd(p0: Animation?) = onAnimationEnd()
}

fun View.playAnimation(
    @AnimRes animResId: Int,
    onAnimationRepeat: () -> Unit = {},
    onAnimationStart: () -> Unit = {},
    onAnimationEnd: () -> Unit = {}
) = with(AnimationUtils.loadAnimation(context, animResId)) {
    setAnimationListener(AnimationListener(onAnimationRepeat, onAnimationStart, onAnimationEnd))
    startAnimation(this)
}

fun Context.isSilentModePermissionAllowed(): Boolean {
    val manager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        manager.isNotificationPolicyAccessGranted
    } else {
        true
    }
}

fun Context.isNotificationListenerPermissionAllowed(): Boolean {
    val enabledListenerPackages = NotificationManagerCompat.getEnabledListenerPackages(this)
    return enabledListenerPackages.contains(this.packageName)
}

fun Context.showToast(message: String, showLong: Boolean = false) {
    Toast.makeText(
        this,
        message,
        if (showLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()
}

fun Context.toggleDnd(enable: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isNotificationListenerPermissionAllowed()) {
        if (enable) disableNotifications(enable) else disableNotifications(false)
    }
}

fun Context.toggleSilentMode(enable: Boolean) {
    val manager: AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
    if (isSilentModePermissionAllowed()) {
        manager.ringerMode =
            if (enable) AudioManager.RINGER_MODE_SILENT else AudioManager.RINGER_MODE_NORMAL
    }
}
