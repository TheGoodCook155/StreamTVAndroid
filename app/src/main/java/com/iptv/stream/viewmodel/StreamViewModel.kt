package com.iptv.stream.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iptv.stream.data.Data
import com.iptv.stream.entity.Channel
import com.iptv.stream.repository.StreamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject



@HiltViewModel
class StreamViewModel @Inject constructor(private val repository: StreamRepository, @ApplicationContext private val context: Context): ViewModel() {

    val fileLocation = context.getExternalFilesDir("Stream")
    val file = File("${context.getExternalFilesDir("Stream")}/stream.ts")
    val data = Data("source.txt",context)
    val map = data.returnData()
    lateinit var channels: List<Channel>
    val loading: MutableState<Boolean> = mutableStateOf(false)


    init {
        Log.d("stream_data", ":StreamViewModel INIT: starts")
        channels = loadChannels(map)
        Log.d("stream_data", ":StreamViewModel INIT: ends")
    }

    private fun loadChannels(map: MutableMap<String, String>): List<Channel> {

        val result = mutableListOf<Channel>()

        map.forEach{entry ->

            val channel = Channel(entry.key,entry.value)
            result.add(channel)
        }

        return result
    }


    fun storeStream(streamURL: String,fileLocation: File){
        Log.d("stream_data", "StreamViewModel | storeStream: started")

        viewModelScope.launch {
            repository.storeStream(streamURL, fileLocation!!)
        }

    }

    fun playStream(context: Context){

        Log.d("stream_data", "StreamViewModel | playStream: started")


        val packageManager = context.packageManager
        val externalFilesDir = context.getExternalFilesDir(null)
        val file = File(externalFilesDir, "Stream/stream.ts")
        Log.d("stream_data", "StreamViewModel | fileExists(): ${file.exists()}")

        val uri = FileProvider.getUriForFile(context, "${context.applicationContext.packageName}.provider", file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            Log.d("stream_data", "StreamViewModel | launch Intent Obj")

            setDataAndType(uri, "video/*")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        viewModelScope.launch {
            Log.d("stream_data", "StreamViewModel | viewModelScope.launch intent")

            loading.value = true
            delay(4000)

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(context, intent, null)
                loading.value = false
            }
        }

        Log.d("stream_data", "StreamViewModel | playStream: ends")


    }

}