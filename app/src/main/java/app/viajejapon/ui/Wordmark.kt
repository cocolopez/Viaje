package app.viajejapon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Logotipo "Viajapon": la "o" es un círculo rojo completo con el kanji 旅 (viaje). */
@Composable
fun Wordmark(size: TextUnit = 30.sp) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Viaja", color = Sumi, fontSize = size, fontWeight = FontWeight.Bold)
        Text("p", color = Ai, fontSize = size, fontWeight = FontWeight.Bold)
        Box(
            Modifier
                .padding(horizontal = 1.dp)
                .size((size.value * 0.9f).dp)
                .background(Shu, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("旅", color = Paper, fontSize = (size.value * 0.5f).sp, fontWeight = FontWeight.Bold)
        }
        Text("n", color = Ai, fontSize = size, fontWeight = FontWeight.Bold)
    }
}
