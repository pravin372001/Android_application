package com.example.jetpacknews.components

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberAsyncImagePainter
import com.example.jetpacknews.NavigationItem
import com.example.jetpacknews.R
import com.example.jetpacknews.database.NewsModel
import com.example.jetpacknews.ui.theme.JetpackNewsTheme
import com.example.jetpacknews.viewmodel.NewsViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NewsItem(
    modifier: Modifier = Modifier,
    news: NewsModel,
    context: Context = LocalContext.current,
    navController: NavController
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ){
        Column(
            modifier = modifier
                .width(320.dp)
                .clip(RoundedCornerShape(8.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = news.imageUrl),
                contentDescription = null,
                modifier = modifier
                    .heightIn(120.dp)
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .size(128.dp)
            )
            Text(
                text = news.title,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = modifier
                    .padding(horizontal = 8.dp)
            )
            Text(
                text = news.date + " " + news.time,
                style = MaterialTheme.typography.bodySmall,
                modifier = modifier
                    .padding(horizontal = 8.dp)
            )
            Row (
                modifier = modifier
                    .align(Alignment.End)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),

            ){
                IconButton(
                    modifier =  modifier.size(40.dp),
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_TEXT, news.title)
                        intent.putExtra(Intent.EXTRA_TEXT, news.readMoreUrl)
                        ContextCompat.startActivity(context, intent, null)
                    }
                ) {
                    Image(
                        imageVector = Icons.Filled.Share,
                        contentDescription = null
                    )
                }
                IconButton(
                    modifier =  modifier.size(40.dp),
                    onClick = {
                        val encodedUrl = URLEncoder.encode(news.readMoreUrl, StandardCharsets.UTF_8.toString())
                        navController.navigate("${NavigationItem.WebViews.route}/${encodedUrl}")
                    }
                ){
                    Image(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
fun NewsList(
    modifier: Modifier = Modifier,
    newsList: List<NewsModel>,
    paginatedNews: LazyPagingItems<NewsModel>,
    navController: NavController,
    newsState: LazyListState = rememberLazyListState()
    ) {
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .height(510.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = newsState
    ) {
        Log.i("NewsList", newsList.toString())
//        if (newsList.isNotEmpty()) {
//            items(newsList) { news ->
//                NewsItem(
//                    news = news,
//                    navController = navController
//                )
//            }
//        } else {
//            Log.d("NewsList", "Empty")
//        }
        items(paginatedNews){news ->
            if (news != null) {
                NewsItem(
                    news = news,
                    navController = navController
                )
            }
        }
        paginatedNews.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                loadState.append is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                loadState.refresh is LoadState.Error -> {
                    item {
                        Text(
                            text = "An error has occurred",
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val curNav = rememberSaveable {
        mutableStateOf(NavigationItem.News.route)
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        val items = listOf(
            Triple(NavigationItem.News.route , Icons.Filled.Newspaper , R.string.bottom_news),
            Triple(NavigationItem.Weather.route , Icons.Filled.DeviceThermostat , R.string.bottom_weather)
        )

        items.forEach { (navItem, icon, labelRes) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(labelRes))
                },
                selected = curNav.value == navItem,
                onClick = {
                    if (curNav.value != navItem) {
                        navController.popBackStack()
                        curNav.value = navItem
                        navController.navigate(navItem)
                    }
                }
            )
        }
    }
}

@Composable
fun BottomNavigationRail(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val curNav = rememberSaveable {
        mutableStateOf(NavigationItem.News.route)
    }

    val items = listOf(
        Triple(NavigationItem.News.route , Icons.Filled.Newspaper , R.string.bottom_news),
        Triple(NavigationItem.Weather.route , Icons.Filled.DeviceThermostat , R.string.bottom_weather)
    )
    items.forEach { (navItem, icon, labelRes) ->
        NavigationRailItem(
            icon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            },
            label = {
                Text(stringResource(labelRes))
            },
            selected = curNav.value == navItem,
            onClick = {
                if (curNav.value != navItem) {
                    navController.popBackStack()
                    curNav.value = navItem
                    navController.navigate(navItem)
                }
            }
        )
    }
}


@Composable
fun SearchBox(
    onQueryChange: (String) -> Unit,
) {
    var searchText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .height(48.dp)
            .fillMaxWidth()
    ) {

        TextField(
            value = searchText,
            onValueChange = { searchText = it; onQueryChange(it)  },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            placeholder = { Text(text = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                MaterialTheme.colorScheme.onSurface,
            ),
            colors = TextFieldDefaults.colors(
                disabledTextColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )

    }
}

@Preview
@Composable
fun SearchPreview() {
    JetpackNewsTheme {
        SearchBox({})
    }
}
