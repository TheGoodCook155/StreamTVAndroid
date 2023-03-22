package com.iptv.stream

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.iptv.stream.ui.theme.StreamTVTheme
import com.iptv.stream.viewmodel.StreamViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iptv.stream.entity.Channel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StreamTVTheme {

                Log.d("stream_data", "onCreate: starts: ")

                val viewModel: StreamViewModel by viewModels()
                val context = LocalContext.current
                val channels = viewModel.channels
                val fileLocation = viewModel.fileLocation
                val file = viewModel.file
                val loading = remember {
                    mutableStateOf(viewModel.loading)
                }

                Log.d("stream_data", "onCreate: fileExists(): ${file.exists()}")

                Log.d("stream_data", "onCreate: channels: ${channels}")

                val channelUrl = remember {
                    mutableStateOf("")
                }


                ListChannels(channels = channels, loading.value.value){
                    Log.d("channelUrlCallback", "mainActivity received: ${it}")
                    channelUrl.value = it

                    viewModel.storeStream(channelUrl.value,fileLocation = fileLocation!!).also {
                        viewModel.playStream(context = context)
                    }
                }

                Log.d("channelUrlCallback", "mainActivity channelURL: ${channelUrl.value}")
                Log.d("stream_data", "onCreate: ends: ")


            }
        }


    }
}



@Composable
fun ListChannels(channels: List<Channel>, loading: Boolean, channelCallback: (String) -> Unit){

    Log.d("stream_data", "ListChannels: starts: ")

    if (loading == true){

        CircularProgressIndicator()

    }else{

        LazyColumn(modifier = Modifier.fillMaxSize()){
            items(channels){ channel ->

                ChannelView(channel){
                    Log.d("channelUrlCallback", "ListChannels received: ${it}")

                    channelCallback(it)
                }

            }
        }

    }

    Log.d("stream_data", "ListChannels: ends: ")


}


@Composable
fun ChannelView(channel: Channel, urlCallback: (String) -> Unit) {
    Log.d("stream_data", "ChannelView: starts: ")


    Card(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .padding(5.dp)
        .clickable {
            val channelUrl = channel.channelURL
            Log.d("channelUrlCallback", "ChannelView: ${channelUrl}")
            urlCallback(channelUrl)
        },
    shape = RoundedCornerShape(10.dp),
    elevation = 5.dp
    ) {
        
        Text(text = "${channel.channelName}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 5.dp),
            textAlign = TextAlign.Center)
        
    }

    Log.d("stream_data", "ChannelView: ends: ")

}



