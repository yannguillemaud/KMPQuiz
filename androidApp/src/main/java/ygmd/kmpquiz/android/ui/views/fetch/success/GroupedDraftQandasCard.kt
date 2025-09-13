package ygmd.kmpquiz.android.ui.views.fetch.success

//@Composable
//fun GroupedDraftQandasCard(
//    identifier: String,
//    qandas: List<DraftQanda>,
//    onSaveAction: () -> Unit,
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 12.dp, vertical = 6.dp)
//            .border(
//                shape = RoundedCornerShape(12.dp),
//                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
//            )
//            .clickable(
//                onClick = onSaveAction
//            )
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Text(
//                text = identifier,
//                style = MaterialTheme.typography.titleMedium,
//                color = Color.Black
//            )
//            Text(
//                text = "${qandas.size} questions",
//                style = MaterialTheme.typography.bodyMedium,
//                color = Color.Gray,
//                modifier = Modifier.padding(top = 4.dp)
//            )
//        }
//    }
//}