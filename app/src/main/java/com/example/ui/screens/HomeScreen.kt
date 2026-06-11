package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.AppViewModel
import com.example.ui.theme.BorderColor
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.SurfaceDarkBar
import com.example.ui.theme.SurfaceDarkCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrendGreen

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    userProfile: UserProfile?,
    onNavigateToAllocation: () -> Unit,
    onNavigateToDisclaimer: () -> Unit,
    onNavigateToTips: () -> Unit
) {
    var cashInput by remember { mutableStateOf(TextFieldValue("")) }
    var inputError by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val niftyPrice by viewModel.niftyPrice.collectAsState()
    val niftyChange by viewModel.niftyChange.collectAsState()
    val niftyChangePercent by viewModel.niftyChangePercent.collectAsState()

    val formattedNiftyPrice = String.format("%,.2f", niftyPrice)
    val formattedNiftyChange = String.format("%+,.2f", niftyChange)
    val formattedNiftyChangePercent = String.format("%+,.2f", niftyChangePercent)
    val isNiftyPositive = niftyChange >= 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header Section with Nifty Status Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDarkBar),
            shape = RoundedCornerShape(16.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isNiftyPositive) TrendGreen else Color.Red)
                        )
                        Text(
                            text = "Nifty 50 Index (Real-time)",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹$formattedNiftyPrice",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "$formattedNiftyChange ($formattedNiftyChangePercent%)",
                        color = if (isNiftyPositive) TrendGreen else Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = { viewModel.fetchNiftyLevel() },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = SurfaceDarkCard)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Market Rate",
                        tint = PrimaryAccent
                    )
                }
            }
        }

        // Available Cash Input Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDarkCard),
            shape = RoundedCornerShape(24.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "SPECIFY INVESTMENT CAPITAL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryAccent,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = "GrowKaro survivalist model allocates every individual rupee precisely to avoid waste and minimize leverage risks.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                )

                OutlinedTextField(
                    value = cashInput,
                    onValueChange = {
                        cashInput = it
                        inputError = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cash_input_field"),
                    label = { Text("Enter available cash (₹)", color = TextSecondary) },
                    placeholder = { Text("e.g. 1000, 2500, 10000", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = PrimaryAccent,
                        unfocusedBorderColor = BorderColor,
                        cursorColor = PrimaryAccent
                    ),
                    isError = inputError != null
                )

                if (inputError != null) {
                    Text(
                        text = inputError ?: "",
                        color = Color.Red,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Button(
                    onClick = {
                        val amtStr = cashInput.text.trim()
                        val amt = amtStr.toDoubleOrNull()
                        if (amt == null || amt <= 0.0) {
                            inputError = "Please enter a valid amount greater than 0"
                        } else if (amt < 100.0) {
                            inputError = "Minimum allocation amount is ₹100 for proper stock slicing"
                        } else {
                            keyboardController?.hide()
                            viewModel.generateAllocation(amt)
                            onNavigateToAllocation()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("get_allocation_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Analysis",
                            tint = SurfaceDarkBar
                        )
                        Text(
                            text = "GET SMART ALLOCATION",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceDarkBar
                        )
                    }
                }
            }
        }

        // Profile quick checklist badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDarkBar),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceDarkCard),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Safety Level Indicator",
                        tint = TrendGreen
                    )
                }
                Column {
                    Text(
                        text = "Active Target Strategy profile",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${userProfile?.riskTolerance ?: "Moderate"} Risk • Age ${userProfile?.age ?: 25}",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation Quick Shortcuts & Disclaimers
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clickable { onNavigateToTips() }
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Tips",
                        tint = PrimaryAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Survival Tips",
                        color = PrimaryAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(text = "•", color = TextMuted)

                Row(
                    modifier = Modifier
                        .clickable { onNavigateToDisclaimer() }
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Disclaimer Detail",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Legal Disclaimer",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = "Trading involves capital risk. We are not SEBI registered advisors. Smart allocations computed as trends analysis modeling.",
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = TextMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
