# 旅 Viaje a Japón

App Android 100% nativa (Kotlin + Jetpack Compose + Material 3) para organizar un viaje a Japón.
Sin servidores ni cuentas: todo se guarda en el teléfono (`trip.json` en el almacenamiento privado de la app).

## Qué incluye
- **Inicio**: cuenta regresiva, medidor de presupuesto, resumen por categoría y próximo vuelo.
- **Vuelos**: ida/vuelta con aeropuertos, fechas y horas, código de reserva y costo.
- **Estadía**: alojamientos con check-in/out, noches, dirección, reserva y botón a Google Maps.
- **Itinerario**: un día por pestaña, lugares con hora, notas, costo estimado en ¥, check de "visitado" y botón a Google Maps.
- **Gastos**: monto en ¥ o moneda base, categorías, quién pagó y entre quiénes se divide; barra de presupuesto que se llena y saldos para saldar.
- **Ajustes**: fechas, moneda base, presupuesto total, cotización 1 ¥ = X, viajeros.

## Compilar
Subí el contenido de esta carpeta a la raíz de un repo de GitHub. El workflow `.github/workflows/build.yml`
compila el APK debug (Actions → *Compilar APK* → artifact `ViajeJapon-APK`).
Requisitos: JDK 17, Gradle 8.9, AGP 8.6.1, compileSdk 35, minSdk 26.
