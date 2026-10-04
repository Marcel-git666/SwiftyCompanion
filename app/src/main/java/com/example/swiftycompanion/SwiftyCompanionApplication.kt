package com.example.swiftycompanion

import android.app.Application
import com.example.swiftycompanion.di.AppContainer

class SwiftyCompanionApplication : Application() {
    val container: AppContainer by lazy { AppContainer() }
}