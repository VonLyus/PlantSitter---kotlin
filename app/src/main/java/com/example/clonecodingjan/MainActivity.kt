package com.example.clonecodingjan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.clonecodingjan.ui.theme.CloneCodingJanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CloneCodingJanTheme {
                Surface(
                    modifier = Modifier
                        .background(color = Color.Yellow)
                        .padding(16.dp)
                ) {
                    Text("CloneCoding")
                    Text("Start")
                }
            }
        }
    }
}
