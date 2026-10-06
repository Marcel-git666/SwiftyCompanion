package com.example.swiftycompanion.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.swiftycompanion.R
import com.example.swiftycompanion.features.users.domain.models.User

@Composable
fun ProfileHeader(
    user: User,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        if (maxWidth < 600.dp) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ProfilePhoto(imageUrl = user.imageUrl)
                ProfileDetails(user = user, modifier = Modifier.fillMaxWidth())
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                ProfilePhoto(imageUrl = user.imageUrl)
                ProfileDetails(user = user, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ProfilePhoto(
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = stringResource(R.string.profile_photo),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(128.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}

@Composable
private fun ProfileDetails(
    user: User,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = user.displayName, style = MaterialTheme.typography.headlineSmall)
        DetailRow(label = stringResource(R.string.profile_email), value = user.email)
        DetailRow(
            label = stringResource(R.string.profile_location),
            value = user.location ?: stringResource(R.string.profile_location_unavailable),
        )
        DetailRow(label = stringResource(R.string.profile_wallet), value = "${user.wallet} ₳")
        DetailRow(label = stringResource(R.string.profile_correction_points), value = user.correctionPoints.toString())
        DetailRow(
            label = stringResource(R.string.profile_level),
            value = user.cursus?.let {
                stringResource(R.string.profile_level_value, it.level.whole, it.level.percent)
            } ?: stringResource(R.string.profile_no_cursus),
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}