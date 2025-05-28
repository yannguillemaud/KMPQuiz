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
import ygmd.kmpquiz.android.ui.views.DisplayFetchQanda
import ygmd.kmpquiz.android.ui.views.DisplaySavedQandas
import ygmd.kmpquiz.android.ui.views.LegacyDisplayFetchQanda

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
                            Button(onClick = { navController.navigate(FetchByCategory) }){
                                Text("Fetch & Answer")
                            }
                            Button(onClick = { navController.navigate(FetchSaved) }){
                                Text("Fetch And Save By Category")
                            }
                            Button(onClick = { navController.navigate(FetchAndAnswerScreen) }){
                                Text("Legacy")
                            }
                        }
                    }
                    composable<FetchAndAnswerScreen> {
                        LegacyDisplayFetchQanda()
                    }
                    composable<FetchSaved> {
                        DisplaySavedQandas()
                    }
                    composable<FetchByCategory> {
                        DisplayFetchQanda()
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
object FetchSaved

@Serializable
object FetchByCategory