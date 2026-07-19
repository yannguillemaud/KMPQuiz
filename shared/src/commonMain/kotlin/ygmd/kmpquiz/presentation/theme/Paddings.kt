package ygmd.kmpquiz.presentation.theme

import androidx.compose.ui.unit.dp

object Dimens {
    val PaddingSmall = 8.dp
    val PaddingMediumSmall = 12.dp
    val PaddingMedium = 16.dp
    val PaddingLarge = 24.dp
    val PaddingExtraLarge = 32.dp
    val ChoiceVerticalPadding = 4.dp

    val CardElevation = 2.dp
    val CategoryChipHeight = 36.dp

    /** Small status/stat dot indicator (hero stat accents, source status dots). */
    val DotIndicatorSize = 8.dp

    /** Multi-select badge (check / empty ring) overlaid on a category item's corner. */
    val SelectionBadgeSize = 24.dp

    /** Minimum height for full-width tappable list rows (Home quick actions, CTA). */
    val ListRowMinHeight = 56.dp

    /**
     * Fixed height of the always-composed bottom action shelf on the quiz play screen.
     * Reserves room for the "Next Question" button so the hero card and answers never
     * reflow when the button fades in after an answer is picked.
     */
    val QuizActionShelfHeight = 64.dp
    val CategoryAvatarSize = 40.dp
    val IconSize = 24.dp
    val SectionIconSize = 18.dp
    val EmptyStateIconSize = 80.dp
    val QandaThumbnailSize = 56.dp
    val ImageAnswerHeight = 100.dp
    val ScoreDonutSize = 160.dp
    val DonutStrokeWidth = 12.dp

    /**
     * Bottom padding reserved by scrollable content so the last item is not
     * hidden behind the floating navigation capsule.
     */
    val BottomNavPadding = 88.dp

    /**
     * Height of the floating navigation capsule (see [App]'s bottom bar).
     * Slightly trimmed from the M3 default (80.dp) so the detached capsule
     * reads lighter. The horizontal/bottom margins reuse [PaddingMedium] /
     * [PaddingSmall], so no dedicated margin token is required.
     */
    val FloatingNavBarHeight = 64.dp
}
