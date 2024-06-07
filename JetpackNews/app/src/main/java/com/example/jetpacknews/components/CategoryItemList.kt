package com.example.jetpacknews.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpacknews.ui.theme.JetpackNewsTheme
import com.example.jetpacknews.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

@Composable
fun CategoryItem(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    newsState: LazyListState
) {
    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = modifier
            .wrapContentSize()
            .padding(8.dp)
            .clickable {
                onClick(text)
                coroutineScope.launch {
                    newsState.animateScrollToItem(0)
                }
            }
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(16.dp)
        )
    }
}

@Composable
fun CategoryList(
    categories : List<String>,
    onClick:  (String) -> Unit,
    newsState: LazyListState
) {
    LazyRow {
        items(categories) { it ->
            CategoryItem(
                text = it.capitalizeFirstLetter(),
                onClick = onClick,
                newsState = newsState
            )
        }
    }
}

private fun String.capitalizeFirstLetter(): String {
    return if (isNotEmpty()) {
        this[0].toUpperCase() + substring(1)
    } else {
        this
    }
}



@Preview(showBackground = true)
@Composable
private fun CategoryPreview() {
    JetpackNewsTheme {
        CategoryItem(
            text = "Technology",
            onClick = {},
            newsState = LazyListState()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryPreviewWithDifferentModifiers() {
    JetpackNewsTheme {
        CategoryList(categories = listOf(
            "Technology",
            "Business",
            "Entertainment",
            "Science",
            "Sports",
            "Health",
            "Politics",
        ),
            onClick = {},
            newsState = LazyListState()
        )
    }
}
