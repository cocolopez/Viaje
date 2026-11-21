package app.viajejapon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.viajejapon.data.Trip
import app.viajejapon.data.Traveler
import app.viajejapon.data.numText
import app.viajejapon.data.parseNum

@Composable
fun SettingsScreen(trip: Trip, onSave: (Trip) -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf(trip.name) }
    var start by remember { mutableStateOf(trip.start) }
    var end by remember { mutableStateOf(trip.end) }
    var base by remember { mutableStateOf(trip.baseCurrency) }
    var budget by remember { mutableStateOf(numText(trip.budget)) }
    var rate by remember { mutableStateOf(numText(trip.rate)) }
    var count by remember { mutableStateOf(trip.countBookings) }
    val travelers = remember { mutableStateListOf<Traveler>().apply { addAll(trip.travelers) } }

    Column(
        Modifier
            .fillMaxSize()
            .background(Washi)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Volver", color = Ai) }
            Spacer(Modifier.width(8.dp))
            Hanko("設", size = 32.dp)
            Spacer(Modifier.width(10.dp))
            Text("Ajustes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            WashiCard {
                SectionTitle("旅", "Viaje")
                Spacer(Modifier.height(10.dp))
                JpField("Nombre del viaje", name, { name = it })
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DateButton("Inicio", start, { start = it }, Modifier.weight(1f))
                    DateButton("Fin", end, { end = it }, Modifier.weight(1f))
                }
            }

            WashiCard {
                SectionTitle("円", "Presupuesto")
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    JpField("Moneda base", base, { base = it.uppercase().take(4) }, Modifier.weight(1f))
                    JpField("Presupuesto total", budget, { budget = it }, Modifier.weight(2f), KeyboardType.Decimal)
                }
                Spacer(Modifier.height(8.dp))
                JpField("Cotización: 1 ¥ = ? $base", rate, { rate = it }, keyboard = KeyboardType.Decimal)
                Text(
                    "Ejemplo: si 1 ¥ equivale a 9,5 ${base.ifBlank { "ARS" }}, escribí 9,5. Se usa para sumar los gastos en yenes al presupuesto.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Nezu,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sumar vuelos y estadía al presupuesto", Modifier.weight(1f))
                    Switch(checked = count, onCheckedChange = { count = it })
                }
            }

            WashiCard {
                SectionTitle("人", "Viajeros")
                Spacer(Modifier.height(4.dp))
                Text("Se usan para dividir gastos.", style = MaterialTheme.typography.bodySmall, color = Nezu)
                Spacer(Modifier.height(8.dp))
                travelers.forEachIndexed { i, t ->
                    val used = trip.expenses.any { it.paidBy == t.id || t.id in it.split }
                    Row(Modifier.padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        JpField("Viajero ${i + 1}", t.name, { travelers[i] = travelers[i].copy(name = it) }, Modifier.weight(1f))
                        if (travelers.size > 1 && !used) {
                            TextButton(onClick = { travelers.removeAt(i) }) { Text("✕", color = Shu) }
                        }
                    }
                }
                OutlinedButton(onClick = { travelers.add(Traveler(name = "")) }) { Text("＋ Agregar viajero") }
            }
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                onSave(
                    trip.copy(
                        name = name.ifBlank { "Viaje a Japón" },
                        start = start,
                        end = end,
                        baseCurrency = base.ifBlank { "USD" },
                        budget = parseNum(budget),
                        rate = parseNum(rate),
                        countBookings = count,
                        travelers = travelers.mapIndexed { i, t ->
                            t.copy(name = t.name.ifBlank { "Viajero ${i + 1}" })
                        },
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) { Text("Guardar") }
    }
}
