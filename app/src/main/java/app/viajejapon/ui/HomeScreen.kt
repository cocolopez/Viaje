package app.viajejapon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.viajejapon.data.Trip
import app.viajejapon.data.bookingsBase
import app.viajejapon.data.baseMoney
import app.viajejapon.data.expensesBase
import app.viajejapon.data.fmtDate
import app.viajejapon.data.parseDate
import app.viajejapon.data.spentBase
import app.viajejapon.data.toBase
import app.viajejapon.data.yenWithoutRate
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun HomeScreen(trip: Trip, pad: PaddingValues, onSettings: () -> Unit, onGo: (Int) -> Unit) {
    val today = LocalDate.now()
    val start = parseDate(trip.start)
    val end = parseDate(trip.end)
    val d = if (start != null) ChronoUnit.DAYS.between(today, start) else 0L
    val big: String
    val small: String
    when {
        start == null -> { big = "日程未定"; small = "Definí las fechas en Ajustes" }
        d > 0 -> { big = "あと $d 日"; small = "días para partir" }
        end == null || !today.isAfter(end) -> { big = "第${1 - d}日"; small = "día ${1 - d} del viaje" }
        else -> { big = "完"; small = "viaje terminado" }
    }

    Box(Modifier.fillMaxSize()) {
    InkArt(
        ArtKind.Pagoda,
        Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = pad.calculateBottomPadding())
            .fillMaxWidth()
            .height(300.dp),
    )
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = pad.calculateBottomPadding() + 24.dp),
    ) {
        // Cabecera: olas, sol rojo y cuenta regresiva
        Box(Modifier.fillMaxWidth().height(250.dp)) {
            InkMountains(Modifier.matchParentSize())
            Box(
                Modifier
                    .matchParentSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Washi))),
            )
            TextButton(
                onClick = onSettings,
                modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(end = 8.dp),
            ) { Text("設  Ajustes", color = Ai, fontWeight = FontWeight.Bold) }
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 52.dp, end = 16.dp)
                    .size(112.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(Modifier.matchParentSize()) {
                    ensoRing(
                        Sumi.copy(alpha = 0.85f),
                        center,
                        radius = size.minDimension * 0.46f,
                        thickness = 8.dp.toPx(),
                    )
                }
                Box(
                    Modifier.size(78.dp).background(Shu, CircleShape),
                    contentAlignment = Alignment.Center,
                ) { Text("旅", color = Paper, fontSize = 36.sp, fontWeight = FontWeight.Bold) }
            }
            Column(Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 16.dp)) {
                Text(trip.name, style = MaterialTheme.typography.titleMedium, color = Nezu)
                Text(big, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Sumi)
                Text(small, style = MaterialTheme.typography.bodyMedium, color = Sumi)
                if (start != null && end != null) {
                    Text(
                        "${fmtDate(trip.start)} → ${fmtDate(trip.end)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Nezu,
                    )
                }
            }
        }

        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Presupuesto
            WashiCard(onClick = { onGo(4) }) {
                SectionTitle("円", "Presupuesto")
                Spacer(Modifier.height(12.dp))
                if (trip.budget > 0) {
                    BudgetMeter(trip.spentBase(), trip.budget, trip.baseCurrency)
                } else {
                    Text("Definí cuánto querés gastar en total y la barra se irá llenando con cada gasto.", color = Nezu)
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = onSettings) { Text("Definir presupuesto") }
                }
                if (trip.yenWithoutRate()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Cargaste montos en yenes pero falta la cotización (1 ¥ = ? ${trip.baseCurrency}). Configurala en Ajustes.",
                        color = Shu,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            // Desglose
            if (trip.expenses.isNotEmpty() || trip.bookingsBase() > 0) {
                WashiCard {
                    SectionTitle("計", "Resumen")
                    Spacer(Modifier.height(8.dp))
                    SummaryRow("Gastos registrados", baseMoney(trip.expensesBase(), trip.baseCurrency))
                    SummaryRow(
                        if (trip.countBookings) "Vuelos y estadía (incluidos)" else "Vuelos y estadía (no incluidos)",
                        baseMoney(trip.bookingsBase(), trip.baseCurrency),
                    )
                    val byCat = trip.expenses
                        .groupBy { it.category }
                        .mapValues { (_, l) -> l.sumOf { trip.toBase(it.amount, it.cur) } }
                        .toList()
                        .sortedByDescending { it.second }
                    if (byCat.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        byCat.forEach { (key, total) ->
                            val c = categoryOf(key)
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Hanko(c.kanji, size = 24.dp, color = Ai)
                                Spacer(Modifier.width(10.dp))
                                Text(c.label, Modifier.weight(1f))
                                Text(baseMoney(total, trip.baseCurrency), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Contadores
            val nights = trip.stays.sumOf {
                val a = parseDate(it.checkIn)
                val b = parseDate(it.checkOut)
                if (a != null && b != null) maxOf(0L, ChronoUnit.DAYS.between(a, b)) else 0L
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Counter(Modifier.weight(1f), "便", trip.flights.size.toString(), "vuelos") { onGo(1) }
                Counter(Modifier.weight(1f), "宿", nights.toString(), "noches") { onGo(2) }
                Counter(Modifier.weight(1f), "日", trip.places.size.toString(), "lugares") { onGo(3) }
                Counter(Modifier.weight(1f), "円", trip.expenses.size.toString(), "gastos") { onGo(4) }
            }

            // Próximo vuelo
            val next = trip.flights
                .filter { (parseDate(it.depDate) ?: LocalDate.MIN) >= today }
                .minByOrNull { it.depDate + it.depTime }
            if (next != null) {
                WashiCard(onClick = { onGo(1) }) {
                    SectionTitle("便", "Próximo vuelo")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${next.from.ifBlank { "?" }}  →  ${next.to.ifBlank { "?" }}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "${fmtDate(next.depDate)}${if (next.depTime.isNotBlank()) " · ${next.depTime}" else ""}",
                        color = Nezu,
                    )
                }
            }
        }
    }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(label, Modifier.weight(1f), color = Nezu)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Counter(modifier: Modifier, kanji: String, value: String, label: String, onClick: () -> Unit) {
    WashiCard(modifier, onClick = onClick, pad = 10.dp) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(kanji, color = Shu, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = Nezu)
        }
    }
}
