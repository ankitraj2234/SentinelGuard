package com.sentinelguard.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sentinelguard.R

/**
 * SentinelGuard App Logo Component
 * 
 * Displays the detective logo in a circular shape.
 * Clean and professional design.
 */
@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    size: Dp = 72.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.sentinel_logo),
            contentDescription = "SentinelGuard Logo",
            modifier = Modifier
                .size(size * 0.75f)  // Logo takes 75% of the circle
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Compact version for toolbar/header use
 */
@Composable
fun AppLogoSmall(
    modifier: Modifier = Modifier
) {
    AppLogo(
        modifier = modifier,
        size = 40.dp
    )
}

/**
 * Large version for splash/setup screens
 */
@Composable
fun AppLogoLarge(
    modifier: Modifier = Modifier
) {
    AppLogo(
        modifier = modifier,
        size = 100.dp
    )
}

