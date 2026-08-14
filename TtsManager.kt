package com.voiceassistants

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TtsManager(
    context: Context
) : TextToSpeech.OnInitListener {

    private val tts =
        TextToSpeech(context, this)

    private var ready = false

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            ready = true

            tts.language =
                Locale.US

            tts.setSpeechRate(0.95f)
        }
    }

    fun speak(text: String) {

        if (!ready) return

        tts.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "voice_assistant"
        )
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
