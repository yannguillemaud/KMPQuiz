package ygmd.kmpquiz.android.ui.composable.quiz

//@Composable
//fun NextQuestionButton(
//    modifier: Modifier = Modifier,
//    isQuizComplete: Boolean,
//    onClick: () -> Unit
//) {
//    val buttonText = if (isQuizComplete) "Show results" else "Next question"
//    val buttonIcon = if (isQuizComplete) Icons.Filled.DoneAll else Icons.AutoMirrored.Filled.ArrowForward
//
//    FilledTonalButton(
//        onClick = onClick,
//        modifier = modifier,
//        colors = ButtonDefaults.filledTonalButtonColors(
//            containerColor = MaterialTheme.colorScheme.secondaryContainer,
//            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
//        ),
//        shape = RoundedCornerShape(16.dp),
//        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
//    ) {
//        AnimatedContent(
//            targetState = Pair(buttonText, buttonIcon),
//            transitionSpec = {
//                (slideInHorizontally(
//                    animationSpec = tween(300),
//                    initialOffsetX = { width -> width / 2 }
//                ) + fadeIn(animationSpec = tween(300)))
//                    .togetherWith(
//                        slideOutHorizontally(
//                            animationSpec = tween(300),
//                            targetOffsetX = { width -> -width / 2 }
//                        ) + fadeOut(animationSpec = tween(300))
//                    )
//            },
//            label = "NextButtonAnimation"
//        ) { (text, icon) ->
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Text(
//                    text = text,
//                    style = MaterialTheme.typography.labelLarge
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Icon(
//                    imageVector = icon,
//                    contentDescription = null,
//                    modifier = Modifier
//                )
//            }
//        }
//    }
//}
//
//@Preview
//@Composable
//fun NextQuestionButtonPreview() {
//    NextQuestionButton(isQuizComplete = true, onClick = {})
//}