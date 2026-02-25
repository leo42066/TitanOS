package com.titanos.feature.skyview

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.titanos.TitanApplication
import com.titanos.domain.model.Aircraft
import kotlin.math.cos
import kotlin.math.sin

class SkyViewLockActivity : ComponentActivity(), SensorEventListener {
    private val app by lazy { application as TitanApplication }
    private val viewModel by viewModels<SkyViewViewModel> {
        SkyViewViewModel.factory(app.container.aircraftRepository)
    }

    private var azimuth: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val aircraft = viewModel.aircraft
                LaunchedEffect(Unit) {
                    viewModel.startFeed(40.7128, -74.0060)
                }
                SkyRadar(aircraft.value, azimuth)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val manager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.also { sensor ->
            manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        val manager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        manager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val e = event ?: return
        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, e.values)
        val orientation = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientation)
        azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}

@Composable
private fun SkyRadar(aircraft: List<Aircraft>, azimuth: Float) {
    var selected by remember { mutableStateOf<Aircraft?>(null) }
    var heading by remember { mutableFloatStateOf(azimuth) }
    heading = azimuth

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF020713))) {
        Text(
            "SkyView Radar AR • Heading ${heading.toInt()}°",
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(aircraft) {
                    detectTapGestures { tap ->
                        selected = aircraft.minByOrNull { plane ->
                            val point = radarPoint(plane.heading + heading)
                            val dx = point.x - tap.x
                            val dy = point.y - tap.y
                            dx * dx + dy * dy
                        }
                    }
                }
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            drawCircle(Color(0xFF0E2A47), radius = size.minDimension * 0.42f, center = center)
            aircraft.forEach { plane ->
                val offset = radarPoint(plane.heading + heading, size.minDimension * 0.35f)
                drawCircle(Color.Cyan, radius = 8f, center = center + offset)
            }
        }
        selected?.let { plane ->
            Text(
                text = "${plane.callsign} alt ${plane.altitudeMeters.toInt()}m spd ${plane.speedMps.toInt()}m/s ${plane.origin.orEmpty()}→${plane.destination.orEmpty()}",
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

private fun radarPoint(degrees: Float, radius: Float = 120f): Offset {
    val rad = Math.toRadians(degrees.toDouble())
    return Offset((cos(rad) * radius).toFloat(), (sin(rad) * radius).toFloat())
}
