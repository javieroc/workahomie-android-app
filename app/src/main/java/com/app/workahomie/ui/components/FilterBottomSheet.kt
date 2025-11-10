package com.app.workahomie.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.workahomie.constants.FACILITIES
import com.app.workahomie.constants.OCCUPATIONS
import com.app.workahomie.data.HostFilters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheet(
    filters: HostFilters,
    onApply: (HostFilters) -> Unit,
    onClose: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var localFilters by remember  { mutableStateOf(filters) }

    ModalBottomSheet(
        onDismissRequest = { onClose() },
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.headlineSmall
                )

                TextButton(
                    onClick = {
                        val cleared = HostFilters()
                        onApply(cleared)
                        onClose()
                    }
                ) {
                    Text("Clear")
                }
            }

            OccupationsSection(
                selected = localFilters.occupations,
                onChange = { updated ->
                    localFilters = localFilters.copy(occupations = updated)
                }
            )

            Spacer(Modifier.height(20.dp))

            FacilitiesSection(
                selected = localFilters.facilities,
                onChange = { updated ->
                    localFilters = localFilters.copy(facilities = updated)
                }
            )

            Spacer(Modifier.height(20.dp))

            RateSliderSection(
                value = localFilters.rate ?: 0.0,
                onChange = { newValue ->
                    localFilters = localFilters.copy(rate = newValue)
                }
            )

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = { onApply(localFilters) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply Filters")
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun FacilitiesSection(
    selected: List<String>,
    onChange: (List<String>) -> Unit
) {
    Column {
        Text("Facilities", style = MaterialTheme.typography.titleMedium)

        FACILITIES.forEach { facility ->
            val id = facility.lowercase()
            val isChecked = selected.contains(id)

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        val updated = if (isChecked)
                            selected - id
                        else
                            selected + id

                        onChange(updated)
                    }
                )
                Text(
                    text = facility,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun OccupationsSection(
    selected: List<String>,
    onChange: (List<String>) -> Unit
) {
    Column {
        Text("Occupation", style = MaterialTheme.typography.titleMedium)

        OCCUPATIONS.forEach { occ ->
            val isChecked = selected.contains(occ.name)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        val updated = if (isChecked)
                            selected - occ.name
                        else
                            selected + occ.name

                        onChange(updated)
                    }
                )
                Text(
                    text = occ.name,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun RateSliderSection(
    value: Double,
    onChange: (Double) -> Unit
) {
    Column {
        Text("Minimum Rating", style = MaterialTheme.typography.titleMedium)

        Slider(
            value = value.toFloat(),
            onValueChange = { new ->
                val stepped = new.toInt().coerceIn(0, 5)
                onChange(stepped.toDouble())
            },
            valueRange = 0f..5f,
            steps = 4
        )

        Text(
            text = "${value.toInt()} stars",
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

