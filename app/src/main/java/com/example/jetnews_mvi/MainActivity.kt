package com.example.jetnews_mvi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jetnews_mvi.feature.interests.InterestsIntent
import com.example.jetnews_mvi.feature.interests.InterestsSideEffect
import com.example.jetnews_mvi.feature.interests.InterestsViewModel
import com.example.jetnews_mvi.feature.interests.InterestsViewState
import com.example.jetnews_mvi.feature.news.NewsIntent
import com.example.jetnews_mvi.feature.news.NewsSideEffect
import com.example.jetnews_mvi.feature.news.NewsViewModel
import com.example.jetnews_mvi.feature.news.NewsViewState
import com.example.jetnews_mvi.model.Interest
import com.example.jetnews_mvi.model.InterestSection
import com.example.jetnews_mvi.model.News
import com.example.jetnews_mvi.ui.theme.JetNewsMVITheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetNewsMVITheme {
                JetNewsApp()
            }
        }
    }
}

@Composable
fun JetNewsApp() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = currentDestination?.route ?: "news"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "News") },
                    label = { Text("News") },
                    selected = currentScreen == "news",
                    onClick = { navController.navigate("news") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Interests") },
                    label = { Text("Interests") },
                    selected = currentScreen == "interests",
                    onClick = { navController.navigate("interests") }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "news",
            modifier = Modifier.padding(padding)
        ) {
            composable("news") {
                NewsScreen()
            }
            composable("interests") {
                InterestsScreen()
            }
        }
    }
}

@Composable
fun NewsScreen(
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(initialValue = NewsViewState.Empty)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.processIntent(NewsIntent.LoadNews)
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is NewsSideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is NewsSideEffect.ShowBookmarkMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is NewsSideEffect.NavigateToDetail -> {
                    // TODO: Implement navigation
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when (viewState) {
            is NewsViewState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is NewsViewState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = (viewState as NewsViewState.Error).message ?: "Unknown error")
                }
            }
            is NewsViewState.Success -> {
                NewsList(
                    news = (viewState as NewsViewState.Success).news,
                    bookmarkedNewsIds = (viewState as NewsViewState.Success).bookmarkedNewsIds,
                    onNewsClick = { newsId ->
                        viewModel.processIntent(NewsIntent.SelectNews(newsId))
                    },
                    onBookmarkClick = { newsId ->
                        viewModel.processIntent(NewsIntent.BookmarkNews(newsId))
                    },
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {}
        }
    }
}

@Composable
fun NewsList(
    news: List<News>,
    bookmarkedNewsIds: Set<String>,
    onNewsClick: (String) -> Unit,
    onBookmarkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(news) { newsItem ->
            NewsCard(
                news = newsItem,
                isBookmarked = bookmarkedNewsIds.contains(newsItem.id),
                onNewsClick = { onNewsClick(newsItem.id) },
                onBookmarkClick = { onBookmarkClick(newsItem.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsCard(
    news: News,
    isBookmarked: Boolean,
    onNewsClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onNewsClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = news.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = news.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onBookmarkClick) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark"
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${news.metadata.author.name} · ${news.metadata.readTimeMinutes} min read",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun InterestsScreen(
    modifier: Modifier = Modifier,
    viewModel: InterestsViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(initialValue = InterestsViewState.Empty)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.processIntent(InterestsIntent.LoadInterests)
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is InterestsSideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is InterestsSideEffect.ShowSelectionMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when (viewState) {
            is InterestsViewState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is InterestsViewState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = (viewState as InterestsViewState.Error).message ?: "Unknown error")
                }
            }
            is InterestsViewState.Success -> {
                InterestsList(
                    interestSections = (viewState as InterestsViewState.Success).interestSections,
                    onInterestClick = { interestId, selected ->
                        if (selected) {
                            viewModel.processIntent(
                                InterestsIntent.ToggleInterestSelection(
                                    interestId
                                )
                            )
                        }
                    },
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {}
        }
    }
}

@Composable
fun InterestsList(
    interestSections: List<InterestSection>,
    onInterestClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        interestSections.forEach { section ->
            item {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(section.interests) { interest ->
                InterestItem(
                    interest = interest,
                    onInterestClick = onInterestClick
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestItem(
    interest: Interest,
    onInterestClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { onInterestClick(interest.id, !interest.isSelected) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = interest.name,
                    style = MaterialTheme.typography.titleMedium
                )
                interest.description?.let { description ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Checkbox(
                checked = interest.isSelected,
                onCheckedChange = { onInterestClick(interest.id, it) }
            )
        }
    }
}