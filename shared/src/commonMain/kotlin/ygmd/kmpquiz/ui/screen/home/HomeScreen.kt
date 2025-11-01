package ygmd.kmpquiz.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("KMP Quiz", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                },
            )
        }
    ) { paddingValues ->
        rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -80 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -80 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = "https://wojakland.com/wp-content/grand-media/image/jester_wojak.png",
                        contentDescription = "brainlet",
                        modifier = Modifier
                            .sizeIn(maxWidth = 280.dp, maxHeight = 140.dp)
                            .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Parcequ'on n'a pas de mémoire",
                        fontSize = 12.sp,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    "Fonctionnalités",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )

                FeatureItem(
                    title = "Téléchargement des Qandas",
                    description = "L'application fetch et télécharge automatiquement les nouveaux QCM depuis l'API.",
                    icon = { Icon(Icons.Outlined.AddShoppingCart, contentDescription = null, tint = Color(0xFF3949AB), modifier = Modifier.size(36.dp)) }
                )

                FeatureItem(
                    title = "Qandas sauvegardés par catégories",
                    description = "Consulte facilement tes Qandas stockés localement, triés par catégories.",
                    icon = { Icon(Icons.Outlined.DownloadDone, contentDescription = null, tint = Color(0xFF3949AB), modifier = Modifier.size(36.dp)) }
                )

                FeatureItem(
                    title = "Créer un quiz programmé",
                    description = "Planifie tes sessions avec un système de quiz automatiques à horaires définis.",
                    icon = { Icon(Icons.Outlined.Quiz, contentDescription = null, tint = Color(0xFF3949AB), modifier = Modifier.size(36.dp)) }
                )
            }
        }
    }
}

@Composable
fun FeatureItem(
    title: String,
    description: String,
    icon: @Composable (() -> Unit)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF303F9F)
            )
            Text(description, fontSize = 12.sp, color = Color(0xFF607D8B))
        }
    }
}