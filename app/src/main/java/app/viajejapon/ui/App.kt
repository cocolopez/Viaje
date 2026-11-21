package app.viajejapon.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

private data class Tab(val kanji: String, val label: String)

private val Tabs = listOf(
    Tab("旅", "Inicio"),
    Tab("便", "Vuelos"),
    Tab("宿", "Estadía"),
    Tab("日", "Itinerario"),
    Tab("円", "Gastos"),
)

@Composable
fun App(vm: TripViewModel = viewModel()) {
    val trip = vm.trip
    val pager = rememberPagerState { Tabs.size }
    val scope = rememberCoroutineScope()
    val tab = pager.currentPage
    var settings by rememberSaveable { mutableStateOf(false) }

    fun go(i: Int) {
        scope.launch { pager.animateScrollToPage(i) }
    }

    BackHandler(enabled = settings) { settings = false }
    BackHandler(enabled = !settings && tab != 0) { go(0) }

    if (settings) {
        SettingsScreen(
            trip = trip,
            onSave = { updated -> vm.update { updated }; settings = false },
            onBack = { settings = false },
        )
    } else {
        Scaffold(
            containerColor = Washi,
            contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
            bottomBar = {
                NavigationBar(containerColor = Paper) {
                    Tabs.forEachIndexed { i, t ->
                        NavigationBarItem(
                            selected = tab == i,
                            onClick = { go(i) },
                            icon = { Text(t.kanji, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                            label = { Text(t.label, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Shu,
                                selectedTextColor = Shu,
                                indicatorColor = ShuSoft,
                                unselectedIconColor = Nezu,
                                unselectedTextColor = Nezu,
                            ),
                        )
                    }
                }
            },
        ) { pad ->
            // Deslizá el dedo hacia los costados para pasar de una pestaña a otra
            HorizontalPager(state = pager, modifier = Modifier.fillMaxSize()) { page ->
                when (page) {
                    0 -> HomeScreen(trip, pad, onSettings = { settings = true }, onGo = { go(it) })
                    1 -> FlightsScreen(trip, pad, vm)
                    2 -> StaysScreen(trip, pad, vm)
                    3 -> ItineraryScreen(trip, pad, vm, onSettings = { settings = true })
                    else -> ExpensesScreen(trip, pad, vm, onSettings = { settings = true })
                }
            }
        }
    }
}
