package ygmd.kmpquiz.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import ygmd.kmpquiz.android.ui.QuizTheme
import ygmd.kmpquiz.android.ui.model.DisplayFetchQanda
import ygmd.kmpquiz.android.ui.model.DisplaySavedQandas

class Main: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizTheme(darkTheme = false)  {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Home){
                    composable<Home> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Button(onClick = { navController.navigate(FetchAndAnswerScreen) }){
                                Text("Fetch & Answer")
                            }
                            Button(onClick = { navController.navigate(FetchAndSaveByCategory) }){
                                Text("Fetch And Save By Category")
                            }

                        }
                    }
                    composable<FetchAndAnswerScreen> {
                        DisplayFetchQanda()
                    }
                    composable<FetchAndSaveByCategory> {
                        DisplaySavedQandas()
                    }
                }
            }
        }
    }
}

@Serializable
object Home

@Serializable
object FetchAndAnswerScreen

@Serializable
object FetchAndSaveByCategory