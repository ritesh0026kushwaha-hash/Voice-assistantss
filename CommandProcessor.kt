package com.voiceassistants

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

class CommandProcessor(
    private val context: Context,
    private val tts: TtsManager
) {

    fun process(command: String) {

        val text = command
            .trim()
            .lowercase()

        when {

            text.contains("youtube") ||
            text.contains("यूट्यूब") -> {

                val query =
                    text
                        .replace("youtube", "")
                        .replace("यूट्यूब", "")
                        .replace("पर", "")
                        .replace("गाना", "")
                        .trim()

                if (query.isEmpty()) {

                    confirm("YouTube खोल रहा हूँ")

                    openUrl(
                        "https://www.youtube.com/"
                    )

                } else {

                    confirm(
                        "YouTube पर $query search कर रहा हूँ"
                    )

                    openUrl(
                        "https://www.youtube.com/results?search_query=" +
                            Uri.encode(query)
                    )
                }
            }

            text.contains("chrome") ||
            text.contains("क्रोम") -> {

                confirm("Chrome खोल रहा हूँ")

                openApp(
                    "com.android.chrome"
                )
            }

            text.contains("camera") ||
            text.contains("कैमरा") -> {

                confirm("Camera खोल रहा हूँ")

                open(
                    Intent(
                        "android.media.action.IMAGE_CAPTURE"
                    )
                )
            }

            text.contains("settings") ||
            text.contains("सेटिंग") -> {

                confirm("Settings खोल रहा हूँ")

                open(
                    Intent(Settings.ACTION_SETTINGS)
                )
            }

            text.contains("wifi") ||
            text.contains("वाईफाई") -> {

                confirm("Wi-Fi settings खोल रहा हूँ")

                open(
                    Intent(
                        Settings.ACTION_WIFI_SETTINGS
                    )
                )
            }

            text.contains("bluetooth") ||
            text.contains("ब्लूटूथ") -> {

                confirm("Bluetooth settings खोल रहा हूँ")

                open(
                    Intent(
                        Settings.ACTION_BLUETOOTH_SETTINGS
                    )
                )
            }

            text == "home" ||
            text.contains("home जाओ") ||
            text.contains("होम जाओ") -> {

                confirm("Home screen पर जा रहा हूँ")

                if (!VoiceAccessibilityService.goHome()) {
                    tts.speak(
                        "Boss, Phone Control permission चाहिए"
                    )
                }
            }

            text == "back" ||
            text.contains("back जाओ") ||
            text.contains("बैक जाओ") -> {

                confirm("पीछे जा रहा हूँ")

                if (!VoiceAccessibilityService.goBack()) {
                    tts.speak(
                        "Boss, Phone Control permission चाहिए"
                    )
                }
            }

            text.contains("recent") ||
            text.contains("रीसेंट") -> {

                confirm("Recent apps खोल रहा हूँ")

                VoiceAccessibilityService.goRecents()
            }

            text.contains("scroll down") ||
            text.contains("नीचे scroll") ||
            text.contains("नीचे स्क्रोल") -> {

                confirm("नीचे scroll कर रहा हूँ")

                VoiceAccessibilityService.scrollDown()
            }

            else -> {

                tts.speak(
                    "OK Boss, command समझ गया, " +
                        "लेकिन इस काम का action अभी available नहीं है।"
                )
            }
        }
    }

    private fun confirm(action: String) {
        tts.speak("OK Boss, $action")
    }

    private fun openApp(packageName: String) {

        val intent =
            context.packageManager
                .getLaunchIntentForPackage(
                    packageName
                )

        if (intent == null) {

            tts.speak(
                "Boss, ये app phone में नहीं मिला"
            )

            return
        }

        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
        )

        context.startActivity(intent)
    }

    private fun openUrl(url: String) {

        open(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
        )
    }

    private fun open(intent: Intent) {

        try {

            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )

            context.startActivity(intent)

        } catch (_: Exception) {

            tts.speak(
                "Boss, ये काम नहीं कर पाया"
            )
        }
    }
}
