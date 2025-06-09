package ygmd.kmpquiz.android.ui.composable.footer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun Footer() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 32.dp),
            thickness = 1.dp,
            color = Color(0xFFE5E7EB)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        Color(0xFF4F46E5),
                        CircleShape
                    )
            )

            Text(
                text = "Version 1.0.0",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
            )
        }

        Text(
            text = "Développé avec ❤️ pour l'apprentissage",
            style = TextStyle(
                fontSize = 11.sp,
                color = Color(0xFFD1D5DB)
            ),
            textAlign = TextAlign.Center
        )
    }
}