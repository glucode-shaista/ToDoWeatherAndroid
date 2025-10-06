package com.example.todoapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val backgroundColor: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    onRequestLocationPermission: () -> Unit = {}
) {
    val pages = listOf(
        OnboardingPage(
            title = "Welcome to ToDoApp",
            description = "Your ultimate task management companion. Organize, prioritize, and accomplish your goals with ease.",
            icon = Icons.Filled.CheckCircle,
            backgroundColor = listOf(
                Color(0xFF667eea),
                Color(0xFF764ba2)
            )
        ),
        OnboardingPage(
            title = "Smart Organization",
            description = "Categorize tasks by Work, Personal, Shopping, and more. Set priorities and due dates to stay on track.",
            icon = Icons.Filled.Category,
            backgroundColor = listOf(
                Color(0xFF764ba2),
                Color(0xFFf093fb)
            )
        ),
        OnboardingPage(
            title = "Advanced Filtering",
            description = "Find exactly what you need with powerful filters. Search by category, priority, status, or date.",
            icon = Icons.Filled.FilterList,
            backgroundColor = listOf(
                Color(0xFFf093fb),
                Color(0xFFf5576c)
            )
        ),
        OnboardingPage(
            title = "Weather Integration",
            description = "Plan your day better with integrated weather information. Know the conditions for your outdoor tasks.",
            icon = Icons.Filled.Cloud,
            backgroundColor = listOf(
                Color(0xFFf5576c),
                Color(0xFF4facfe)
            )
        ),
        OnboardingPage(
            title = "Location for Weather",
            description = "Allow location access to get accurate weather information for your area. This helps you plan outdoor tasks better!",
            icon = Icons.Filled.LocationOn,
            backgroundColor = listOf(
                Color(0xFF4facfe),
                Color(0xFF00f2fe)
            )
        ),
        OnboardingPage(
            title = "Get Started",
            description = "You're all set! Start creating tasks, setting priorities, and achieving your goals. Let's make today productive!",
            icon = Icons.Filled.Rocket,
            backgroundColor = listOf(
                Color(0xFF00f2fe),
                Color(0xFF667eea)
            )
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = pages[pagerState.currentPage].backgroundColor
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (pagerState.currentPage < pages.size - 1) {
                    TextButton(
                        onClick = onFinish,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Skip",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(
                    page = pages[page],
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Page indicators and navigation
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == pagerState.currentPage) 12.dp else 8.dp)
                                .background(
                                    color = if (index == pagerState.currentPage) {
                                        Color.White
                                    } else {
                                        Color.White.copy(alpha = 0.5f)
                                    },
                                    shape = CircleShape
                                )
                        )
                    }
                }

                // Navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (pagerState.currentPage == 0) {
                        Arrangement.End
                    } else if (pagerState.currentPage == pages.size - 1) {
                        Arrangement.Center
                    } else {
                        Arrangement.SpaceBetween
                    }
                ) {
                    // Previous button
                    if (pagerState.currentPage > 0) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(listOf(Color.White, Color.White))
                            ),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = "Previous",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Next/Get Started/Allow Location button
                    Button(
                        onClick = {
                            when {
                                // Location permission page (second to last)
                                pagerState.currentPage == pages.size - 2 -> {
                                    onRequestLocationPermission()
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                                // Final page
                                pagerState.currentPage == pages.size - 1 -> {
                                    onFinish()
                                }
                                // Regular pages
                                else -> {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF667eea)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = when {
                                pagerState.currentPage == pages.size - 2 -> "Allow Location"
                                pagerState.currentPage == pages.size - 1 -> "Get Started"
                                else -> "Next"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title
        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Description
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
