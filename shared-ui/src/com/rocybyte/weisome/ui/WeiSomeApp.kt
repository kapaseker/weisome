package com.rocybyte.weisome.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.generated.resources.Res
import com.rocybyte.weisome.generated.resources.ic_welcome
import org.jetbrains.compose.resources.painterResource

/** Renders the root content of the WeiSome desktop application. */
@Composable
fun WeiSomeApp() {
    WeiSomeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_welcome),
                contentDescription = null,
                modifier = Modifier.size(96.dp),
            )
            Text("WeiSome", style = MaterialTheme.typography.headlineMedium)
            Text("Desktop application starter", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

