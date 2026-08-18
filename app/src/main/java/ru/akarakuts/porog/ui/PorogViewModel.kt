package ru.akarakuts.porog.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.akarakuts.porog.PorogApplication
import ru.akarakuts.porog.domain.ChecklistIconCatalog
import ru.akarakuts.porog.domain.LeaveBrief
import ru.akarakuts.porog.widget.refreshPorogWidget

data class PorogUiState(
    val loading: Boolean = true,
    val brief: LeaveBrief? = null,
    val parkingBusy: Boolean = false,
    val parkingError: Boolean = false,
    val newItemDraft: String = "",
    val newItemIcon: String = ChecklistIconCatalog.DEFAULT,
    val iconPickedManually: Boolean = false,
)

class PorogViewModel(application: Application) : AndroidViewModel(application) {
    private val c = (application as PorogApplication).container

    private val _state = MutableStateFlow(PorogUiState())
    val state: StateFlow<PorogUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, parkingError = false)
            val brief = c.leaveBrief.load()
            val snap = c.leaveBrief.snapshotOf(brief)
            c.settings.saveWidgetSnapshot(snap)
            c.scheduler.schedule(
                brief.leaveByMillis,
                brief.notifyMinutesBefore,
                brief.notificationsEnabled && brief.hasNotificationPermission,
            )
            refreshPorogWidget(getApplication())
            _state.value = _state.value.copy(loading = false, brief = brief)
        }
    }

    fun toggleCheck(id: Long, checked: Boolean) {
        viewModelScope.launch {
            c.checklist.toggle(id, checked)
            refresh()
        }
    }

    fun clearChecks() {
        viewModelScope.launch {
            c.checklist.clearChecks()
            refresh()
        }
    }

    fun addItem() {
        val title = _state.value.newItemDraft
        val icon = _state.value.newItemIcon
        viewModelScope.launch {
            c.checklist.addCustom(title, icon)
            _state.value = _state.value.copy(
                newItemDraft = "",
                newItemIcon = ChecklistIconCatalog.DEFAULT,
                iconPickedManually = false,
            )
            refresh()
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            c.checklist.deleteItem(id)
            refresh()
        }
    }

    fun setDraft(value: String) {
        val prev = _state.value
        val keepPick = prev.iconPickedManually && value.isNotBlank()
        _state.value = prev.copy(
            newItemDraft = value,
            newItemIcon = if (keepPick) prev.newItemIcon else ChecklistIconCatalog.match(value),
            iconPickedManually = keepPick,
        )
    }

    fun setNewItemIcon(id: String) {
        _state.value = _state.value.copy(newItemIcon = id, iconPickedManually = true)
    }

    fun setCommute(minutes: Int) {
        viewModelScope.launch {
            c.settings.setCommuteMinutes(minutes)
            refresh()
        }
    }

    fun setNotifyBefore(minutes: Int) {
        viewModelScope.launch {
            c.settings.setNotifyMinutesBefore(minutes)
            refresh()
        }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch {
            c.settings.setNotificationsEnabled(enabled)
            refresh()
        }
    }

    fun parkHere() {
        viewModelScope.launch {
            _state.value = _state.value.copy(parkingBusy = true, parkingError = false)
            val point = c.location.current()
            if (point == null) {
                _state.value = _state.value.copy(parkingBusy = false, parkingError = true)
            } else {
                c.parking.save(point.latitude, point.longitude)
                _state.value = _state.value.copy(parkingBusy = false)
                refresh()
            }
        }
    }

    companion object {
        fun factory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PorogViewModel(app) as T
                }
            }
    }
}
