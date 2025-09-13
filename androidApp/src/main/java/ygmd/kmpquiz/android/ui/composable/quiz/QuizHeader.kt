package ygmd.kmpquiz.android.ui.composable.quiz

//@Composable
//fun QuizHeader(
//    currentQuestion: Int,
//    totalQuestions: Int,
//    category: String,
//    onNavigateBack: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.White)
//            .padding(16.dp)
//    ) {
//        // Top bar
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            IconButton(onClick = onNavigateBack) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                    contentDescription = "Retour",
//                    tint = Color(0xFF1F2937)
//                )
//            }
//
//            Text(
//                text = "$currentQuestion / $totalQuestions",
//                style = TextStyle(
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    color = Color(0xFF4F46E5)
//                )
//            )
//        }
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        // Progress bar
//        LinearProgressIndicator(
//            progress = { currentQuestion.toFloat() / totalQuestions },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(4.dp),
//            color = Color(0xFF4F46E5),
//            trackColor = Color(0xFFE5E7EB),
//        )
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        // Catégorie
//        Text(
//            text = category,
//            style = TextStyle(
//                fontSize = 14.sp,
//                color = Color(0xFF6B7280)
//            )
//        )
//    }
//}