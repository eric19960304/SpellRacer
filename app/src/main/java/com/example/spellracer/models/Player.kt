package com.example.spellracer.models

data class Player(
    var uid: String = "",
    var userDisplayName: String = "",
    var avgAccuracy : Double = 0.0,
    var avgWpm: Double = 0.0,
    var numberOfGame: Int = 0
)