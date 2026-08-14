package com.voiceassistants

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class VoiceAccessibilityService :
    AccessibilityService() {

    companion object {

        private var instance:
            VoiceAccessibilityService? = null

        override fun onBind(
            intent: android.content.Intent?
        ): android.os.IBinder? {
            return super.onBind(intent)
        }

        fun goHome(): Boolean {

            return instance?.performGlobalAction(
                GLOBAL_ACTION_HOME
            ) ?: false
        }

        fun goBack(): Boolean {

            return instance?.performGlobalAction(
                GLOBAL_ACTION_BACK
            ) ?: false
        }

        fun goRecents(): Boolean {

            return instance?.performGlobalAction(
                GLOBAL_ACTION_RECENTS
            ) ?: false
        }

        fun scrollDown(): Boolean {

            val service = instance
                ?: return false

            val path = Path()

            path.moveTo(
                500f,
                1300f
            )

            path.lineTo(
                500f,
                500f
            )

            val gesture =
                GestureDescription.Builder()
                    .addStroke(
                        GestureDescription.StrokeDescription(
                            path,
                            0,
                            500
                        )
                    )
                    .build()

            return service.dispatchGesture(
                gesture,
                null,
                null
            )
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(
        event: AccessibilityEvent?
    ) {
        // Future phone-control actions
        // can be handled here.
    }

    override fun onInterrupt() {
        // Accessibility interrupted.
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }
    }
