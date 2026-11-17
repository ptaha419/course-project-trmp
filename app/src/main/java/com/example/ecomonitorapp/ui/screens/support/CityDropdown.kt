package com.example.ecomonitorapp.ui.screens.support

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ecomonitorapp.models.City


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    cities: List<City>,
    modifier: Modifier = Modifier,
    label: String = "Город",
    isError: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val filtered = remember(value, cities) {
        val query = value.trim()
        if (query.isEmpty()) cities
        else cities.filter { it.name.contains(query, ignoreCase = true) }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = true
            },
            label = { Text(label) },
            isError = isError,
            supportingText = supportingText,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable) // в M3 < 1.3: .menuAnchor()
                .fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = expanded && filtered.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 320.dp),
        ) {
            filtered.take(50).forEach { city ->
                DropdownMenuItem(
                    text = {
                        Text(city.name + (city.subject?.let { " ($it)" } ?: ""))
                    },
                    onClick = {
                        onValueChange(city.name)
                        expanded = false
                    },
                )
            }
            if (filtered.size > 50) {
                DropdownMenuItem(
                    text = { Text("Уточните запрос, показаны первые 50") },
                    onClick = {},
                )
            }
        }
    }
}