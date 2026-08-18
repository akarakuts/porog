/** HelpScreen — короткие карточки «зачем и как» по разделам приложения. */
package ru.akarakuts.porog.ui.help

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.akarakuts.porog.R
import ru.akarakuts.porog.ui.components.PorogCard

@Composable
fun HelpScreen(onAbout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HelpSection(Icons.Outlined.Home, R.string.help_why_title, R.string.help_why_body)
        HelpSection(Icons.Filled.WbSunny, R.string.help_weather_title, R.string.help_weather_body)
        HelpSection(Icons.Filled.Schedule, R.string.help_leave_title, R.string.help_leave_body)
        HelpSection(Icons.Filled.Checklist, R.string.help_checklist_title, R.string.help_checklist_body)
        HelpSection(Icons.Filled.DirectionsCar, R.string.help_parking_title, R.string.help_parking_body)
        HelpSection(Icons.Filled.Widgets, R.string.help_widget_title, R.string.help_widget_body)
        HelpSection(Icons.Filled.Security, R.string.help_perm_title, R.string.help_perm_body)
        FilledTonalButton(
            onClick = onAbout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            Icon(Icons.Filled.Info, contentDescription = null)
            Text(stringResource(R.string.help_open_about), modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun HelpSection(icon: ImageVector, titleId: Int, bodyId: Int) {
    PorogCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp),
            )
            Text(stringResource(titleId), style = MaterialTheme.typography.titleMedium)
        }
        Text(stringResource(bodyId), style = MaterialTheme.typography.bodyLarge)
    }
}
