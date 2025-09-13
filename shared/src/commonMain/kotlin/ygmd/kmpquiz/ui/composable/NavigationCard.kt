package ygmd.kmpquiz.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.ui.model.NavigationItem

@Composable
fun NavigationCard(
    item: NavigationItem,
    onClick: (NavigationItem) -> Unit,
    modifier: Modifier = Modifier
){
    Card(
        onClick = { onClick(item) },
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation()
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ){
            Column(
                modifier = modifier.weight(1f)
            ){
                Text(item.name)
            }
        }
    }
}