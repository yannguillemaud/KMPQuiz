package ygmd.kmpquiz.android.ui.composable.notificationsetting

import ygmd.kmpquiz.domain.entities.cron.CronExpression

fun CronExpression.asText(): String = when (toString()) {
    "0 0 * * *" -> "Tous les jours"
    "0 0 * * 0" -> "Toutes les semaines"
    "0 0 1 * *" -> "Tous les mois"
    "0 0 1 1 *" -> "Tous les ans"
    "0 * * * *" -> "Toutes les heures"
    else -> "Personnalisé"
}