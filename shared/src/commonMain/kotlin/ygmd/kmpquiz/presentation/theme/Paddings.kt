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

    /** Thinner than the M3 default (4.dp) stroke, for small inline progress indicators (e.g. in-button spinners). */
    val ThinStrokeWidth = 2.dp

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

    /**
     * Floor for [ygmd.kmpquiz.presentation.composable.playquiz.QuizHeroQuestionCard]'s
     * height — promoted from a hardcoded `defaultMinSize` literal so the value has a
     * single source of truth. Applied via a plain (non-required) `heightIn(min = …, max =
     * [HeroCardMaxHeight])` on the card, with the card wrapped in
     * `Box(Modifier.weight(1f), contentAlignment = Alignment.Center)` at the call site
     * (`QuizStartedContent`) instead of putting `weight(1f)` on the card directly — that
     * `Box`'s default `propagateMinConstraints = false` stops it forwarding its own tight
     * incoming min height to the card, so this floor is a genuine *best-effort* target: it
     * raises the card to 200.dp when the weighted allocation is generous, but gracefully
     * coerces down to whatever space is actually available when the allocation is smaller.
     * It deliberately does **not** use `requiredHeightIn`, which ignores the incoming
     * constraint outright — on a short window that forced the card to a hard 200.dp,
     * overflowing past the `Column`'s own bounds and pushing `QuizAnswersSection` /
     * `QuizActionShelf` off-screen and unreachable (regression caught in review, 2026-07-29;
     * do not reintroduce `requiredHeightIn` here).
     */
    val HeroCardMinHeight = 200.dp

    /**
     * Ceiling for [ygmd.kmpquiz.presentation.composable.playquiz.QuizHeroQuestionCard]'s
     * height — paired with [HeroCardMinHeight] in the same `heightIn(min = …, max = …)`
     * call on the card itself (not the image inside it; the image just fills whatever
     * space the now-bounded card ends up with). Prevents the hero card from ballooning on
     * tall/maximized desktop windows. See [HeroCardMinHeight]'s KDoc for why the plain
     * (non-required) `heightIn` genuinely takes effect here despite the card living inside
     * a `weight(1f)` allocation.
     */
    val HeroCardMaxHeight = 480.dp

    /**
     * Max width for quiz session / review content (`QuizSessionScreen`,
     * `CategoryReviewScreen`), so wide viewports (desktop windows, tablets, unfolded
     * foldables) don't stretch question images and answer cards edge-to-edge.
     */
    val SessionContentMaxWidth = 720.dp
}
