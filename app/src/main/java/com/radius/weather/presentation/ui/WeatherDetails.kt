package com.radius.weather.presentation.ui

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.radius.weather.R
import com.radius.weather.data.localdb.Weather
import com.radius.weather.domain.model.Forecastday
import com.radius.weather.domain.model.Hour
import com.radius.weather.presentation.state.WeatherDetailState
import com.radius.weather.presentation.viewmodel.WeatherDetailsViewModel

@Composable
fun WeatherDetailsMain(
    name: String? = "",
    latLng :String? = "",
    navController: NavHostController,
    context: Context = LocalContext.current,
    viewModel: WeatherDetailsViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        if (!name.isNullOrEmpty()){
            viewModel.getForecastFromApi(name)
        }

        if (!latLng.isNullOrEmpty()){
            viewModel.getForecastFromApi(latLng)
        }
    }
    val state by viewModel.getForeCast.collectAsState()
    if (state.isLoading){
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            TopBarComposable(navController, state, viewModel)
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )) {
                mainTopViewComposable(state, context)
                hourlyForecastComposable(state, viewModel, context)
                dayForecastComposable(state, viewModel, context)
                widgetComposable(state)
                DetailsComposable(state)
            }
        }
    }
}
@Composable
private fun TopBarComposable(navController: NavHostController, state: WeatherDetailState, viewModel: WeatherDetailsViewModel) {
    var addRemoveTxt by remember {
        mutableStateOf("Add")
    }
    var fontColor by remember {
        mutableStateOf(Color(R.color.black))
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier
            .weight(1f)
            .padding(0.dp, 10.dp)
            .clickable {
                // checking if Text is not equal to add then only adding to local db while navigating
                if (addRemoveTxt != "Add") {
                    val weather = Weather(
                        cityName = state.location?.name.toString(),
                        country = state.location?.country.toString(),
                        temp = state.current?.temp_c
                            ?.toInt()
                            .toString(),
                        time = state.location?.localtime_epoch?.toLong(),
                        url = state.current?.condition?.icon.toString()
                    )
                    viewModel.insertIntoLocalDb(
                        weather
                    )
                }
                navController.navigateUp()
            }
        )
        {
            Icon(painterResource(id = R.drawable.baseline_arrow_back_24), contentDescription = "Back Button")
        }
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    if (addRemoveTxt == "Add"){
                        addRemoveTxt = "Remove"
                        fontColor = Color(R.color.red)
                    }else{
                        addRemoveTxt = "Add"
                        fontColor = Color(R.color.black)
                    }
                }
            ) {
                Icon(painterResource(id = R.drawable.delete_fill0_wght400_grad0_opsz48), contentDescription = "Add", tint = fontColor, modifier = Modifier
                    .height(20.dp)
                    .width(20.dp))
                Text(text = addRemoveTxt, fontSize = 14.sp, color = fontColor)
            }
        }
    }
}
@Composable
private fun mainTopViewComposable(state: WeatherDetailState, context: Context) {
    Column(modifier = Modifier
        .padding(0.dp, 16.dp)
        .fillMaxWidth()) {
        Column(Modifier.fillMaxWidth()) {
            Text(text = state.location?.name.toString(), fontSize = 36.sp, fontFamily = FontFamily(Font(R.font.avenir_black)))
            Text(text = state.location?.country.toString(), fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(0.dp, 16.dp, 0.dp, 0.dp)
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Text(text = state.current?.temp_c?.toInt().toString(), fontSize = 76.sp, fontFamily = FontFamily(Font(R.font.avenir_black)))
                Text(modifier = Modifier.padding(0.dp,16.dp, 0.dp,0.dp),
                    text = "\u2103", fontSize = 30.sp, fontFamily = FontFamily(Font(R.font.avenir_black)))
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(state.current?.condition?.icon?.substringAfterLast("/") != null){
                    AsyncImage(
                        model = Drawable.createFromStream(context.applicationContext.assets.open(
                            "day/"+ state.current.condition.icon.substringAfterLast("/")
                        ), null) ,
                        contentDescription =  "icon",
                        modifier = Modifier
                            .height(64.dp)
                            .width(64.dp),
                    ) }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f),verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(id = R.drawable.baseline_arrow_upward_24), contentDescription = "upward")
                    Text(text = state.forecast?.forecastday?.get(0)?.day?.maxtemp_c?.toInt().toString(), fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
                    Text(modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(0.dp, 0.dp, 0.dp, 4.dp),
                        text = "\u2103", fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
                }
                Row(modifier = Modifier.padding(16.dp, 0.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(id = R.drawable.baseline_arrow_downward_24), contentDescription = "upward")
                    Text(text = state.forecast?.forecastday?.get(0)?.day?.mintemp_c?.toInt().toString(), fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
                    Text(modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(0.dp, 0.dp, 0.dp, 4.dp),
                        text = "\u2103", fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
                }
            }
            Row(modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "feels like ${state.current?.feelslike_c?.toInt()}",fontFamily = FontFamily(Font(R.font.avenir_book)))
                Text(modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(0.dp, 0.dp, 0.dp, 2.dp),
                    text = "\u2103", fontSize = 10.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
            }
        }
    }
}
@Composable
private fun hourlyForecastComposable(
    state: WeatherDetailState,
    viewModel: WeatherDetailsViewModel,
    context: Context
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ){
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = state.current?.condition?.text.toString(), fontSize = 16.sp, modifier = Modifier.padding(0.dp,0.dp, 0.dp, 12.dp), fontFamily = FontFamily(Font(R.font.avenir_book)))
            Divider(thickness = 1.dp, color = Color.LightGray)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(0.dp, 10.dp, 0.dp, 0.dp)
                    .fillMaxWidth()
            ) {
                state.forecast?.forecastday?.get(0)?.hour?.let { dat->
                    items(dat){
                        hourlyForecastItem(it, viewModel, context)
                    }
                }
            }
        }
    }
}

@Composable
private fun hourlyForecastItem(hour: Hour, viewModel: WeatherDetailsViewModel, context: Context) {
    runCatching {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = viewModel.getTimeInHour(hour.time_epoch).toString(),
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp),
                fontFamily = FontFamily(Font(R.font.avenir_book))
            )
            AsyncImage(
                model = Drawable.createFromStream(context.applicationContext.assets.open(
                    "day/"+hour.condition.icon.substringAfterLast("/")
                ), null) ,
                contentDescription =  "icon"
            )
            Row(modifier = Modifier.padding(0.dp, 5.dp, 0.dp,0.dp)) {
                Text(text = hour.temp_c.toInt().toString(), fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
                Text(modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(0.dp, 0.dp, 0.dp, 2.dp),
                    text = "\u2103", fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
            }
        }
    }.getOrElse {
        it.stackTrace
    }
}
@Composable
private fun dayForecastComposable(state: WeatherDetailState, viewModel: WeatherDetailsViewModel, context: Context) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(0.dp, 16.dp)
            .fillMaxWidth()
    )
    {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()){
                Icon(painterResource(id = R.drawable.calendar_month_fill0_wght400_grad0_opsz48), contentDescription = "calendar", modifier = Modifier
                    .height(20.dp)
                    .height(20.dp))
                Text(
                    text = "5-day forecast",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(5.dp,0.dp, 0.dp, 10.dp),
                    fontFamily = FontFamily(Font(R.font.avenir_black))
                )
            }
            state.forecast?.forecastday.let {
                if(it != null){
                    for(i in 0..it.lastIndex){
                        if (i != it.lastIndex){
                            dayForecastItem(it[i], viewModel, context, true)
                        }else{
                            dayForecastItem(it[i], viewModel, context, false)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun dayForecastItem(
    forecast: Forecastday,
    viewModel: WeatherDetailsViewModel,
    context: Context,
    visible : Boolean
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f),verticalAlignment = Alignment.CenterVertically) {
                Text(text = viewModel.parseDayOfWeek(forecast.date_epoch).toString(), fontFamily = FontFamily(Font(R.font.avenir_book)))
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = Drawable.createFromStream(context.applicationContext.assets.open(
                        "day/"+ forecast.day.condition.icon.substringAfterLast("/")
                    ), null) ,
                    contentDescription =  "icon"
                )
                Row(
                    modifier = Modifier.padding(14.dp, 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painterResource(id = R.drawable.baseline_arrow_upward_24), contentDescription = "Up" )
                    Text(text = forecast.day.maxtemp_c.toInt().toString(), fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
                    Text(modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(0.dp, 0.dp, 0.dp, 3.dp),
                        text = "\u2103", fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.avenir_book))
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(id = R.drawable.baseline_arrow_downward_24), contentDescription = "Down" )
                    Text(text = forecast.day.mintemp_c.toInt().toString(), fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
                    Text(modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(0.dp, 0.dp, 0.dp, 3.dp),
                        text = "\u2103", fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.avenir_book))
                    )
                }
            }
        }
        if (visible){
            Divider(thickness = 1.dp, color = Color.LightGray, modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 4.dp))
        }
    }
}

@Composable
private fun widgetComposable(state: WeatherDetailState) {
    Column {
        Row {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(0.dp, 0.dp, 8.dp, 0.dp)

            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row {
                        Icon(painterResource(id = R.drawable.beach_access_fill0_wght400_grad0_opsz48), contentDescription = "Umbrella",modifier = Modifier
                            .height(20.dp)
                            .width(20.dp)
                            .padding(0.dp, 0.dp, 5.dp, 0.dp))
                        Text(text = "Precipitation", fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
                    }
                    Text(text = state.current?.precip_mm.toString()+" mm", fontSize = 12.sp, modifier = Modifier.padding(0.dp, 5.dp,0.dp,0.dp), fontFamily = FontFamily(Font(R.font.avenir_black)))
                }
            }
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp, 0.dp, 0.dp, 0.dp)

            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row {
                            Icon(painterResource(id = R.drawable.air_fill0_wght400_grad0_opsz48), contentDescription = "Umbrella", modifier = Modifier
                                .height(20.dp)
                                .width(20.dp)
                                .padding(0.dp, 0.dp, 5.dp, 0.dp))
                            Text(text = "Wind", fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
                        }
                        Text(text = state.current?.wind_kph.toString()+" Kph", fontSize = 12.sp, modifier = Modifier.padding(0.dp, 5.dp,0.dp,0.dp), fontFamily = FontFamily(Font(R.font.avenir_black)))
                    }
                }
            }
        }

        Row(modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)) {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(0.dp, 0.dp, 8.dp, 0.dp)

            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row {
                        Icon(painterResource(id = R.drawable.wb_sunny_fill0_wght400_grad0_opsz48), contentDescription = "Umbrella",modifier = Modifier
                            .height(20.dp)
                            .width(20.dp)
                            .padding(0.dp, 0.dp, 5.dp, 0.dp))
                        Text(text = "UV index", fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
                    }
                    Text(text = state.current?.uv.toString(), fontSize = 12.sp, modifier = Modifier.padding(0.dp, 5.dp,0.dp,0.dp), fontFamily = FontFamily(Font(R.font.avenir_black)))
                }
            }
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp, 0.dp, 0.dp, 0.dp)

            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row {
                        Icon(painterResource(id = R.drawable.wb_sunny_fill0_wght400_grad0_opsz48), contentDescription = "Umbrella", modifier = Modifier
                            .height(20.dp)
                            .width(20.dp)
                            .padding(0.dp, 0.dp, 5.dp, 0.dp)
                        )
                        Text(text = "Sun", fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
                    }
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(id = R.drawable.wb_sunny_fill0_wght400_grad0_opsz48), contentDescription = "Umbrella", modifier = Modifier
                                .height(15.dp)
                                .width(15.dp)
                                .padding(0.dp, 0.dp, 2.dp, 0.dp)
                            )
                            Text(text = state.forecast?.forecastday?.get(0)?.astro?.sunrise.toString(), fontSize = 12.sp, modifier = Modifier.padding(0.dp, 5.dp,0.dp,0.dp).align(Alignment.CenterVertically),fontFamily = FontFamily(Font(R.font.avenir_black)))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)) {
                            Icon(painterResource(id = R.drawable.clear_night_fill0_wght400_grad0_opsz48), contentDescription = "Umbrella", modifier = Modifier
                                .height(15.dp)
                                .width(15.dp)
                                .padding(0.dp, 0.dp, 2.dp, 0.dp)
                            )
                            Text(text = state.forecast?.forecastday?.get(0)?.astro?.sunset.toString(), fontSize = 12.sp, modifier = Modifier.padding(0.dp, 5.dp,0.dp,0.dp).align(Alignment.CenterVertically),fontFamily = FontFamily(Font(R.font.avenir_black)))
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun DetailsComposable(state: WeatherDetailState) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if(!state.alers?.alert.isNullOrEmpty()){
            Row(modifier = Modifier.padding(10.dp,0.dp,0.dp,0.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(id = R.drawable.baseline_add_24), contentDescription = "Umbrella")
                Text(text = "Alerts", fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
            }

            state.alers?.alert?.forEach { alerts ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Column(modifier = Modifier.padding(10.dp)) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(Color(R.color.orange))
                            ) {
                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = alerts.category.toString(),
                                    color = Color(R.color.orange)
                                )
                            }
                        }
                        Text(text = alerts.headline.toString(), fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
                        Text(text = "21 jul, 2023 8:00 pm - 29 jul, 2023 9:00 pm", modifier = Modifier.padding(0.dp, 8.dp), fontFamily = FontFamily(Font(R.font.avenir_book)))
                        Text(text = alerts.desc.toString(), fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.avenir_book)))
                    }
                }
            }
        }
    }
}

