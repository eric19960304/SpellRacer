package com.example.spellracer.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class GameResult (
    @DocumentId var id: String = "",
    @ServerTimestamp val timeStamp: Timestamp? = null,
    var uid: String = "",
    var userDisplayName: String = "",
    var accuracy : Int = 0,
    var wpm: Int = 0,
    var answer: String = "",
    var userInput: String = ""
)