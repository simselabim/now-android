package com.now.features.discoverymap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.now.app.AppState
import com.now.core.design.NowColors
import com.now.core.model.MapPoint
import com.now.core.model.MapPointState

@Composable
fun DiscoveryMapScreen(appState: AppState) {
    Column(modifier = Modifier.fillMaxSize()) {
        FakeMap(
            points = appState.visibleMapPoints,
            onPointTap = { appState.viewPoint(it) },
            modifier = Modifier.fillMaxWidth().height(420.dp)
        )
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ready today nearby", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Choose carefully. One live match.", color = NowColors.inkSoft)
                    appState.errorMessage?.let { Text(it, color = NowColors.inkSoft, fontSize = 12.sp) }
                }
                Button(onClick = { appState.goOffline() }) {
                    Text("Go Offline")
                }
            }
            if (appState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
            Text(
                "Tap a point to open profile. Not Now disappears until tomorrow. Block disappears permanently.",
                color = NowColors.inkSoft,
                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(10.dp)
            )
        }
    }
}

@Composable
private fun FakeMap(points: List<MapPoint>, onPointTap: (MapPoint) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(NowColors.tealPale), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = NowColors.teal.copy(alpha = 0.32f),
                radius = 145.dp.toPx(),
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
            )
            drawCircle(color = NowColors.teal, radius = 14.dp.toPx(), center = center)
            drawCircle(color = Color.White, radius = 6.dp.toPx(), center = center)
        }
        Text(
            "2 km radius",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(y = (-150).dp).background(Color.White, RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 6.dp)
        )
        points.forEachIndexed { index, point ->
            val offsets = listOf(
                Offset(-112f, -80f),
                Offset(104f, -18f),
                Offset(-52f, 112f),
                Offset(118f, 118f)
            )
            val offset = offsets[index % offsets.size]
            Box(
                modifier = Modifier
                    .offset(offset.x.dp, offset.y.dp)
                    .size(34.dp)
                    .clickable { onPointTap(point) },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(28.dp)) {
                    drawCircle(color = Color.White, radius = 14.dp.toPx())
                    drawCircle(color = colorFor(point.state), radius = 10.dp.toPx())
                }
            }
        }
    }
}

private fun colorFor(state: MapPointState): Color =
    when (state) {
        MapPointState.Unseen -> NowColors.teal
        MapPointState.Viewed -> NowColors.inkSoft
        MapPointState.Interested -> NowColors.coral
        MapPointState.HiddenToday, MapPointState.Blocked -> Color.Transparent
    }
