package com.app.workahomie.ui.components

import android.annotation.SuppressLint
import android.util.Log
import android.widget.AutoCompleteTextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import org.json.JSONObject

@SuppressLint("SetTextI18n")
@Composable
fun AddressInputField(
    initialAddress: String,
    onAddressSelected: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val textColor = colors.onSurface.toArgb()
    val hintColor = colors.onSurfaceVariant.copy(alpha = 0.6f).toArgb()
    val backgroundColor = colors.surface.toArgb()
    val borderColor = colors.outline.copy()
    val dropdownBg = colors.surfaceColorAtElevation(3.dp).toArgb()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(vertical = 6.dp)
            .border(
                width = 1.4.dp,
                color = borderColor,
                shape = RoundedCornerShape(4.dp)
            )
            .background(colors.surface, RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp)
    ) {
        AndroidView(
            factory = { ctx ->
                val placesClient = Places.createClient(ctx)
                val token = AutocompleteSessionToken.newInstance()

                val input = AutoCompleteTextView(ctx).apply {
                    hint = "Search address..."
                    setText(initialAddress)
                    textSize = 16f
                    setTextColor(textColor)
                    setHintTextColor(hintColor)
                    setBackgroundColor(backgroundColor)
                    setPadding(0, 24, 0, 24)
                }

                // Create a custom ArrayAdapter that applies dark/light text color
                val adapter = object : android.widget.ArrayAdapter<String>(
                    ctx,
                    android.R.layout.simple_dropdown_item_1line
                ) {
                    override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                        val view = super.getView(position, convertView, parent)
                        val textView = view.findViewById<android.widget.TextView>(android.R.id.text1)
                        textView.setTextColor(textColor)
                        textView.textSize = 16f
                        view.setBackgroundColor(dropdownBg)
                        return view
                    }
                }

                input.setDropDownBackgroundDrawable(android.graphics.drawable.ColorDrawable(dropdownBg))
                val placeIdMap = mutableMapOf<String, String>()
                input.setAdapter(adapter)

                input.addTextChangedListener(object : android.text.TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        val query = s?.toString().orEmpty()
                        if (query.length > 2) {
                            val request = FindAutocompletePredictionsRequest.builder()
                                .setSessionToken(token)
                                .setQuery(query)
                                .build()

                            placesClient.findAutocompletePredictions(request)
                                .addOnSuccessListener { response ->
                                    adapter.clear()
                                    placeIdMap.clear()
                                    response.autocompletePredictions.forEach {
                                        val desc = it.getFullText(null).toString()
                                        adapter.add(desc)
                                        placeIdMap[desc] = it.placeId
                                    }
                                    adapter.notifyDataSetChanged()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("AddressInputField", "Prediction error: ${e.message}")
                                }
                        }
                    }
                    override fun afterTextChanged(s: android.text.Editable?) {}
                })

                input.setOnItemClickListener { _, _, position, _ ->
                    val description = adapter.getItem(position)
                    val placeId = placeIdMap[description]
                    if (placeId != null) {
                        val placeRequest = FetchPlaceRequest.newInstance(
                            placeId,
                            listOf(
                                Place.Field.ID,
                                Place.Field.NAME,
                                Place.Field.ADDRESS,
                                Place.Field.LAT_LNG,
                                Place.Field.TYPES
                            )
                        )

                        placesClient.fetchPlace(placeRequest)
                            .addOnSuccessListener { placeResponse ->
                                val place = placeResponse.place
                                val json = JSONObject().apply {
                                    put("place_id", place.id)
                                    put("name", place.name)
                                    put("address", place.address)
                                    place.latLng?.let {
                                        put("lat", it.latitude)
                                        put("lon", it.longitude)
                                    }
                                    put("types", place.types?.map { it.name })
                                }.toString()

                                input.setText(place.address)
                                onAddressSelected(json)
                            }
                            .addOnFailureListener { e ->
                                Log.e("AddressInputField", "Failed to fetch place: ${e.message}")
                            }
                    }
                }

                input
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
