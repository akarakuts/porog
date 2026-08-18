/** SettingsScreen — дорога, напоминание и пункты чеклиста. */
package ru.akarakuts.porog.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ru.akarakuts.porog.R
import ru.akarakuts.porog.ui.PorogViewModel
import ru.akarakuts.porog.ui.checklist.catalogIconIds
import ru.akarakuts.porog.ui.checklist.checklistIcon
import ru.akarakuts.porog.ui.components.PorogCard
import ru.akarakuts.porog.ui.components.SectionLabel
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    vm: PorogViewModel,
    onHelp: () -> Unit,
    onAbout: () -> Unit,
) {
    val state by vm.state.collectAsState()
    val brief = state.brief
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        val commute = brief?.commuteMinutes ?: 30
        SectionLabel(stringResource(R.string.settings_commute))
        PorogCard {
            Text(
                stringResource(R.string.settings_commute_value, commute),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Slider(
                value = commute.toFloat(),
                onValueChange = { vm.setCommute(it.roundToInt()) },
                valueRange = 5f..120f,
                steps = 22,
            )
        }

        val lead = brief?.notifyMinutesBefore ?: 10
        SectionLabel(stringResource(R.string.settings_notify))
        PorogCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(R.string.settings_notify_enable),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = brief?.notificationsEnabled ?: true,
                    onCheckedChange = vm::setNotifications,
                )
            }
            Text(
                stringResource(R.string.settings_notify_lead, lead),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Slider(
                value = lead.toFloat(),
                onValueChange = { vm.setNotifyBefore(it.roundToInt()) },
                valueRange = 0f..60f,
                steps = 11,
                enabled = brief?.notificationsEnabled ?: true,
            )
        }

        SectionLabel(stringResource(R.string.settings_checklist))
        PorogCard {
            brief?.checklist?.forEach { item ->
                ListItem(
                    headlineContent = { Text(item.title) },
                    leadingContent = {
                        Icon(
                            checklistIcon(item.icon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = { vm.deleteItem(item.id) }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.settings_delete_item),
                            )
                        }
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            OutlinedTextField(
                value = state.newItemDraft,
                onValueChange = vm::setDraft,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.settings_new_item)) },
                leadingIcon = {
                    Icon(
                        checklistIcon(state.newItemIcon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { vm.addItem() }),
            )
            Text(
                stringResource(R.string.settings_icon_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            IconLibrary(
                selected = state.newItemIcon,
                onSelect = vm::setNewItemIcon,
            )
            FilledTonalButton(
                onClick = vm::addItem,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = state.newItemDraft.isNotBlank(),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Text(
                    stringResource(R.string.settings_add_item),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }

        PorogCard {
            FilledTonalButton(
                onClick = onHelp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null)
                Text(stringResource(R.string.nav_help), modifier = Modifier.padding(start = 8.dp))
            }
            FilledTonalButton(
                onClick = onAbout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Icon(Icons.Filled.Info, contentDescription = null)
                Text(stringResource(R.string.nav_about), modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IconLibrary(selected: String, onSelect: (String) -> Unit) {
    val pick = stringResource(R.string.settings_icon_pick)
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        catalogIconIds().forEach { id ->
            val on = id == selected
            FilledTonalIconButton(
                onClick = { onSelect(id) },
                modifier = Modifier.size(44.dp),
                colors = if (on) {
                    IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    IconButtonDefaults.filledTonalIconButtonColors()
                },
            ) {
                Icon(
                    checklistIcon(id),
                    contentDescription = pick,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}
