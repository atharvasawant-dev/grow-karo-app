package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.NetworkClient
import com.example.data.model.Recommendation
import com.example.data.model.StockAllocation
import com.example.ui.AppViewModel
import com.example.ui.theme.BorderColor
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.SurfaceDarkBar
import com.example.ui.theme.SurfaceDarkCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrendGreen
import com.example.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: AppViewModel
) {
    val historyList by viewModel.recommendationHistoryFlow.collectAsState(initial = emptyList())
    var selectedRecommendation by remember { mutableStateOf<Recommendation?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App header intro
        Column {
            Text(
                text = "Recommendation History",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Track how past AI allocations were structured",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(SurfaceDarkBar, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Zero items inside history log database",
                        tint = TextMuted,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "History is empty",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your suggested allocations will appear here of when you specify investable capitals on the Home dashboard.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("history_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(historyList) { rec ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRecommendation = rec },
                        colors = CardDefaults.cardColors(containerColor = SurfaceDarkBar),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 0.5.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Smart Allocation",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "₹${String.format("%,.0f", rec.amount)}",
                                    color = PrimaryAccent,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.SansSerif
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = rec.dateStr,
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "View Specs",
                                    color = PrimaryAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "Detail view icon",
                                    tint = PrimaryAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal popup showing previous allocation details
    if (selectedRecommendation != null) {
        val rec = selectedRecommendation!!

        // Parse list of allocations from SQLite JSON string safely
        val parsedAllocations = remember(rec) {
            try {
                val moshi = NetworkClient.getMoshi()
                val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, StockAllocation::class.java)
                val adapter = moshi.adapter<List<StockAllocation>>(listType)
                adapter.fromJson(rec.allocationsJson) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }

        AlertDialog(
            onDismissRequest = { selectedRecommendation = null },
            containerColor = SurfaceDarkCard,
            confirmButton = {
                TextButton(onClick = { selectedRecommendation = null }) {
                    Text("Close", color = PrimaryAccent, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Column {
                    Text(
                        text = "Historical Allocation Review",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = rec.dateStr,
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Capital Imposed:",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "₹${String.format("%,.0f", rec.amount)}",
                            color = PrimaryAccent,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }

                    Divider(color = BorderColor)

                    // Historical item table list
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(parsedAllocations) { item ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SurfaceDarkBar),
                                border = ButtonDefaults.outlinedButtonBorder.copy(width = 0.5.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.name.substringBefore(" ("),
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "₹${String.format("%,.0f", item.amount)}",
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.reason,
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}
