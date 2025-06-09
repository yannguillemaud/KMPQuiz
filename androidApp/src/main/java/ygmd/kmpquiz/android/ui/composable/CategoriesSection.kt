package ygmd.kmpquiz.android.ui.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoriesSection(
    categories: List<String>,
    selectedCategory: String? = null,
    onCategorySelected: (String) -> Unit
) {
    Column {
        Text(
            text = "Catégories",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F2937)
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryChip(
                    category = category,
                    isSelected = category == selectedCategory,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF0F0FF) else Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF4F46E5) else Color(0xFFE5E7EB)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = category,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected) Color(0xFF4F46E5) else Color(0xFF374151)
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}