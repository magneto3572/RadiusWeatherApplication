package com.radius.weather.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.radius.weather.R
import com.radius.weather.data.localdb.Weather
import com.radius.weather.presentation.MainActivity
import com.radius.weather.presentation.viewmodel.WeatherHomeViewModel


@SuppressLint("UnrememberedMutableState", "MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun weatherSearch(
    navController : NavHostController,
    context: Context = LocalContext.current,
    viewModel: WeatherHomeViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.getAllDataFromLocalDb()
    }

    val state by viewModel.getSearchListItem.collectAsState()
    val localDbState by viewModel.getLocalDbResponse.collectAsState()

    if(state.isLoading && localDbState.isLoading){
        return
    }

    var text by remember {
        mutableStateOf("")
    }

    var active by remember {
        mutableStateOf(false)
    }

    val stateList by localDbState.list.collectAsState(initial = emptyList<Weather>())

    Column(modifier = Modifier.padding(16.dp)) {
        heading()
        SearchBar(
            shape = MaterialTheme.shapes.extraSmall,
            colors = SearchBarDefaults.colors(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 16.dp, 0.dp, 0.dp),
            query = text,
            onQueryChange = {
                text = it
                viewModel.getSearchSuggestion(it)
            } ,
            onSearch = {
                active = false
            },
            active = active,
            onActiveChange = {
                active = it
            },
            placeholder = {
                Text(text = "City, Region or US/UK zip code")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon")
            },
            trailingIcon = {
                if(active){
                    Icon(
                        modifier = Modifier.clickable {
                            if(text.isNotEmpty()){
                                text = ""
                            }else{
                                active = false
                            }
                        },
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon" )
                }
            }

        ) {
            Row(modifier = Modifier
                .padding(10.dp, 20.dp)
                .clickable {
                    val latLng = "${MainActivity.location?.latitude},${MainActivity.location?.longitude}"
                    if (latLng.isNotEmpty()){
                        navController.navigate("details/$latLng")
                    }
                }) {
                Icon(painterResource(id = R.drawable.near_me_fill0_wght400_grad0_opsz48), contentDescription = "Location Icon", tint = Color.Blue, modifier = Modifier
                    .height(20.dp)
                    .width(20.dp))
                Text(modifier = Modifier.padding(5.dp, 0.dp,0.dp,0.dp), text = "Current Location", color = Color.Blue, fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
            }
            state.list.forEach { a1 ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(16.dp, 0.dp)
                        .clickable {
                            navController.navigate("details/${a1.name}".trim())
                        }
                ) {
                    Text(text = a1.name)
                    Divider(thickness = 1.dp, color = Color.LightGray, modifier = Modifier.padding(0.dp, 10.dp,0.dp,0.dp))
                }
            }
        }
        Box(){
            if((stateList as List<Weather>).isNotEmpty()){
                savedLocationListView(viewModel = viewModel, stateList as List<Weather>, context)
            }else{
                emptyStateView()
            }
        }
    }
}


@Composable
private fun savedLocationListView(viewModel: WeatherHomeViewModel, list: List<Weather>, context: Context) {
    LazyColumn(modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth()
        .padding(0.dp, 16.dp, 0.dp, 0.dp)) {
        itemsIndexed(items = list,
            key = {a, item ->
                item.hashCode()
            }
        ){ _, item ->
            LocationItemCard(context, item, viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationItemCard(
    context : Context,
    weather: Weather,
    viewModel: WeatherHomeViewModel,
) {
    val state = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                viewModel.removeFromLocalDb(weather = weather)
            }
            true
        }
    )

    SwipeToDismiss(
        state = state,
        background = {
            val color = when (state.dismissDirection) {
                DismissDirection.StartToEnd -> Color.Transparent
                DismissDirection.EndToStart -> Color.Red
                null -> Color.Transparent
            }

            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = color)
                    .padding(8.dp)
            ) {
                Text(modifier = Modifier.padding(8.dp, 0.dp), text = "Delete", color = Color.White, fontSize = 16.sp ,fontFamily = FontFamily(Font(R.font.avenir_book)))
            }
        },
        dismissContent = {
            Row(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier
                    .weight(1f)
                    .padding(0.dp, 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = weather.cityName.toString(), fontSize = 24.sp ,fontFamily = FontFamily(Font(R.font.avenir_black)), modifier = Modifier.padding(0.dp, 8.dp))
                        Text(text = weather.country.toString(), fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
                    }
                }
                Row(modifier = Modifier
                    .weight(1f)
                    .padding(0.dp, 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = weather.temp.toString(), fontSize = 24.sp ,fontFamily = FontFamily(Font(R.font.avenir_black)), modifier = Modifier.padding(0.dp, 8.dp))
                            Text(modifier = Modifier
                                .align(Alignment.Bottom)
                                .padding(0.dp, 0.dp, 0.dp, 10.dp),
                                text = "\u2103", fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.avenir_black)))
                            AsyncImage(
                                model = Drawable.createFromStream(context.applicationContext.assets.open(
                                    "day/"+ weather.url?.substringAfterLast("/")
                                ), null) ,
                                contentDescription =  "icon"
                            )
                        }
                        Text(text = viewModel.getTimeFromDate(weather.time), fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
                    }
                }
            }
        },
        directions = setOf(DismissDirection.EndToStart)
    )
    Divider()
}

@Composable
private fun heading(){
    Text(modifier = Modifier.padding(0.dp,80.dp,0.dp,0.dp),
        text = "Weather App",
        fontSize = 36.sp)
}

@Composable
private fun emptyStateView(){

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(0.dp, 180.dp, 0.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier
                .height(88.dp)
                .width(88.dp),
            painter = painterResource(R.drawable.weather_mix),
            contentDescription = "Weather Mix"
        )
        Text(
            modifier = Modifier.padding(0.dp, 16.dp),
            textAlign = TextAlign.Center,
            text = "Search for a city or US/UK zip to check the weather"
        )
    }
}






