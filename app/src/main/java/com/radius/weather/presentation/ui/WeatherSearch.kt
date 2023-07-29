package com.radius.weather.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun weatherSearch(){
    var text by remember {
        mutableStateOf("")
    }
    var active by remember {
        mutableStateOf(false)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        heading()
        Scaffold {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 16.dp, 0.dp, 0.dp),
                query = text,
                onQueryChange = {
                    text = it
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

            }

            emptyStateView()
        }
    }
}

@Composable
private fun heading(){
    Text(modifier = Modifier.padding(0.dp,80.dp,0.dp,0.dp),
        text = "Weather App",
        fontSize = 36.sp)
}

@Composable
private fun emptyStateView(){
    Column(modifier =  Modifier.fillMaxWidth().fillMaxHeight().padding(0.dp, 120.dp, 0.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            modifier = Modifier.height(88.dp).width(88.dp),
            contentDescription = "Cloud Icon")
        Text(
            text = "Search for a city or US/UK zip to check the weather"
        )
    }
}
