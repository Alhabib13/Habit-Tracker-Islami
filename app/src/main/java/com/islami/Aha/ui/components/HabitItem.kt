package com.islami.Aha.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.islami.Aha.data.model.Habit

@Composable
fun HabitItem(
    habit: Habit,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Background Bulat/Kotak Tumpul
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0F7FF), // Biru muda halus
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = habit.icon, style = MaterialTheme.typography.headlineSmall)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Nama & Deskripsi
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = habit.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Checkbox Custom Hijau
            Checkbox(
                checked = habit.isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF2E7D32), // Hijau sesuai Phase 1
                    uncheckedColor = Color.LightGray
                )
            )
        }
    }
}

// Preview khusus untuk melihat desain per item
@Preview(showBackground = true)
@Composable
fun HabitItemPreview() {
    HabitItem(
        habit = Habit(1, "Drink Water", "💧", "Stay hydrated all day", true),
        onCheckedChange = {}
    )
}