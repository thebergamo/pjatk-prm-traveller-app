package br.com.thedon.travellerapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.com.thedon.travellerapp.ui.theme.TravellerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TravellerAppTheme {
                TravellerApp()
            }
        }
    }
}