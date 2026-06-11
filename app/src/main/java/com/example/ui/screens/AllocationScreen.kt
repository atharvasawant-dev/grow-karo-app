package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Recommendation
import com.example.data.model.StockAllocation
import com.example.ui.AppViewModel
import com.example.ui.theme.BorderColor
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.SurfaceDarkBar
import com.example.ui.theme.SurfaceDarkCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrendGreen
import com.example.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllocationScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    val currentRec by viewModel.currentRecommendation.collectAsState()
    val allocations by viewModel.currentAllocations.collectAsState()
    val loading by viewModel.isAllocationLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Smart Allocation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to input screen",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDarkBar,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            if (loading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = PrimaryAccent)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Analyzing Market Momentum...",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Calculating ideal hedge ratios and sector weights",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            } else if (currentRec == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "No recommendation loaded",
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Allocation Generated",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please go back to the home view and input your available investment capital.",
                        color = TextMuted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                    ) {
                        Text(text = "Go to Cash Input", color = SurfaceDarkBar)
                    }
                }
            } else {
                val recommendation = currentRec!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Summary Card / Header with total Rupees amount
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDarkCard),
                        shape = RoundedCornerShape(20.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = "SMART RUPEE ALLOCATION",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "₹${String.format("%,.2f", recommendation.amount)}",
                                        color = PrimaryAccent,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(PrimaryContainer)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Survival Mode",
                                        color = OnPrimaryContainer,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Divider(color = BorderColor, thickness = 1.dp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Expected Range",
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = recommendation.expectedReturn,
                                        color = TrendGreen,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Risk Matrix",
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Moderate - High",
                                        color = WarningOrange,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Allocation high density table
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDarkBar),
                        shape = RoundedCornerShape(16.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                    ) {
                        Column {
                            // Header banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BorderColor)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "ALLOCATION STRATEGY",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "v4.2 AI Model Ready",
                                        fontSize = 10.sp,
                                        color = PrimaryAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Table column labels
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceDarkCard)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Symbol",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1.5f)
                                )
                                Text(
                                    text = "Amount",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1.0f),
                                    textAlign = TextAlign.End
                                )
                                Text(
                                    text = "Risk Level",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1.2f),
                                    textAlign = TextAlign.End
                                )
                            }

                            Divider(color = BorderColor, thickness = 1.dp)

                            // List items
                            allocations.forEach { item ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1.5f)) {
                                            Text(
                                                text = item.name.substringBefore(" ("),
                                                color = TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            val fullName = item.name.substringAfter(" (", "").substringBefore(")")
                                            if (fullName.isNotEmpty()) {
                                                Text(
                                                    text = fullName,
                                                    color = TextMuted,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }

                                        Text(
                                            text = "₹${String.format("%,.0f", item.amount)}",
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.weight(1.0f),
                                            textAlign = TextAlign.End
                                        )

                                        Box(
                                            modifier = Modifier
                                                .weight(1.2f)
                                                .padding(start = 12.dp),
                                            contentAlignment = Alignment.CenterEnd
                                        ) {
                                            val (badgeBg, badgeText) = when (item.risk.lowercase()) {
                                                "high" -> Pair(Color(0x33EF4444), DangerRed)
                                                "low" -> Pair(Color(0x3322C55E), TrendGreen)
                                                else -> Pair(Color(0x33F97316), WarningOrange)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .border(1.dp, badgeText.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                                    .background(badgeBg)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = item.risk,
                                                    color = badgeText,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 0.5.sp
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Justification
                                    Text(
                                        text = "↳ Trend: ${item.reason}",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }
                                Divider(color = BorderColor.copy(alpha = 0.5f), thickness = 0.5.dp)
                            }
                        }
                    }

                    // Risk Warning Caution Callout
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0x1ADB923C)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Safety guidelines and notifications",
                                tint = WarningOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "AI Survival Metric Notice",
                                    color = WarningOrange,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = recommendation.riskWarning.ifBlank {
                                        "Small portfolios (<₹5k) are sensitive to index friction. Limit transaction fees by bundling orders in full deliveries."
                                    },
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // How to Invest step-by-step
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDarkCard),
                        shape = RoundedCornerShape(16.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "HOW TO INVEST ON GROWW / ZERODHA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryAccent,
                                letterSpacing = 1.sp
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                StepRow(step = "1", text = "Open your brokerage app (Groww, Zerodha, AngelOne or Upstox).")
                                StepRow(step = "2", text = "Add ₹${String.format("%,.0f", recommendation.amount)} cash to your account balance.")
                                StepRow(step = "3", text = "Search each stock/ETF by its core symbol code shown in the table.")
                                StepRow(step = "4", text = "Use 'Delivery' type (CNC) instead of Intraday to construct a long term hedge.")
                                StepRow(step = "5", text = "Input buy orders using the calculated allocation weights.")
                            }
                        }
                    }

                    // Broker launch button
                    Button(
                        onClick = { /* Launch Broker context */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("broker_action_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Launch,
                                contentDescription = "Launch",
                                tint = SurfaceDarkBar
                            )
                            Text(
                                text = "MANUAL LOGGING IN GROWW / ZERODHA",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceDarkBar
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun StepRow(step: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(PrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                color = OnPrimaryContainer,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
