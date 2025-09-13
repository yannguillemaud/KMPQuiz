package ygmd.kmpquiz.android.ui.composable.quiz

//@Composable
//fun QuizCompletedSection(
//    state: QuizSessionUiState.Completed,
//    onNavigateBack: () -> Unit
//) {
//    val percentage = (state.results.score * 100f / state.results.questions).toInt()
//    var showDetails by remember { mutableStateOf(false) }
//
//    val scoreColor = when {
//        percentage >= 80 -> Color(0xFF10B981) // Vert - Excellent
//        percentage >= 60 -> Color(0xFFF59E0B) // Orange - Bien
//        else -> Color(0xFFEF4444) // Rouge - À améliorer
//    }
//
//    val encouragementMessage = when {
//        percentage >= 80 -> "Excellent travail ! 🎉"
//        percentage >= 60 -> "Bien joué ! 👍"
//        else -> "Continue tes efforts ! 💪"
//    }
//
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF8F9FA))
//            .statusBarsPadding(),
//        contentPadding = PaddingValues(24.dp),
//        verticalArrangement = Arrangement.spacedBy(20.dp)
//    ) {
//        item {
//            // Header de célébration
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                colors = CardDefaults.cardColors(containerColor = Color.White),
//                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//                shape = RoundedCornerShape(20.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(32.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    // Score principal avec animation
//                    Text(
//                        text = "${state.results.score}/${state.results.questions}",
//                        style = TextStyle(
//                            fontSize = 48.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = scoreColor
//                        )
//                    )
//
//                    Text(
//                        text = "$percentage%",
//                        style = TextStyle(
//                            fontSize = 24.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = scoreColor
//                        )
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    Text(
//                        text = encouragementMessage,
//                        style = TextStyle(
//                            fontSize = 18.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = Color(0xFF1F2937)
//                        )
//                    )
//
//                    Spacer(modifier = Modifier.height(20.dp))
//
//                    // Progress bar circulaire visuelle
//                    Box(
//                        modifier = Modifier.size(120.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator(
//                            progress = { percentage / 100f },
//                            modifier = Modifier.fillMaxSize(),
//                            color = scoreColor,
//                            strokeWidth = 8.dp,
//                            trackColor = Color(0xFFE5E7EB)
//                        )
//                        Text(
//                            text = "$percentage%",
//                            style = TextStyle(
//                                fontSize = 20.sp,
//                                fontWeight = FontWeight.Bold,
//                                color = scoreColor
//                            )
//                        )
//                    }
//                }
//            }
//        }
//
//        item {
//            // Toggle pour voir les détails
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { showDetails = !showDetails },
//                colors = CardDefaults.cardColors(containerColor = Color.White),
//                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//                shape = RoundedCornerShape(16.dp)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(20.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "Voir le détail des réponses",
//                        style = TextStyle(
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = Color(0xFF1F2937)
//                        )
//                    )
//
//                    Icon(
//                        imageVector = if (showDetails) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
//                        contentDescription = if (showDetails) "Masquer" else "Afficher",
//                        tint = Color(0xFF6B7280)
//                    )
//                }
//            }
//        }
//
//        // Détails des questions (avec animation)
//        if (showDetails) {
//            items(state.session.qandas.indices.toList()) { index ->
//                QuestionDetailCard(
//                    questionIndex = index,
//                    qanda = state.session.qandas[index],
//                    userAnswer = state.session.userAnswers[index],
//                    animationDelay = index * 50 // Animation séquentielle
//                )
//            }
//        }
//
//        item {
//            Spacer(modifier = Modifier.height(20.dp))
//
//            // Boutons d'action
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                // Bouton secondaire - Retour
//                OutlinedButton(
//                    onClick = onNavigateBack,
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(48.dp),
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        contentColor = Color(0xFF4F46E5)
//                    ),
//                    border = BorderStroke(1.dp, Color(0xFF4F46E5)),
//                    shape = RoundedCornerShape(12.dp)
//                ) {
//                    Text(
//                        "Retour",
//                        style = TextStyle(
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Medium
//                        )
//                    )
//                }
//
//                // Bouton principal - Nouveau quiz (pour plus tard)
//                Button(
//                    onClick = { /* TODO: Nouveau quiz aléatoire */ },
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(48.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF4F46E5)
//                    ),
//                    shape = RoundedCornerShape(12.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.Refresh,
//                        contentDescription = null,
//                        modifier = Modifier.size(18.dp)
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        "Nouveau quiz",
//                        style = TextStyle(
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Medium
//                        )
//                    )
//                }
//            }
//        }
//    }
//}