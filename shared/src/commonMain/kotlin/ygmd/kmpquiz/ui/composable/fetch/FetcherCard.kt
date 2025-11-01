package ygmd.kmpquiz.ui.composable.fetch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.ui.theme.Dimens
import ygmd.kmpquiz.ui.theme.Dimens.DefaultPadding

@Composable
fun FetcherCard(
    modifier: Modifier = Modifier,
    name: String,
    isUptodate: Boolean,
    isLoading: Boolean,
    onFetchInvoke: () -> Unit,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(Dimens.CardElevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DefaultPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column() {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    isUptodate -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "$name est à jour",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    else -> {
                        IconButton(onClick = onFetchInvoke){
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Télécharger $name"
                            )
                        }
                    }
                }
            }
        }
    }
}