package com.iptv.stream

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iptv.stream.entity.Channel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            StreamTVTheme {

                Log.d("stream_data", "onCreate: starts: ===================")

                val viewModel: StreamViewModel by viewModels()
                val context = LocalContext.current
                val channels = viewModel.channels
                val fileLocation = viewModel.fileLocation
                val file = viewModel.file
                val loading = viewModel.loading

                Log.d("stream_data", "onCreate: fileExists(): ${file.exists()}")

                val channelUrl = remember {
                    mutableStateOf("")
                }

                ListChannels(channels = channels, loading = loading.value){
                    Log.d("channelUrlCallback", "mainActivity received: ${it}")
                    channelUrl.value = it

                    viewModel.storeStream(channelUrl.value,fileLocation = fileLocation!!).also {
                        viewModel.playStream(context = context)
                    }
                }

                Log.d("channelUrlCallback", "mainActivity channelURL: ${channelUrl.value}")
                Log.d("stream_data", "onCreate: ends: ===================")

            }
        }


    }
}



@Composable
fun ListChannels(channels: List<Channel>, loading: Boolean, channelCallback: (String) -> Unit){

    Log.d("stream_data", "ListChannels: starts: ===================")
    Log.d("stream_data", "ListChannels: loading: ${loading}")


    val searchChannel = remember {
        mutableStateOf("")
    }


    if (loading == true){

        CircularProgressIndicator()

    }else{

        Column {

            SearchChannel(searchChannel){
                searchChannel.value = it
            }

            LazyColumn(modifier = Modifier.fillMaxSize()){

                items(if (searchChannel.value == "") channels else channels.filter {channel ->
                    channel.channelName.contains(searchChannel.value, ignoreCase = true)
                }){ channel ->
                    
                        ChannelView(channel){
                            Log.d("channelUrlCallback", "ListChannels received: ${it}")
                            channelCallback(it)
                        }
                }
            }

        }


    }

    Log.d("stream_data", "ListChannels: ends: ===================")


}

//@Preview
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchChannel(searchChannel: MutableState<String>, channelCallback: (String) -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember {
        FocusRequester()
    }

    val hiddenTextField = remember {
        mutableStateOf(true)
    }

    val textFieldWidth = remember {
        Animatable(initialValue = 0f)
    }

    LaunchedEffect(hiddenTextField.value) {

        if (hiddenTextField.value){

            textFieldWidth.animateTo(
                0f,
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            )
        }

        if (!hiddenTextField.value){

            textFieldWidth.animateTo(
                300f,
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            )

        }

    }


    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End) {


        Box(modifier = Modifier
            .width(textFieldWidth.value.dp)
        ) {
            OutlinedTextField(value = searchChannel.value, onValueChange = {
                searchChannel.value = it
            },
                modifier = Modifier
                    .padding(5.dp),
                trailingIcon = {

                    if (searchChannel.value.isNotBlank()){

                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear icon",
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp)
                                .padding(10.dp)
                                .clickable {
                                    searchChannel.value = ""
                                })

                    }

                }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions (
                    onDone = {
                        keyboardController?.hide()
                        channelCallback(searchChannel.value)
                    })
            )
        }

            //TX ends


        IconButton(onClick = {
            Log.d("textFieldWidth", "SearchChannel: entering onClick - textFieldWidth: ${textFieldWidth.value}")

            hiddenTextField.value = !hiddenTextField.value

            keyboardController?.show()
            focusRequester.requestFocus()

            if (!hiddenTextField.value && searchChannel.value != ""){
                channelCallback(searchChannel.value)
                searchChannel.value = ""
            }

            Log.d("textFieldWidth", "SearchChannel: exiting onClick textFieldWidth: ${textFieldWidth.value}")

        }) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon",
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .weight(2f)
                    .focusRequester(focusRequester))
        }

    }

}


@Composable
fun ChannelView(channel: Channel, urlCallback: (String) -> Unit) {
//    Log.d("stream_data", "ChannelView: starts: ")


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

//    Log.d("stream_data", "ChannelView: ends: ")

}



