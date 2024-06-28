package com.example.jetpacknews.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jetpacknews.ui.theme.JetpackNewsTheme
import com.example.jetpacknews.viewmodel.NewsViewModel

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
        val newsState = rememberLazyListState()
        val paginatedNews = viewModel.pagingNewsList
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

        NewsList(
            modifier = Modifier.fillMaxWidth(),
            paginatedNewsState = paginatedNews,
            navController = navController,
            newsState = newsState
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NewsHomePreview() {
    JetpackNewsTheme {
//        NewsHome()
    }
}