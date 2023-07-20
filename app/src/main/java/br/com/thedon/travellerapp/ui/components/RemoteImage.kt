package br.com.thedon.travellerapp.ui.components

import android.annotation.SuppressLint
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import br.com.thedon.travellerapp.R
import br.com.thedon.travellerapp.ui.theme.compositedOnSurface
import coil.compose.AsyncImage

/**
 * A wrapper around [AsyncImage], setting a default [contentScale] and showing
 * content while loading.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun RemoteImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderColor: Color = MaterialTheme.colors.compositedOnSurface(0.2f)
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        placeholder = painterResource(R.drawable.photo_placeholder),
        modifier = modifier,
        contentScale = contentScale
    )
}