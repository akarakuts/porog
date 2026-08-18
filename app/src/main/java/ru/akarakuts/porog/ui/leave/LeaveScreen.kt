/** LeaveScreen — главный взгляд на 10 секунд: погода, выход, чеклист, парковка. */
package ru.akarakuts.porog.ui.leave

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ru.akarakuts.porog.R
import ru.akarakuts.porog.domain.ChecklistItem
import ru.akarakuts.porog.domain.LeaveBrief
import ru.akarakuts.porog.ui.PorogViewModel
import ru.akarakuts.porog.ui.checklist.checklistIcon
import ru.akarakuts.porog.ui.components.PorogCard
import ru.akarakuts.porog.ui.components.PorogMark
import ru.akarakuts.porog.ui.components.SectionLabel
import ru.akarakuts.porog.ui.theme.Amber
import ru.akarakuts.porog.ui.theme.Cream
import ru.akarakuts.porog.ui.theme.TealDeep
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveScreen(
    vm: PorogViewModel,
    onHelp: () -> Unit,
    onSettings: () -> Unit,
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    val locLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { vm.refresh() }
    val calLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { vm.refresh() }
    val notifyLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { vm.refresh() }

    LaunchedEffect(Unit) { vm.refresh() }

    val brief = state.brief
    PullToRefreshBox(
        isRefreshing = state.loading && brief != null,
        onRefresh = vm::refresh,
        modifier = Modifier.fillMaxSize(),
    ) {
        if (state.loading && brief == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PorogMark(size = 72.dp)
                Spacer(Modifier.height(20.dp))
                CircularProgressIndicator()
            }
        } else if (brief != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                HomeHeader(onHelp = onHelp, onSettings = onSettings)
                PermissionBlock(
                    brief = brief,
                    onLocation = {
                        locLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                            ),
                        )
                    },
                    onCalendar = { calLauncher.launch(Manifest.permission.READ_CALENDAR) },
                    onNotify = {
                        if (Build.VERSION.SDK_INT >= 33) {
                            notifyLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                )
                HeroCard(brief)
                ChecklistCard(brief, vm)
                ParkingCard(
                    brief = brief,
                    busy = state.parkingBusy,
                    error = state.parkingError,
                    onPark = vm::parkHere,
                    onFind = {
                        val p = brief.parking ?: return@ParkingCard
                        val uri = Uri.parse("geo:${p.latitude},${p.longitude}?q=${p.latitude},${p.longitude}")
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    },
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun HomeHeader(onHelp: () -> Unit, onSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PorogMark(size = 36.dp)
        Column(Modifier.padding(start = 12.dp).weight(1f)) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
            Text(
                stringResource(R.string.home_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onHelp) {
            Icon(
                Icons.AutoMirrored.Filled.HelpOutline,
                contentDescription = stringResource(R.string.nav_help),
            )
        }
        IconButton(onClick = onSettings) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = stringResource(R.string.nav_settings),
            )
        }
    }
}

@Composable
private fun PermissionBlock(
    brief: LeaveBrief,
    onLocation: () -> Unit,
    onCalendar: () -> Unit,
    onNotify: () -> Unit,
) {
    val needNotify = Build.VERSION.SDK_INT >= 33 && !brief.hasNotificationPermission
    if (brief.hasLocationPermission && brief.hasCalendarPermission && !needNotify) return
    PorogCard(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
        Text(
            stringResource(R.string.perm_needed_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        if (!brief.hasLocationPermission) {
            PermissionRow(
                icon = Icons.Filled.MyLocation,
                text = stringResource(R.string.perm_location_rationale),
                onClick = onLocation,
            )
        }
        if (!brief.hasCalendarPermission) {
            PermissionRow(
                icon = Icons.Filled.CalendarMonth,
                text = stringResource(R.string.perm_calendar_rationale),
                onClick = onCalendar,
            )
        }
        if (needNotify) {
            PermissionRow(
                icon = Icons.Filled.Notifications,
                text = stringResource(R.string.perm_notify_rationale),
                onClick = onNotify,
            )
        }
    }
}

@Composable
private fun PermissionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(22.dp).padding(top = 2.dp),
            )
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(1f),
            )
        }
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ),
        ) {
            Text(stringResource(R.string.perm_grant))
        }
    }
}

@Composable
private fun HeroCard(brief: LeaveBrief) {
    val fmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            now = System.currentTimeMillis()
        }
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = TealDeep,
        contentColor = Cream,
    ) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            WeatherHero(brief)
            HorizontalDivider(color = Cream.copy(alpha = 0.18f))
            LeaveHero(brief, fmt, now)
        }
    }
}

@Composable
private fun WeatherHero(brief: LeaveBrief) {
    val w = brief.weather
    if (w == null) {
        Text(
            brief.weatherError ?: stringResource(R.string.weather_error),
            style = MaterialTheme.typography.bodyLarge,
            color = Cream.copy(alpha = 0.85f),
        )
        return
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            weatherIcon(w.weatherCode),
            contentDescription = stringResource(w.adviceRes),
            modifier = Modifier.size(40.dp),
            tint = Amber,
        )
        Text(
            stringResource(R.string.weather_temp, w.temperatureC),
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(start = 8.dp),
            color = Cream,
        )
        Spacer(Modifier.weight(1f))
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Amber.copy(alpha = 0.18f),
        ) {
            Text(
                stringResource(w.adviceRes),
                style = MaterialTheme.typography.labelLarge,
                color = Amber,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }
    if (w.hourlyHint.isNotBlank()) {
        Text(
            stringResource(R.string.hourly_next) + "  ·  " + w.hourlyHint,
            style = MaterialTheme.typography.bodyMedium,
            color = Cream.copy(alpha = 0.72f),
        )
    }
}

@Composable
private fun LeaveHero(brief: LeaveBrief, fmt: SimpleDateFormat, now: Long) {
    val event = brief.nextEvent
    Text(
        stringResource(R.string.leave_title),
        style = MaterialTheme.typography.titleSmall,
        color = Cream.copy(alpha = 0.7f),
    )
    if (event == null) {
        Text(
            if (brief.hasCalendarPermission) {
                stringResource(R.string.leave_no_event)
            } else {
                stringResource(R.string.perm_calendar_rationale)
            },
            style = MaterialTheme.typography.bodyLarge,
            color = Cream.copy(alpha = 0.9f),
        )
        return
    }
    val leave = brief.leaveByMillis?.let { fmt.format(Date(it)) }.orEmpty()
    Text(
        leave,
        style = MaterialTheme.typography.displayMedium,
        color = Amber,
    )
    brief.leaveByMillis?.let { millis ->
        Text(
            relativeLeaveText(millis, now),
            style = MaterialTheme.typography.titleMedium,
            color = relativeLeaveColor(millis, now),
        )
    }
    val title = event.title.ifBlank { stringResource(R.string.leave_untitled) }
    Text(
        stringResource(R.string.leave_event, title, fmt.format(Date(event.startMillis))),
        style = MaterialTheme.typography.bodyLarge,
        color = Cream.copy(alpha = 0.85f),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun relativeLeaveText(leaveBy: Long, now: Long): String {
    val deltaMin = ((leaveBy - now) / 60_000L).toInt()
    return when {
        deltaMin <= 0 -> stringResource(R.string.leave_overdue, formatDuration(-deltaMin))
        deltaMin <= 2 -> stringResource(R.string.leave_now)
        else -> stringResource(R.string.leave_in, formatDuration(deltaMin))
    }
}

@Composable
private fun relativeLeaveColor(leaveBy: Long, now: Long): androidx.compose.ui.graphics.Color {
    val deltaMin = ((leaveBy - now) / 60_000L).toInt()
    return when {
        deltaMin <= 0 -> MaterialTheme.colorScheme.error
        deltaMin <= 15 -> Amber
        else -> Cream.copy(alpha = 0.85f)
    }
}

@Composable
private fun formatDuration(minutes: Int): String {
    val m = minutes.coerceAtLeast(0)
    val h = m / 60
    val rest = m % 60
    return if (h == 0) {
        stringResource(R.string.duration_min, rest)
    } else {
        stringResource(R.string.duration_hour_min, h, rest)
    }
}

@Composable
private fun ChecklistCard(brief: LeaveBrief, vm: PorogViewModel) {
    val done = brief.checklist.count { it.checked }
    val total = brief.checklist.size
    val trailing = when {
        total == 0 -> null
        done == total -> stringResource(R.string.checklist_done)
        else -> stringResource(R.string.checklist_progress, done, total)
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel(stringResource(R.string.checklist_title), trailing = trailing)
        if (total > 0) {
            LinearProgressIndicator(
                progress = { if (total == 0) 0f else done.toFloat() / total },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .height(4.dp),
            )
        }
        brief.checklist.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                row.forEach { item ->
                    ChecklistTile(
                        item = item,
                        onToggle = { vm.toggleCheck(item.id, !item.checked) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        if (done > 0) {
            val haptic = LocalHapticFeedback.current
            OutlinedButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    vm.clearChecks()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Icon(Icons.Filled.RestartAlt, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text(stringResource(R.string.checklist_clear))
            }
        }
    }
}

@Composable
private fun ChecklistTile(
    item: ChecklistItem,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val container by animateColorAsState(
        targetValue = if (item.checked) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        animationSpec = tween(180),
        label = "tile",
    )
    val content by animateColorAsState(
        targetValue = if (item.checked) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(180),
        label = "tileContent",
    )
    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
            onToggle()
        },
        modifier = modifier
            .height(76.dp)
            .semantics {
                role = Role.Checkbox
                toggleableState = if (item.checked) ToggleableState.On else ToggleableState.Off
            },
        shape = MaterialTheme.shapes.large,
        color = container,
        contentColor = content,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                if (item.checked) Icons.Filled.CheckCircle else checklistIcon(item.icon),
                contentDescription = null,
                modifier = Modifier.size(26.dp),
                tint = if (item.checked) MaterialTheme.colorScheme.primary else content,
            )
            Text(
                item.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (item.checked) TextDecoration.LineThrough else null,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ParkingCard(
    brief: LeaveBrief,
    busy: Boolean,
    error: Boolean,
    onPark: () -> Unit,
    onFind: () -> Unit,
) {
    val fmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val haptic = LocalHapticFeedback.current
    val p = brief.parking
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel(stringResource(R.string.parking_title))
        PorogCard {
            if (p != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        stringResource(R.string.parking_saved, fmt.format(Date(p.savedAtMillis))),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            } else {
                Text(
                    stringResource(R.string.parking_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (error) {
                Text(stringResource(R.string.parking_error), color = MaterialTheme.colorScheme.error)
            }
            if (p != null) {
                Button(
                    onClick = onFind,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                ) {
                    Icon(Icons.Filled.DirectionsCar, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text(stringResource(R.string.parking_find))
                }
                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPark()
                    },
                    enabled = !busy && brief.hasLocationPermission,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                ) {
                    ParkButtonContent(busy)
                }
            } else {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPark()
                    },
                    enabled = !busy && brief.hasLocationPermission,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                ) {
                    ParkButtonContent(busy)
                }
            }
        }
    }
}

@Composable
private fun ParkButtonContent(busy: Boolean) {
    if (busy) {
        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
    } else {
        Icon(Icons.Filled.LocationOn, contentDescription = null)
        Spacer(Modifier.size(8.dp))
        Text(stringResource(R.string.parking_save))
    }
}
