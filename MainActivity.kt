package com.voiceassistants

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var tts: TtsManager
    private lateinit var processor: CommandProcessor

    private lateinit var statusText: TextView
    private lateinit var assistantMessage: TextView
    private lateinit var commandText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        assistantMessage = findViewById(R.id.assistantMessage)
        commandText = findViewById(R.id.commandText)

        tts = TtsManager(this)
        processor = CommandProcessor(this, tts)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            startListening()
        }

        findViewById<Button>(R.id.stopButton).setOnClickListener {
            stopListening()
        }

        findViewById<Button>(R.id.controlButton).setOnClickListener {
            openAccessibilitySettings()
        }

        if (
            checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                100
            )
        }

        setupSpeechRecognizer()
    }

    private fun setupSpeechRecognizer() {

        speechRecognizer =
            SpeechRecognizer.createSpeechRecognizer(this)

        speechRecognizer.setRecognitionListener(
            object :
                android.speech.RecognitionListener {

                override fun onReadyForSpeech(params: Bundle?) {
                    statusText.text = "LISTENING"
                    assistantMessage.text = "I'm listening, Boss..."
                }

                override fun onBeginningOfSpeech() {
                    statusText.text = "LISTENING"
                }

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    statusText.text = "PROCESSING"
                }

                override fun onError(error: Int) {
                    statusText.text = "READY"
                    assistantMessage.text = "Try again, Boss."
                }

                override fun onResults(results: Bundle?) {

                    val matches =
                        results?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )

                    val command =
                        matches?.firstOrNull()
                            ?: return

                    commandText.text = command

                    statusText.text = "WORKING"

                    processor.process(command)

                    statusText.postDelayed({
                        statusText.text = "READY"
                    }, 2500)
                }

                override fun onPartialResults(
                    partialResults: Bundle?
                ) {}

                override fun onEvent(
                    eventType: Int,
                    params: Bundle?
                ) {}
            }
        )
    }

    private fun startListening() {

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(
                this,
                "Speech recognition is not available",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val intent =
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_PARTIAL_RESULTS,
            true
        )

        speechRecognizer.startListening(intent)
    }

    private fun stopListening() {

        speechRecognizer.stopListening()

        statusText.text = "READY"
        assistantMessage.text = "Ready, Boss."
    }

    private fun openAccessibilitySettings() {

        try {

            startActivity(
                Intent(
                    Settings.ACTION_ACCESSIBILITY_SETTINGS
                )
            )

        } catch (_: Exception) {

            Toast.makeText(
                this,
                "Accessibility settings unavailable",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {

        speechRecognizer.destroy()
        tts.shutdown()

        super.onDestroy()
    }
}
