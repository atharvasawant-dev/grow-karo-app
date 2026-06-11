package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
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

@Composable
fun SettingsScreen(
    viewModel: AppViewModel,
    userProfile: UserProfile?,
    onNavigateToDisclaimer: () -> Unit,
    onNavigateToTips: () -> Unit
) {
    var ageStr by remember(userProfile) { mutableStateOf(userProfile?.age?.toString() ?: "25") }
    var selectedRisk by remember(userProfile) { mutableStateOf(userProfile?.riskTolerance ?: "Moderate") }
    var customApiKey by remember(userProfile) { mutableStateOf(userProfile?.customApiKey ?: "") }
    var saveStatusMsg by remember { mutableStateOf<String?>(null) }
    var showResetDetails by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Title
        Column {
            Text(
                text = "Target Profile & Settings",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Define your demographic parameters to calibrate the smart advisor",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        // Profile Parameters Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDarkCard),
            shape = RoundedCornerShape(20.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "DETERMINE DEMOGRAPHICS & RISK",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryAccent,
                    letterSpacing = 1.sp
                )

                // Age input Field
                OutlinedTextField(
                    value = ageStr,
                    onValueChange = { ageStr = it.filter { char -> char.isDigit() } },
                    label = { Text("Profile Target Age", color = TextSecondary) },
                    placeholder = { Text("e.g. 25, 40", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = PrimaryAccent,
                        unfocusedBorderColor = BorderColor
                    )
                )

                // Custom Gemini API Key input
                OutlinedTextField(
                    value = customApiKey,
                    onValueChange = { customApiKey = it },
                    label = { Text("Custom Gemini API Key (Optional)", color = TextSecondary) },
                    placeholder = { Text("Enter AI Studio key (AIzaSy...)", color = TextMuted) },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("api_key_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = PrimaryAccent,
                        unfocusedBorderColor = BorderColor
                    )
                )

                // Risk level radio selection row
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Risk Tolerance Profile",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val riskProfiles = listOf("Conservative", "Moderate", "Aggressive")
                        riskProfiles.forEach { r ->
                            val isSelected = selectedRisk.lowercase() == r.lowercase()
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PrimaryAccent else SurfaceDarkBar)
                                    .clickable { selectedRisk = r }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = r,
                                    color = if (isSelected) SurfaceDarkBar else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Save button
                Button(
                    onClick = {
                        val ageInt = ageStr.toIntOrNull() ?: 25
                        viewModel.saveProfile(ageInt, selectedRisk, customApiKey)
                        saveStatusMsg = "Demographics saved successfully!"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("save_profile_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Text(text = "Save Profiles", color = SurfaceDarkBar, fontWeight = FontWeight.Bold)
                }

                // Temporary confirmation notification
                if (saveStatusMsg != null) {
                    Text(
                        text = saveStatusMsg!!,
                        color = TrendGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        // Section header for external resources
        Text(
            text = "Resources & Disclosures",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        // Navigation tiles inside settings
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NavigationSettingTile(
                title = "Educational Survival Tips",
                subtitle = "Read investment philosophies & portfolio basics",
                icon = Icons.Default.Lightbulb,
                iconColor = WarningOrange,
                onClick = onNavigateToTips
            )

            NavigationSettingTile(
                title = "Legal Disclaimer Document",
                subtitle = "Mandatory SEC & SEBI regulatory notifications",
                icon = Icons.Default.Gavel,
                iconColor = PrimaryAccent,
                onClick = onNavigateToDisclaimer
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // System operations master controls (clear data)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDarkBar),
            shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 0.5.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Database master operations",
                            tint = DangerRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Admin Master controls",
                            color = DangerRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(onClick = { showResetDetails = true }) {
                        Text("Reset System", color = DangerRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal dialogue confirming database reset
    if (showResetDetails) {
        AlertDialog(
            onDismissRequest = { showResetDetails = false },
            containerColor = SurfaceDarkCard,
            title = {
                Text(
                    text = "Perform Master Factory Wipe?",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This action will permanently delete all logged buy holdings, user portfolio items, and historical recommendation parameters. It cannot be undone.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistoryAndData()
                        showResetDetails = false
                        saveStatusMsg = "All database records purged!"
                    }
                ) {
                    Text(text = "Purge Everything", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDetails = false }) {
                    Text(text = "Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun NavigationSettingTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceDarkBar),
        shape = RoundedCornerShape(12.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SurfaceDarkCard),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate to settings panel details",
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
