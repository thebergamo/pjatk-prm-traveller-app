package br.com.thedon.travellerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.thedon.travellerapp.R
import br.com.thedon.travellerapp.ui.navigation.NavigationDestination

object LoadingDestination: NavigationDestination {
    override val route = "loading"
    override val titleRes = R.string.loading_title
}

@Composable
fun LoadingScreen() {

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .wrapContentSize(align = Alignment.Center)
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(align = Alignment.Center)
        )
        Text("Loading...")
    }
}