package ygmd.kmpquiz.android.ui.views.home

//@Composable
//fun HomeScreen(
//    onNavigateToFetch: () -> Unit,
//    onNavigateToSaved: () -> Unit,
//    onNavigateToQuiz: () -> Unit,
//    onNavigateToNotifications: () -> Unit,
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF8F9FA))
//            .statusBarsPadding()
//            .padding(24.dp),
//        verticalArrangement = Arrangement.spacedBy(32.dp)
//    ) {
//        HomeHeader()
//
//        Column(
//            modifier = Modifier.fillMaxWidth(),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            // Première ligne
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                Box(modifier = Modifier.weight(1f)) {
//                    MenuCard(
//                        title = "Rechercher",
//                        icon = Icons.Filled.Search,
//                        primaryColor = Color(0xFF4F46E5),
//                        onClick = onNavigateToFetch
//                    )
//                }
//                Box(modifier = Modifier.weight(1f)) {
//                    MenuCard(
//                        title = "Sauvegardés",
//                        icon = Icons.Filled.BookmarkBorder,
//                        primaryColor = Color(0xFF059669),
//                        onClick = onNavigateToSaved
//                    )
//                }
//            }
//
//            // Deuxième ligne
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                Box(modifier = Modifier.weight(1f)) {
//                    MenuCard(
//                        title = "Quiz",
//                        icon = Icons.Filled.Quiz,
//                        primaryColor = Color(0xFFDC2626),
//                        onClick = onNavigateToQuiz
//                    )
//                }
//                Box(modifier = Modifier.weight(1f)) {
//                    MenuCard(
//                        title = "Notifications",
//                        icon = Icons.Filled.Notifications,
//                        primaryColor = Color(0xFFEA580C),
//                        onClick = onNavigateToNotifications
//                    )
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        FooterSection()
//    }
//}
//
//@Composable
//private fun HomeHeader() {
//    Column(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        Box(
//            modifier = Modifier
//                .size(80.dp)
//                .background(
//                    Color(0xFF4F46E5).copy(alpha = 0.1f),
//                    CircleShape
//                ),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                imageVector = Icons.Filled.Quiz,
//                contentDescription = null,
//                modifier = Modifier.size(40.dp),
//                tint = Color(0xFF4F46E5)
//            )
//        }
//
//        Text(
//            text = "Quiz Master",
//            style = MaterialTheme.typography.headlineMedium.copy(
//                fontWeight = FontWeight.Bold,
//                color = Color(0xFF1F2937)
//            ),
//            textAlign = TextAlign.Center
//        )
//
//        Text(
//            text = "Parce que j'ai aucune mémoire",
//            style = MaterialTheme.typography.bodyLarge.copy(
//                color = Color(0xFF6B7280)
//            ),
//            textAlign = TextAlign.Center
//        )
//    }
//}
//
//@Composable
//private fun FooterSection() {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = "Version 1.0.0",
//            style = MaterialTheme.typography.bodySmall.copy(
//                color = Color(0xFF9CA3AF)
//            )
//        )
//    }
//}