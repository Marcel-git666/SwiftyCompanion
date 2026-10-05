package com.example.swiftycompanion.features.users.domain.models

import kotlin.math.roundToInt

@JvmInline
value class Level(val value: Double) {
    private val hundredths: Int
        get() = (value * 100).roundToInt()

    val whole: Int
        get() = hundredths / 100

    val percent: Int
        get() = hundredths % 100
}