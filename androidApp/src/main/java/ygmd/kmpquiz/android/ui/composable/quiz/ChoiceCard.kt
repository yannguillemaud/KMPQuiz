package ygmd.kmpquiz.android.ui.composable.quiz

//@Composable
//fun ChoiceCard(
//    choice: Choice,
//    isSelected: Boolean,
//    isAnswered: Boolean,
//    isCorrect: Boolean?,
//    onClick: () -> Unit
//) {
//    // Logique des états visuels
//    val (backgroundColor, borderColor, textColor) = when {
//        isAnswered && isCorrect == true -> Triple(
//            Color(0xFFF0FDF4), // Vert très clair
//            Color(0xFF10B981), // Vert
//            Color(0xFF047857)  // Vert foncé
//        )
//
//        isAnswered && isCorrect == false && isSelected -> Triple(
//            Color(0xFFFEF2F2), // Rouge très clair
//            Color(0xFFEF4444), // Rouge
//            Color(0xFFB91C1C)  // Rouge foncé
//        )
//
//        isSelected && !isAnswered -> Triple(
//            Color(0xFFF0F0FF), // Bleu très clair
//            Color(0xFF4F46E5), // Bleu primaire
//            Color(0xFF3730A3)  // Bleu foncé
//        )
//
//        else -> Triple(
//            Color.White,
//            Color(0xFFE5E7EB), // Gris clair
//            Color(0xFF374151)  // Gris foncé
//        )
//    }
//
//    val animatedBorderColor by animateColorAsState(
//        targetValue = borderColor,
//        animationSpec = tween(200),
//        label = "border_color"
//    )
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(enabled = !isAnswered) { onClick() }, // Désactivé après réponse
//        colors = CardDefaults.cardColors(containerColor = backgroundColor),
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = if (isSelected) 4.dp else 2.dp
//        ),
//        shape = RoundedCornerShape(12.dp),
//        border = BorderStroke(
//            width = if (isSelected || isCorrect == true) 2.dp else 1.dp,
//            color = animatedBorderColor
//        )
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            when (choice) {
//                is Choice.TextChoice -> Text(
//                    text = choice.text,
//                    style = TextStyle(
//                        fontSize = 16.sp,
//                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
//                        color = textColor,
//                        lineHeight = 22.sp
//                    ),
//                    modifier = Modifier.weight(1f)
//                )
//
//                is Choice.ImageChoice -> {
//                    /* TODO */
//                }
//            }
//        }
//
//        when {
//            isAnswered && isCorrect == true -> Icon(
//                imageVector = Icons.Filled.CheckCircle,
//                contentDescription = "Correct",
//                tint = Color(0xFF10B981),
//                modifier = Modifier.size(24.dp)
//            )
//
//            isAnswered && isCorrect == false && isSelected -> Icon(
//                imageVector = Icons.Filled.Cancel,
//                contentDescription = "Incorrect",
//                tint = Color(0xFFEF4444),
//                modifier = Modifier.size(24.dp)
//            )
//
//            isSelected && !isAnswered -> Icon(
//                imageVector = Icons.Filled.RadioButtonChecked,
//                contentDescription = "Sélectionné",
//                tint = Color(0xFF4F46E5),
//                modifier = Modifier.size(20.dp)
//            )
//
//            else -> Icon(
//                imageVector = Icons.Filled.RadioButtonUnchecked,
//                contentDescription = "Non sélectionné",
//                tint = Color(0xFF9CA3AF),
//                modifier = Modifier.size(20.dp)
//            )
//        }
//    }
//}