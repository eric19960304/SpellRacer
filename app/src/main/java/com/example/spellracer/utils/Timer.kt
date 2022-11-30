package com.example.spellracer.utils

import android.graphics.Color
import android.widget.TextView
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

class Timer(private var textView: TextView) {
    private var endMillis = 0L // Only read, not Atomic

    suspend fun countDown(durationMillis: Long) {
        endMillis = System.currentTimeMillis() + durationMillis
        var currentMillis = System.currentTimeMillis()
        // End color of replayButton is  red
        val delayMillis = 100L // Time step for updates
        // XML textView
        textView.setBackgroundColor(Color.WHITE)

        while (coroutineContext.isActive && (endMillis > currentMillis)) {
            // XML TextView
            textView.text = String.format(
                "%d",
                ((endMillis - currentMillis) / 1000.0f).toInt()
            )
            delay(delayMillis)
            currentMillis = System.currentTimeMillis()
        }
        textView.text = "Playing..."
    }
}