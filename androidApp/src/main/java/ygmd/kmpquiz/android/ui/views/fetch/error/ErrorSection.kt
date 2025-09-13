package ygmd.kmpquiz.android.ui.views.fetch.error

// todo
//@Composable
//fun FetchErrorSection(
//    error: UiError,
//    onRetry: () -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(32.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.spacedBy(20.dp)
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(80.dp)
//                    .background(
//                        Color(0xFFFEF2F2),
//                        CircleShape
//                    ),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Filled.Error,
//                    contentDescription = null,
//                    modifier = Modifier.size(40.dp),
//                    tint = Color(0xFFEF4444)
//                )
//            }
//
//            Text(
//                text = "Erreur de chargement",
//                style = TextStyle(
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    color = Color(0xFF1F2937)
//                ),
//                textAlign = TextAlign.Center
//            )
//
//            Text(
//                text = error.message,
//                style = TextStyle(
//                    fontSize = 16.sp,
//                    color = Color(0xFF6B7280)
//                ),
//                textAlign = TextAlign.Center
//            )
//
//            Button(
//                onClick = onRetry,
//                modifier = Modifier
//                    .fillMaxWidth(0.6f)
//                    .height(48.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFF4F46E5)
//                ),
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Filled.Refresh,
//                    contentDescription = null,
//                    modifier = Modifier.size(18.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    "Réessayer",
//                    style = TextStyle(
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Medium
//                    )
//                )
//            }
//        }
//    }
//}