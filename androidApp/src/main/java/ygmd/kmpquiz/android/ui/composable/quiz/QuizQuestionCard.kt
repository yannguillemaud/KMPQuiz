package ygmd.kmpquiz.android.ui.composable.quiz

//@Composable
//fun QuestionCard(question: Question) {
//    Card(
//        modifier = Modifier.padding(16.dp),
//    ) {
//        when (question) {
//            is Question.TextQuestion -> {
//                Text(
//                    text = question.text,
//                    style = MaterialTheme.typography.titleLarge
//                )
//            }
//
//            is Question.ImageQuestion -> {
//                if(!question.text.isNullOrBlank()){
//                    Text(
//                        text = question.text!!,
//                        style = MaterialTheme.typography.titleLarge
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                }
//
//                AsyncImage(
//                    model = question.imageUrl,
//                    contentDescription = question.text ?: "Image question",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(250.dp), // todo - adapt
//                    contentScale = ContentScale.Crop, // todo - adapt
//                    // todo - not compose friendly, fallback custom ?
////                    placeholder = painterResource(id = R.drawable.stat_sys_download),
////                    error = painterResource(id = R.drawable.ic_delete)
//                )
//            }
//        }
//    }
//}
//
//@Preview
//@Composable
//fun preview(){
//    QuestionCard(
//        Question.ImageQuestion(
//            imageUrl = "https://raw.githubusercontent.com/yannguillemaud/cs2-map-positions/main/inferno/logs.png"
//        )
//    )
//}