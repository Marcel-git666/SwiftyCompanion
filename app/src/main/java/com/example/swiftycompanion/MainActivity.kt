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
import com.example.swiftycompanion.common.utils.Either
import com.example.swiftycompanion.ui.theme.SwiftyCompanionTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // TEMP: ověření tokenu a prvního requestu – po testu smazat
        lifecycleScope.launch {
            val userSource = (application as SwiftyCompanionApplication).container.userSource

            try {
                repeat(3) {
                    when (val result = userSource.getUser("lformank")) {
                        is Either.Success -> Log.d("UserTest", "Got ${result.value.login}: ${result.value.displayName}")
                        is Either.Failure -> Log.w("UserTest", "Failed: ${result.error}")
                    }
                }
            } catch (e: Exception) {
                Log.e("UserTest", "Failed", e)
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