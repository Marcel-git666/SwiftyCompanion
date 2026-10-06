package com.example.swiftycompanion.common.device

class ExpiredTokenSimulation {
    @Volatile
    private var isArmed = false

    fun arm() {
        isArmed = true
    }

    fun consume(): Boolean {
        val wasArmed = isArmed
        isArmed = false
        return wasArmed
    }
}