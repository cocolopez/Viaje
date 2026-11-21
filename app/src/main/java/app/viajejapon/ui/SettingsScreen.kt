package app.viajejapon.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.viajejapon.data.Rates
import app.viajejapon.data.Trip
import app.viajejapon.data.Traveler
import app.viajejapon.data.fmtDate
import app.viajejapon.data.numText
import app.viajejapon.data.parseNum
import app.viajejapon.data.toJson
import app.viajejapon.data.tripFromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDate

@Composable
fun SettingsScreen(trip: Trip, onSave: (Trip) -> Unit, onImport: (Trip) -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf(trip.name) }
    var start by remember { mutableStateOf(trip.start) }
    var end by remember { mutableStateOf(trip.end) }
    var base by remember { mutableStateOf(trip.baseCurrency) }
    var budget by remember { mutableStateOf(numText(trip.budget)) }
    var rate by remember { mutableStateOf(numText(trip.rate)) }
    var rateDate by remember { mutableStateOf(trip.rateDate) }
    var rateStatus by remember { mutableStateOf("") }
    var count by remember { mutableStateOf(trip.countBookings) }
    val travelers = remember { mutableStateListOf<Traveler>().apply { addAll(trip.travelers) } }

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var backupStatus by remember { mutableStateOf("") }
    var pendingImport by remember { mutableStateOf<Trip?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        if (uri != null) {
            val ok = runCatching {
                ctx.contentResolver.openOutputStream(uri, "wt")!!.use {
                    it.write(trip.toJson().toString(2).toByteArray(Charsets.UTF_8))
                }
            }.isSuccess
            backupStatus = if (ok) "Copia guardada." else "No se pudo guardar la copia."
        }
    }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            runCatching {
                val text = ctx.contentResolver.openInputStream(uri)!!.use { it.readBytes().toString(Charsets.UTF_8) }
                val o = JSONObject(text)
                require(o.has("travelers"))
                tripFromJson(o)
            }.onSuccess { pendingImport = it }
                .onFailure { backupStatus = "El archivo no parece una copia válida." }
        }
    }

    val htmlLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/html"),
    ) { uri ->
        if (uri != null) {
            val ok = runCatching {
                val tpl = ctx.assets.open("viewer.html").bufferedReader(Charsets.UTF_8).use { it.readText() }
                val html = tpl
                    .replace("__TRIP_JSON__", trip.toJson().toString().replace("</", "<\\/"))
                    .replace("__EXPORT_DATE__", LocalDate.now().toString())
                ctx.contentResolver.openOutputStream(uri, "wt")!!.use { it.write(html.toByteArray(Charsets.UTF_8)) }
            }.isSuccess
            backupStatus = if (ok) "Página HTML guardada. Se abre en cualquier navegador." else "No se pudo guardar el HTML."
        }
    }

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
                JpField("Cotización: 1 ¥ = ? $base", rate, { rate = it; rateDate = "" }, keyboard = KeyboardType.Decimal)
                Text(
                    "Ejemplo: si 1 ¥ equivale a 9,5 ${base.ifBlank { "ARS" }}, escribí 9,5. Se usa para sumar los gastos en yenes al presupuesto.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Nezu,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = {
                    val code = base.trim().uppercase()
                    rateStatus = "Consultando…"
                    scope.launch {
                        val r = withContext(Dispatchers.IO) { runCatching { Rates.yenTo(code) } }
                        r.onSuccess {
                            rate = Rates.fmt(it)
                            rateDate = LocalDate.now().toString()
                            rateStatus = "1 ¥ = ${Rates.fmt(it)} $code"
                        }.onFailure {
                            rateStatus = "No se pudo obtener la cotización. Revisá la conexión y el código de moneda."
                        }
                    }
                }) { Text("Actualizar cotización desde internet") }
                if (rateStatus.isNotBlank()) {
                    Text(rateStatus, style = MaterialTheme.typography.bodySmall, color = Ai, modifier = Modifier.padding(top = 4.dp))
                }
                if (rateDate.isNotBlank()) {
                    Text(
                        "Cotización bajada el ${fmtDate(rateDate)} (se actualiza una vez por día).",
                        style = MaterialTheme.typography.bodySmall,
                        color = Nezu,
                    )
                }
                Text(
                    "Datos de ExchangeRate-API · exchangerate-api.com. Es la cotización de referencia, no la del mercado paralelo: podés corregirla a mano.",
                    style = MaterialTheme.typography.labelSmall,
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

            WashiCard {
                SectionTitle("守", "Copia de seguridad")
                Spacer(Modifier.height(4.dp))
                Text(
                    "Guardá todos tus datos en un archivo (.json) para tenerlos a salvo o pasarlos a otro teléfono.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Nezu,
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { exportLauncher.launch("viaje-japon-backup.json") }) { Text("Exportar") }
                    OutlinedButton(onClick = { importLauncher.launch(arrayOf("*/*")) }) { Text("Importar") }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "También podés generar una página web de solo lectura con todo el viaje (pestañas, itinerario y presupuesto) para compartirla: nadie puede modificarla.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Nezu,
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { htmlLauncher.launch("viajapon.html") }) { Text("Exportar HTML (solo lectura)") }
                if (backupStatus.isNotBlank()) {
                    Text(backupStatus, style = MaterialTheme.typography.bodySmall, color = Ai, modifier = Modifier.padding(top = 6.dp))
                }
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
                        rateDate = rateDate,
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

    pendingImport?.let { imported ->
        AlertDialog(
            onDismissRequest = { pendingImport = null },
            title = { Text("¿Importar esta copia?") },
            text = {
                Text(
                    "Se reemplazarán todos los datos actuales por los de la copia: " +
                        "${imported.flights.size} vuelos, ${imported.stays.size} alojamientos, " +
                        "${imported.places.size} lugares y ${imported.expenses.size} gastos.",
                )
            },
            confirmButton = {
                TextButton(onClick = { pendingImport = null; onImport(imported) }) { Text("Importar", color = Shu) }
            },
            dismissButton = { TextButton(onClick = { pendingImport = null }) { Text("Cancelar") } },
        )
    }
}
