package app.viajejapon.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import app.viajejapon.data.Expense
import app.viajejapon.data.Flight
import app.viajejapon.data.Place
import app.viajejapon.data.Stay
import app.viajejapon.data.Trip
import app.viajejapon.data.TripStore
import app.viajejapon.data.upsert
import java.io.File

class TripViewModel(app: Application) : AndroidViewModel(app) {

    private val store = TripStore(File(app.filesDir, "trip.json"))

    var trip by mutableStateOf(store.load())
        private set

    fun update(f: (Trip) -> Trip) {
        trip = f(trip)
        store.save(trip)
    }

    fun saveFlight(f: Flight) = update { it.copy(flights = it.flights.upsert(f) { x -> x.id }) }
    fun deleteFlight(id: String) = update { it.copy(flights = it.flights.filterNot { x -> x.id == id }) }

    fun saveStay(s: Stay) = update { it.copy(stays = it.stays.upsert(s) { x -> x.id }) }
    fun deleteStay(id: String) = update { it.copy(stays = it.stays.filterNot { x -> x.id == id }) }

    fun savePlace(p: Place) = update { it.copy(places = it.places.upsert(p) { x -> x.id }) }
    fun deletePlace(id: String) = update { it.copy(places = it.places.filterNot { x -> x.id == id }) }
    fun togglePlace(p: Place) = savePlace(p.copy(done = !p.done))

    fun saveExpense(e: Expense) = update { it.copy(expenses = it.expenses.upsert(e) { x -> x.id }) }
    fun deleteExpense(id: String) = update { it.copy(expenses = it.expenses.filterNot { x -> x.id == id }) }
}
