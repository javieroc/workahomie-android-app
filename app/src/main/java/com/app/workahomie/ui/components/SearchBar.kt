package com.app.workahomie.ui.components

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest

@SuppressLint("UseCompatLoadingForDrawables")
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onPlaceSelected: (placeId: String) -> Unit
) {
    AndroidView(
        factory = { ctx ->
            AutoCompleteTextView(ctx).apply {
                hint = "Where to?"
                textSize = 18f

                // Add left search icon manually
                setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_search,0, 0, 0)

                setBackgroundResource(android.R.drawable.editbox_background_normal)


                val placesClient = Places.createClient(ctx)
                val token = AutocompleteSessionToken.newInstance()
                val adapter = ArrayAdapter<String>(ctx, android.R.layout.simple_dropdown_item_1line)
                val placeIdMap = mutableMapOf<String, String>()

                setAdapter(adapter)

                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        val query = s?.toString().orEmpty()
                        if (query.isNotEmpty()) {
                            val request = FindAutocompletePredictionsRequest.builder()
                                .setSessionToken(token)
                                .setQuery(query)
                                .build()

                            placesClient.findAutocompletePredictions(request)
                                .addOnSuccessListener { predictions ->
                                    adapter.clear()
                                    placeIdMap.clear()
                                    predictions.autocompletePredictions.forEach {
                                        val desc = it.getFullText(null).toString()
                                        adapter.add(desc)
                                        placeIdMap[desc] = it.placeId
                                    }
                                    adapter.notifyDataSetChanged()
                                }
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })

                setOnItemClickListener { _, _, position, _ ->
                    val description = adapter.getItem(position)
                    val placeId = placeIdMap[description]
                    placeId?.let { onPlaceSelected(it) }
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}
