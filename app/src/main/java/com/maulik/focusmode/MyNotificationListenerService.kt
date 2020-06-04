package com.maulik.focusmode

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.maulik.focusmode.util.isNotificationDisabled

class MyNotificationListenerService: NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        clearAll()
    }

    private fun clearAll() {
        if (isNotificationDisabled()) {
            cancelAllNotifications()
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        clearAll()
    }
}