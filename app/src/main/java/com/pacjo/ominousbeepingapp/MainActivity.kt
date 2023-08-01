package com.pacjo.ominousbeepingapp

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pacjo.ominousbeepingapp.ui.theme.OminousBeepingAppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Initialize MediaPlayer
            mediaPlayer = MediaPlayer.create(this, R.raw.beep_sound)

            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp

            // Calculate the font size based on screen width
            val bannerFontSize = (screenWidth / 6).value.sp

            // Calculate the strokeWidth as a fraction of the screen width
            val strokeWidthFraction = 0.065f    // Adjust  width of the circle (e.g., 0.1f = 10% of screen width)
            val strokeWidth = (screenWidth * strokeWidthFraction).value.dp

            OminousBeepingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f) // Set the aspect ratio to 1:1 (width:height)
                        ) {
                            CircleBlinkingAnimation(strokeWidth, mediaPlayer)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // First word with larger font size and filling the width
                            Text(
                                text = "OMINOUS",
                                style = bannerTextStyle(bannerFontSize)
                            )
                        }
                        // Second and third words with the same font size
                        Text(
                            text = "BEEPING",
                            style = bannerTextStyle(bannerFontSize)
                        )
                        Text(
                            text = "APP",
                            style = bannerTextStyle(bannerFontSize)
                        )
                    }
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release() // Release the MediaPlayer when the activity is destroyed
    }
}

@Composable
fun CircleBlinkingAnimation(strokeWidth: Dp, mediaPlayer: MediaPlayer) {

    val circleList = listOf(
        CircleInfo(0.15f, true),
        CircleInfo(0.4f),
        CircleInfo(0.7f),
        CircleInfo(1f)
    )

    val visibilityTrackers = remember { List(circleList.size) { mutableStateOf(false) } }

    LaunchedEffect(Unit) {
        while (true) {
            visibilityTrackers[0].value = true
            mediaPlayer.start()
            delay(350)
            visibilityTrackers[1].value = true
            mediaPlayer.start()
            delay(300)
            visibilityTrackers[2].value = true
            mediaPlayer.start()
            delay(200)
            visibilityTrackers[3].value = true
            mediaPlayer.start()
            visibilityTrackers[0].value = false
            delay(50)
            visibilityTrackers[1].value = false
            delay(50)
            visibilityTrackers[2].value = false
            delay(50)
            visibilityTrackers[3].value = false
            mediaPlayer.start()
            delay(400)
        }
    }

    circleList.forEachIndexed { index, circleInfo ->
        BlinkingVisibility(visible = visibilityTrackers[index].value) {
            IndicatorRing(
                strokeWidth = strokeWidth,
                radiusFraction = circleInfo.radiusFraction,
                filled = circleInfo.isFilled
            )
        }
    }
}

data class CircleInfo(
    val radiusFraction: Float,
    val isFilled: Boolean = false
)

@Composable
fun bannerTextStyle(fontSize: TextUnit) = TextStyle(
    fontSize = fontSize,
    fontWeight = FontWeight.Bold,
    color = MaterialTheme.colorScheme.primary
)

@Composable
fun IndicatorRing(strokeWidth: Dp, radiusFraction: Float, filled: Boolean = false) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasSize = size.copy()

        // Calculate the radius of the circle (accounting for the stroke width)
        val circleRadius = (canvasSize.minDimension - strokeWidth.toPx()) / 2 * radiusFraction

        if(filled) {
            drawCircle(
                color = Color.Red,
                radius = circleRadius
            )
        } else {
            drawCircle(
                color = Color.Red,
                radius = circleRadius,
                style = Stroke(strokeWidth.toPx())
            )
        }
    }
}

@Composable
fun BlinkingVisibility(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = EnterTransition.None,
        exit = ExitTransition.None
    ) {
        content()
    }
}