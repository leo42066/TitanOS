package com.titanos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.titanos.domain.model.AppInfo
import com.titanos.domain.model.ModeType
import com.titanos.feature.gamemode.GameModeViewModel
import com.titanos.feature.launcher.LauncherViewModel
import com.titanos.feature.modes.ModeViewModel
import com.titanos.feature.monitoring.MonitoringViewModel
import com.titanos.feature.skyview.SkyViewViewModel

class MainActivity : ComponentActivity() {

    private val app by lazy { application as TitanApplication }

    private val modeViewModel by viewModels<ModeViewModel> {
        ModeViewModel.factory(app.container.modeRepository)
    }

    private val launcherViewModel by viewModels<LauncherViewModel> {
        LauncherViewModel.factory(app.container.systemRepository)
    }

    private val gameModeViewModel by viewModels<GameModeViewModel> {
        GameModeViewModel.factory(app.container.activateGameModeUseCase)
    }

    private val monitoringViewModel by viewModels<MonitoringViewModel> {
        MonitoringViewModel.factory(app.container.observeModeAndMetricsUseCase)
    }

    private val skyViewModel by viewModels<SkyViewViewModel> {
        SkyViewViewModel.factory(app.container.aircraftRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TitanLauncherApp(
                    modeViewModel = modeViewModel,
                    launcherViewModel = launcherViewModel,
                    gameModeViewModel = gameModeViewModel,
                    monitoringViewModel = monitoringViewModel,
                    skyViewViewModel = skyViewModel,
                    openOverlayPermission = {
                        startActivity(
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:$packageName")
                            )
                        )
                    },
                    launchApp = { appInfo ->
                        packageManager.getLaunchIntentForPackage(appInfo.packageName)?.let(::startActivity)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TitanLauncherApp(
    modeViewModel: ModeViewModel,
    launcherViewModel: LauncherViewModel,
    gameModeViewModel: GameModeViewModel,
    monitoringViewModel: MonitoringViewModel,
    skyViewViewModel: SkyViewViewModel,
    openOverlayPermission: () -> Unit,
    launchApp: (AppInfo) -> Unit
) {
    val activeMode by modeViewModel.activeMode.collectAsState()
    val monitoring by monitoringViewModel.state.collectAsState()
    val aircraft by skyViewViewModel.aircraft.collectAsState()

    var togglesOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        skyViewViewModel.startFeed(40.7128, -74.0060)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TitanLauncher • ${activeMode.name}") },
                actions = {
                    AssistChip(onClick = { togglesOpen = true }, label = { Text("Modes") })
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LauncherGrid(
                apps = launcherViewModel.apps,
                onAppOpen = { app ->
                    if (launcherViewModel.isSteamOrGame(app)) {
                        modeViewModel.setMode(ModeType.GameMode)
                        gameModeViewModel.setGameMode(true)
                    }
                    launchApp(app)
                }
            )

            OverlayPanel(
                mode = activeMode,
                monitoring = monitoring,
                aircraftCount = aircraft.size,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    if (togglesOpen) {
        ModalBottomSheet(onDismissRequest = { togglesOpen = false }) {
            QuickModeToggles(
                activeMode = activeMode,
                onModeSelected = {
                    modeViewModel.setMode(it)
                    gameModeViewModel.setGameMode(it == ModeType.GameMode)
                    togglesOpen = false
                },
                onEnableOverlay = openOverlayPermission
            )
        }
    }
}

@Composable
private fun LauncherGrid(
    apps: List<AppInfo>,
    onAppOpen: (AppInfo) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(apps) { app ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAppOpen(app) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(app.label, style = MaterialTheme.typography.titleMedium)
                        Text(app.packageName, style = MaterialTheme.typography.bodySmall)
                    }
                    if (app.isGame) {
                        Icon(Icons.Default.SportsEsports, contentDescription = "Game")
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickModeToggles(
    activeMode: ModeType,
    onModeSelected: (ModeType) -> Unit,
    onEnableOverlay: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Phone Modes", style = MaterialTheme.typography.titleMedium)
        ModeToggleRow(activeMode, onModeSelected)
        AssistChip(onClick = onEnableOverlay, label = { Text("Enable overlay permissions") })
    }
}

@Composable
private fun ModeToggleRow(activeMode: ModeType, onModeSelected: (ModeType) -> Unit) {
    val modes = listOf(
        ModeType.Normal to Icons.Default.Tune,
        ModeType.GameMode to Icons.Default.SportsEsports,
        ModeType.BatterySaver to Icons.Default.Bolt,
        ModeType.Developer to Icons.Default.DeveloperMode,
        ModeType.SkyViewRadar to Icons.Default.Flight
    )
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(modes) { (mode, icon) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onModeSelected(mode) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, contentDescription = mode.name)
                        Text(mode.name)
                    }
                    if (activeMode == mode) {
                        Text("Active", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun OverlayPanel(
    mode: ModeType,
    monitoring: com.titanos.feature.monitoring.MonitoringState,
    aircraftCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .background(Color.Black.copy(alpha = 0.65f), shape = MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatPill("FPS ${monitoring.metrics.fps}")
        StatPill("CPU ${monitoring.metrics.cpuPercent}%")
        StatPill("GPU ${monitoring.metrics.gpuPercent}%")
        StatPill("BAT ${monitoring.metrics.batteryPercent}%")
        if (mode == ModeType.SkyViewRadar) {
            StatPill("Radar $aircraftCount")
        }
    }
}

@Composable
private fun StatPill(label: String) {
    Text(
        text = label,
        color = Color.White,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.background(Color.DarkGray, shape = MaterialTheme.shapes.small).padding(6.dp)
    )
}
