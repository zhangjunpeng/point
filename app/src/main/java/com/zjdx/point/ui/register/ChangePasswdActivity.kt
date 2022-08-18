package com.zjdx.point.ui.register

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zjdx.point.ui.theme.PointTheme

class ChangePasswdActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PointTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    resetPd()
                }
            }
        }
    }
}

@Composable
fun resetPd() {
    Column() {
        Text(text = "第一行")
        Text(text = "第二行")
        Text(text = "第三行。。。。")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PointTheme {
        resetPd()
    }
}