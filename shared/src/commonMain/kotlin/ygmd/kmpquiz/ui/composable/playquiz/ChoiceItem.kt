package ygmd.kmpquiz.ui.composable.playquiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ygmd.kmpquiz.domain.model.qanda.Choice
import ygmd.kmpquiz.ui.theme.Dimens.ChoiceVerticalPadding
import ygmd.kmpquiz.ui.theme.Dimens.DefaultPadding
import ygmd.kmpquiz.ui.theme.ExtendedTheme

@Composable
fun ChoiceItem(
    modifier: Modifier = Modifier,
    choice: Choice,
    isCorrect: Boolean?, // null = pas encore validé, true = bonne réponse, false = mauvaise réponse
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = when (isCorrect) {
        true -> ExtendedTheme.colors.success   // bonne réponse = vert
        false -> MaterialTheme.colorScheme.error // mauvaise réponse = rouge
        else -> MaterialTheme.colorScheme.surface // neutre
    }

    val animatedBorder by animateColorAsState(
        targetValue = borderColor,
        label = "borderColorAnim"
    )

    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ChoiceVerticalPadding),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(2.dp, animatedBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(DefaultPadding)
                .animateContentSize()
        ) {
            when (choice) {
                is Choice.ImageChoice -> {
                    AsyncImage(
                        model = choice.imageUrl,
                        contentDescription = "Choix image : ${choice.imageUrl}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(MaterialTheme.shapes.small),
                        contentScale = ContentScale.Crop
                    )
                }

                is Choice.TextChoice -> {
                    Text(
                        text = choice.text,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
