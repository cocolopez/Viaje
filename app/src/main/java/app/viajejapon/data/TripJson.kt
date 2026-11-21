package app.viajejapon.data

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

private fun <T> JSONArray?.mapObjects(f: (JSONObject) -> T): List<T> {
    if (this == null) return emptyList()
    return (0 until length()).map { f(getJSONObject(it)) }
}

private fun JSONArray?.strings(): List<String> {
    if (this == null) return emptyList()
    return (0 until length()).map { getString(it) }
}

fun Trip.toJson(): JSONObject = JSONObject().apply {
    put("name", name)
    put("start", start)
    put("end", end)
    put("base", baseCurrency)
    put("budget", budget)
    put("rate", rate)
    put("countBookings", countBookings)
    put("travelers", JSONArray().also { a ->
        travelers.forEach { a.put(JSONObject().put("id", it.id).put("name", it.name)) }
    })
    put("flights", JSONArray().also { a ->
        flights.forEach { f ->
            a.put(JSONObject().apply {
                put("id", f.id); put("airline", f.airline); put("number", f.number)
                put("from", f.from); put("to", f.to)
                put("depDate", f.depDate); put("depTime", f.depTime)
                put("arrDate", f.arrDate); put("arrTime", f.arrTime)
                put("confirmation", f.confirmation)
                put("cost", f.cost); put("cur", f.cur); put("notes", f.notes)
            })
        }
    })
    put("stays", JSONArray().also { a ->
        stays.forEach { s ->
            a.put(JSONObject().apply {
                put("id", s.id); put("name", s.name); put("city", s.city)
                put("address", s.address)
                put("checkIn", s.checkIn); put("checkOut", s.checkOut)
                put("confirmation", s.confirmation)
                put("cost", s.cost); put("cur", s.cur); put("notes", s.notes)
            })
        }
    })
    put("places", JSONArray().also { a ->
        places.forEach { p ->
            a.put(JSONObject().apply {
                put("id", p.id); put("date", p.date); put("time", p.time)
                put("name", p.name); put("notes", p.notes)
                put("estYen", p.estYen); put("done", p.done)
            })
        }
    })
    put("expenses", JSONArray().also { a ->
        expenses.forEach { e ->
            a.put(JSONObject().apply {
                put("id", e.id); put("title", e.title); put("category", e.category)
                put("amount", e.amount); put("cur", e.cur); put("date", e.date)
                put("paidBy", e.paidBy)
                put("split", JSONArray(e.split))
                put("note", e.note)
            })
        }
    })
}

fun tripFromJson(o: JSONObject): Trip = Trip(
    name = o.optString("name", "Viaje a Japón"),
    start = o.optString("start", ""),
    end = o.optString("end", ""),
    baseCurrency = o.optString("base", "USD"),
    budget = o.optDouble("budget", 0.0),
    rate = o.optDouble("rate", 0.0),
    countBookings = o.optBoolean("countBookings", true),
    travelers = o.optJSONArray("travelers")
        .mapObjects { Traveler(it.getString("id"), it.optString("name", "")) }
        .ifEmpty { listOf(Traveler(name = "Yo")) },
    flights = o.optJSONArray("flights").mapObjects {
        Flight(
            id = it.getString("id"),
            airline = it.optString("airline", ""),
            number = it.optString("number", ""),
            from = it.optString("from", ""),
            to = it.optString("to", ""),
            depDate = it.optString("depDate", ""),
            depTime = it.optString("depTime", ""),
            arrDate = it.optString("arrDate", ""),
            arrTime = it.optString("arrTime", ""),
            confirmation = it.optString("confirmation", ""),
            cost = it.optDouble("cost", 0.0),
            cur = it.optString("cur", CUR_BASE),
            notes = it.optString("notes", ""),
        )
    },
    stays = o.optJSONArray("stays").mapObjects {
        Stay(
            id = it.getString("id"),
            name = it.optString("name", ""),
            city = it.optString("city", ""),
            address = it.optString("address", ""),
            checkIn = it.optString("checkIn", ""),
            checkOut = it.optString("checkOut", ""),
            confirmation = it.optString("confirmation", ""),
            cost = it.optDouble("cost", 0.0),
            cur = it.optString("cur", CUR_BASE),
            notes = it.optString("notes", ""),
        )
    },
    places = o.optJSONArray("places").mapObjects {
        Place(
            id = it.getString("id"),
            date = it.optString("date", ""),
            time = it.optString("time", ""),
            name = it.optString("name", ""),
            notes = it.optString("notes", ""),
            estYen = it.optDouble("estYen", 0.0),
            done = it.optBoolean("done", false),
        )
    },
    expenses = o.optJSONArray("expenses").mapObjects {
        Expense(
            id = it.getString("id"),
            title = it.optString("title", ""),
            category = it.optString("category", "otros"),
            amount = it.optDouble("amount", 0.0),
            cur = it.optString("cur", CUR_JPY),
            date = it.optString("date", ""),
            paidBy = it.optString("paidBy", ""),
            split = it.optJSONArray("split").strings(),
            note = it.optString("note", ""),
        )
    },
)

/** Guarda el viaje como un único archivo JSON en el almacenamiento privado de la app. */
class TripStore(private val file: File) {

    fun load(): Trip =
        runCatching { tripFromJson(JSONObject(file.readText())) }.getOrElse { Trip() }

    fun save(trip: Trip) {
        runCatching {
            val tmp = File(file.parentFile, file.name + ".tmp")
            tmp.writeText(trip.toJson().toString())
            if (!tmp.renameTo(file)) {
                file.writeText(trip.toJson().toString())
                tmp.delete()
            }
        }
    }
}
