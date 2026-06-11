package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PortfolioItem
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    viewModel: AppViewModel
) {
    val portfolioItems by viewModel.portfolioFlow.collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }

    // Aggregate portfolio values
    val totalInvested = portfolioItems.sumOf { it.amountInvested }
    val totalCurrentValue = portfolioItems.sumOf { it.quantity * it.currentPrice }
    val absolutePL = totalCurrentValue - totalInvested
    val plPercent = if (totalInvested > 0) (absolutePL / totalInvested) * 100.0 else 0.0
    val isProfit = absolutePL >= 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Portfolio summary card with P&L display
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDarkCard),
            shape = RoundedCornerShape(24.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PORTFOLIO NET VALUE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryAccent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹${String.format("%,.2f", totalCurrentValue)}",
                            color = TextPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }

                    // Simulated pricing activator
                    IconButton(
                        onClick = { viewModel.simulateMarketFluctuation() },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = SurfaceDarkBar),
                        modifier = Modifier.testTag("simulate_drift_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Simulate LIVE Price drift fluctuation",
                            tint = WarningOrange
                        )
                    }
                }

                Divider(color = BorderColor, thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Invested Capital",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "₹${String.format("%,.0f", totalInvested)}",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Total Unrealized Profit / Loss",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isProfit) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = if (isProfit) "Profits increased" else "Losses suffered",
                                tint = if (isProfit) TrendGreen else DangerRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${if (isProfit) "+" else ""}₹${String.format("%,.2f", absolutePL)} (${String.format("%+.2f", plPercent)}%)",
                                color = if (isProfit) TrendGreen else DangerRed,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Action Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "My Holdings",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "${portfolioItems.size} custom items tracked locally",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("log_holding_button")
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Holding",
                        tint = OnPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Log Buy",
                        color = OnPrimaryContainer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Holdings List view
        if (portfolioItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(SurfaceDarkBar, RoundedCornerShape(16.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalActivity,
                        contentDescription = "Zero items inside holding log database",
                        tint = TextMuted,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "No stock bought logs discovered",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Generate a smart allocation recommendation first, buy manually in your broker, and log bought tickers above to trace live balances.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(portfolioItems, key = { it.id }) { item ->
                    val plAmt = (item.currentPrice - item.buyPrice) * item.quantity
                    val itemProfit = plAmt >= 0
                    val currentVal = item.quantity * item.currentPrice

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDarkBar),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 0.5.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = item.ticker,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = item.stockName,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "₹${String.format("%,.2f", currentVal)}",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "${item.quantity.toInt()} shares",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = BorderColor.copy(alpha = 0.3f), thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Avg Buy Price",
                                            color = TextMuted,
                                            fontSize = 9.sp
                                        )
                                        Text(
                                            text = "₹${item.buyPrice}",
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Sim. Live Price",
                                            color = TextMuted,
                                            fontSize = 9.sp
                                        )
                                        Text(
                                            text = "₹${item.currentPrice}",
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (itemProfit) Color(0x1F22C55E) else Color(0x1FCE1313))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${if (itemProfit) "+" else ""}₹${String.format("%.1f", plAmt)}",
                                            color = if (itemProfit) TrendGreen else DangerRed,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.deletePortfolioItem(item.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Holding",
                                            tint = TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal buy logging dialogue
    if (showAddDialog) {
        var ticker by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        var priceStr by remember { mutableStateOf("") }
        var quantityStr by remember { mutableStateOf("") }
        var errorOccurred by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = SurfaceDarkCard,
            title = {
                Text(
                    text = "Log Asset Purchase",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Add custom stock or ETF units purchased manually in Zerodha/Groww to track them in our local portfolio dashboard.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = ticker,
                        onValueChange = { ticker = it.uppercase(Locale.ROOT) },
                        label = { Text("Ticker Symbol (e.g. TATAPOWER)", color = TextSecondary) },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryAccent,
                            unfocusedBorderColor = BorderColor
                        )
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Company Name (e.g. Tata Power Ltd)", color = TextSecondary) },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryAccent,
                            unfocusedBorderColor = BorderColor
                        )
                    )

                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Average Buy Price (₹)", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryAccent,
                            unfocusedBorderColor = BorderColor
                        )
                    )

                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        label = { Text("Quantity Bought", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryAccent,
                            unfocusedBorderColor = BorderColor
                        )
                    )

                    if (errorOccurred != null) {
                        Text(text = errorOccurred!!, color = Color.Red, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val priceVal = priceStr.toDoubleOrNull()
                        val qtyVal = quantityStr.toDoubleOrNull()
                        if (ticker.isBlank() || name.isBlank() || priceVal == null || qtyVal == null || priceVal <= 0 || qtyVal <= 0) {
                            errorOccurred = "Please fill in all details with positive numeric values"
                        } else {
                            viewModel.buyAsset(
                                ticker = ticker,
                                name = name,
                                amountInvested = priceVal * qtyVal,
                                buyPrice = priceVal,
                                quantity = qtyVal
                            )
                            showAddDialog = false
                        }
                    }
                ) {
                    Text(text = "Save Holdings", color = PrimaryAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(text = "Discard", color = TextSecondary)
                }
            }
        )
    }
}
