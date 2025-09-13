package ygmd.kmpquiz.android.ui.composable.quiz

//@Composable
//fun QuestionDetailCard(
//    questionIndex: Int,
//    qanda: Qanda,
//    userAnswer: Choice?,
//    animationDelay: Int = 0
//) {
//    var isVisible by remember { mutableStateOf(false) }
//    val isCorrect = userAnswer == qanda.correctAnswer
//
//    LaunchedEffect(Unit) {
//        kotlinx.coroutines.delay(animationDelay.toLong())
//        isVisible = true
//    }
//
//    AnimatedVisibility(
//        visible = isVisible,
//        enter = slideInVertically(
//            initialOffsetY = { it / 3 },
//            animationSpec = spring(
//                dampingRatio = Spring.DampingRatioMediumBouncy
//            )
//        ) + fadeIn()
//    ) {
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            colors = CardDefaults.cardColors(containerColor = Color.White),
//            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//            shape = RoundedCornerShape(16.dp),
//            border = BorderStroke(
//                width = 1.dp,
//                color = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444)
//            )
//        ) {
//            Column(
//                modifier = Modifier.padding(20.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                // Header avec numéro et statut
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "Question ${questionIndex + 1}",
//                        style = TextStyle(
//                            fontSize = 14.sp,
//                            fontWeight = FontWeight.SemiBold,
//                            color = Color(0xFF6B7280)
//                        )
//                    )
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(4.dp)
//                    ) {
//                        Icon(
//                            imageVector = if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
//                            contentDescription = if (isCorrect) "Correct" else "Incorrect",
//                            tint = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444),
//                            modifier = Modifier.size(16.dp)
//                        )
//                        Text(
//                            text = if (isCorrect) "Correct" else "Incorrect",
//                            style = TextStyle(
//                                fontSize = 12.sp,
//                                fontWeight = FontWeight.Medium,
//                                color = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444)
//                            )
//                        )
//                    }
//                }
//
//                // Question
//                QuestionCard(qanda.question)
//
//                // Réponse utilisateur
//                if (userAnswer != null) {
//                    Card(
//                        colors = CardDefaults.cardColors(
//                            containerColor = if (isCorrect) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
//                        ),
//                        shape = RoundedCornerShape(8.dp)
//                    ) {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(12.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                imageVector = Icons.Filled.Person,
//                                contentDescription = "Votre réponse",
//                                tint = Color(0xFF6B7280),
//                                modifier = Modifier.size(16.dp)
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text(
//                                text = "Votre réponse : $userAnswer",
//                                style = TextStyle(
//                                    fontSize = 14.sp,
//                                    color = if (isCorrect) Color(0xFF047857) else Color(0xFFB91C1C)
//                                )
//                            )
//                        }
//                    }
//                }
//
//                // Bonne réponse (si incorrect)
//                if (!isCorrect) {
//                    Card(
//                        colors = CardDefaults.cardColors(
//                            containerColor = Color(0xFFF0FDF4)
//                        ),
//                        shape = RoundedCornerShape(8.dp)
//                    ) {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(12.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                imageVector = Icons.Filled.Lightbulb,
//                                contentDescription = "Bonne réponse",
//                                tint = Color(0xFF10B981),
//                                modifier = Modifier.size(16.dp)
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text(
//                                text = "Bonne réponse : ${qanda.correctAnswer}",
//                                style = TextStyle(
//                                    fontSize = 14.sp,
//                                    fontWeight = FontWeight.Medium,
//                                    color = Color(0xFF047857)
//                                )
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}