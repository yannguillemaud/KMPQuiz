//@Composable
//fun MenuCard(
//    title: String,
//    icon: ImageVector,
//    primaryColor: Color,
//    onClick: () -> Unit,
//) {
//    var isPressed by remember { mutableStateOf(false) }
//    val scale by animateFloatAsState(
//        targetValue = if (isPressed) 0.95f else 1f,
//        animationSpec = tween(100),
//        label = "scale"
//    )
//
//    Card(
//        onClick = onClick,
//        modifier = Modifier
//            .fillMaxWidth()
//            .aspectRatio(1f)
//            .scale(scale)
//            .pointerInput(Unit){
//                detectTapGestures(
//                    onPress = {
//                        isPressed = true
//                        tryAwaitRelease()
//                        isPressed = false
//                    }
//                )
//            },
//        colors = CardDefaults.cardColors(containerColor = White),
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = 2.dp,
//            pressedElevation = 8.dp
//        ),
//        shape = RoundedCornerShape(20.dp)
//    ){
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(20.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(56.dp)
//                    .background(
//                        primaryColor.copy(alpha = 0.1f),
//                        CircleShape
//                    ),
//                contentAlignment = Alignment.Center
//            ){
//                Icon(
//                    imageVector = icon,
//                    contentDescription = null,
//                    modifier = Modifier.size(28.dp),
//                    tint = primaryColor
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = title,
//                style = MaterialTheme.typography.titleMedium.copy(
//                    fontWeight = FontWeight.SemiBold,
//                    color = Color(0xFF1F2937)
//                ),
//                textAlign = TextAlign.Center
//            )
//        }
//    }
//}