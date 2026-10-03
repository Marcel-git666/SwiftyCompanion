package com.example.swiftycompanion

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.swiftycompanion.common.device.createHttpClient
import com.example.swiftycompanion.features.auth.device.ApiRemoteAuthSource
import com.example.swiftycompanion.ui.theme.SwiftyCompanionTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            val authSource = ApiRemoteAuthSource(
                http = createHttpClient(),
                clientId = BuildConfig.FORTY_TWO_UID,
                clientSecret = BuildConfig.FORTY_TWO_SECRET,
            )
            try {
                val token = authSource.fetchToken()
                Log.d("TokenTest", "OK, expiresIn=${token.expiresIn}s, token=${token.accessToken.take(6)}…")
            } catch (e: Exception) {
                Log.e("TokenTest", "Failed", e)
            }
        }
        setContent {
            SwiftyCompanionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SwiftyCompanionTheme {
        Greeting("Android")
    }
}