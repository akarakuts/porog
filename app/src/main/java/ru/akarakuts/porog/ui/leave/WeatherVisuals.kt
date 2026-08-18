/** WeatherVisuals — иконка по коду Open-Meteo (WMO). */
package ru.akarakuts.porog.ui.leave

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

fun weatherIcon(code: Int): ImageVector = when (code) {
    in 0..1 -> Icons.Filled.WbSunny
    in 2..3 -> Icons.Filled.Cloud
    in 45..48 -> Icons.Filled.Cloud
    in 51..67, in 80..82 -> Icons.Filled.Umbrella
    in 71..77, in 85..86 -> Icons.Filled.AcUnit
    in 95..99 -> Icons.Filled.Thunderstorm
    else -> Icons.Filled.WaterDrop
}
