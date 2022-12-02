package com.example.spellracer.utils

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan

class DiffTextMaker {
    companion object {
        fun make(answerWords: List<String>, userInputWords: List<String>)
                : Pair<SpannableStringBuilder, Int> {
            val result = SpannableStringBuilder()

            var correctWordsCount = 0

            userInputWords.forEachIndexed { index, inputWord ->
                if(index > 0) {
                    result.append(toColoredText(" ", Color.BLACK))
                }
                if(index < answerWords.size) {
                    val answerWord = answerWords[index]
                    val isCorrect = answerWord.equals(inputWord, ignoreCase = true)
                    result.append(toColoredText(inputWord, if(isCorrect) Color.GREEN else Color.RED))
                    if(isCorrect) {
                        correctWordsCount++
                    }
                } else {
                    result.append(toColoredText(inputWord, Color.RED))
                }
            }

            return Pair(result,correctWordsCount)
        }

        fun toColoredText(s: String, colorId: Int): SpannableString {
            val ss = SpannableString(s)
            ss.setSpan(
                ForegroundColorSpan(colorId),
                0, ss.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            return ss
        }
    }
}