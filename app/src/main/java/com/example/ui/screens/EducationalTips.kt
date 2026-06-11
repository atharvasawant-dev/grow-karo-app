package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderColor
import com.example.ui.theme.InfoBlue
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
fun EducationalTips(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Portfolio Survival Tips",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen tagline
            Text(
                text = "Surviving with Small Capitals",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "When investing under ₹5000, high management transaction fees can devour growth. GrowKaro leverages these foundational strategies to defend your wealth:",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            // Tip Card 1
            TipCard(
                title = "1. Avoid Frequent Trading Surcharges",
                body = "DP charges and exchange taxes are flat per transaction block in brokers like Groww/Zerodha. Thus, repeatedly buying and selling ₹200 worth shares everyday creates negative return curves. Aim to Buy and Accumulate using monthly delivery quotas.",
                icon = Icons.Default.Shield,
                color = TrendGreen
            )

            // Tip Card 2
            TipCard(
                title = "2. The 'Survival Instinct' Ratio",
                body = "Never allocate 100% of your holdings into growth sector stocks. Power grids and Renewable projects run on multi-year building cycles which are volatile. Keep 5-15% in Liquid cash buffers or GOLDBEES (Gold ETFs) to buy deep standard dips.",
                icon = Icons.Default.WorkspacePremium,
                color = WarningOrange
            )

            // Tip Card 3
            TipCard(
                title = "3. Compound Effect Calculations",
                body = "A CAGR (Compound Annual Growth Rate) of 20% turns a single purchase of ₹1,000 into ₹2,488 in 5 years, and ₹6,191 in 10 years. Continuous consistent saving outweighs choosing high risk single stock gambles.",
                icon = Icons.Default.Percent,
                color = InfoBlue
            )

            // Tip Card 4
            TipCard(
                title = "4. Direct Deliveries vs Intraday",
                body = "Direct stock deliveries (Buy CNC) let you hold asset ownership indefinitely. Intraday leverage forces automated end-of-day losses and square-offs during rapid panics.",
                icon = Icons.Default.LocalFireDepartment,
                color = PrimaryAccent
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Disclaimer: GrowKaro AI aggregates public equity records. Historical return yields are not guarantees for recurring future outcomes.",
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                color = TextMuted,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TipCard(
    title: String,
    body: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDarkCard),
        shape = RoundedCornerShape(16.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = body,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
