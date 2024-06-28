package com.example.jetpacknews.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.jetpacknews.database.NewsModel
import com.example.jetpacknews.ui.theme.JetpackNewsTheme
import com.example.jetpacknews.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

@Composable
fun NewsHome(
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel,
    navController: NavController
    ) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val newsListState = viewModel.newsList.observeAsState()
        val newsState = rememberLazyListState()
        val paginatedNews = viewModel.pagingNewsList.collectAsLazyPagingItems()
        Spacer(Modifier.height(16.dp))
        SearchBox { viewModel.getFilteredPagingNews(it) }
        Spacer(Modifier.height(16.dp))
        CategoryList(
            categories = viewModel.getCatergoriesList(),
            onClick = {
                viewModel.onCategorySelected(text = it)
            },
            newsState = newsState
        )
        Spacer(Modifier.height(16.dp))
        Log.i("NewsHome", newsListState.value.toString())
        newsListState.value?.let { newsList ->
            NewsList(
                modifier = Modifier.fillMaxWidth(),
                newsList = newsList,
                paginatedNews = paginatedNews,
                navController = navController,
                newsState = newsState
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun NewsHomePreview() {
    JetpackNewsTheme {
//        NewsHome()
    }
}