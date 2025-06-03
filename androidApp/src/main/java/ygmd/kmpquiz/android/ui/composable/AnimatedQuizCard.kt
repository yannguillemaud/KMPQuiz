package ygmd.kmpquiz.android.ui.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.pojo.contentKey


@Composable
fun AnimatedQuizCard(
    qanda: InternalQanda,
    index: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onFavoriteToggle: () -> Unit,
    isGridView: Boolean = false
) {
    val animationDelay = (index * 50).coerceAtMost(300)
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(qanda.contentKey()) { // CORRIGÉ : Utiliser contentKey comme clé
        kotlinx.coroutines.delay(animationDelay.toLong())
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    ) {
//        if (isGridView) {
//            // CORRIGÉ : Utiliser les composables corrects
//            SavedQandaCard(
//                qanda = qanda,
//                onClick = onClick,
//                onDelete = onDelete,
//                onFavoriteToggle = onFavoriteToggle
//            )
//        } else {
//            QuizCardList(
//                qanda = qanda,
//                onClick = onClick,
//                onDelete = onDelete,
//                onFavoriteToggle = onFavoriteToggle
//            )
//        }
    }
}