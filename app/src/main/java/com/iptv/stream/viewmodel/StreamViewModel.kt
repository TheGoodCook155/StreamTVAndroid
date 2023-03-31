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
import com.iptv.stream.data.DataWrapper
import com.iptv.stream.entity.Channel
import com.iptv.stream.repository.StreamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject



@HiltViewModel
class StreamViewModel @Inject constructor(private val repository: StreamRepository, @ApplicationContext private val context: Context): ViewModel() {

    val fileLocation = context.getExternalFilesDir("Stream")
    val file = File("${context.getExternalFilesDir("Stream")}/stream.ts")
    private val dataWrapper : MutableState<DataWrapper<Boolean,Int>> = mutableStateOf(repository.dataWrapper)
    lateinit var  bufferJob: Job
    var launchIntentJob: Job? = null
    val data = Data("source.txt",context)
    val map = data.returnData()
    lateinit var channels: List<Channel>
    val loading: MutableState<Boolean> = mutableStateOf(false)

    init {

        Log.d("stream_data", ":StreamViewModel INIT: starts===================")
        loading.value = false
        channels = loadChannels(map)
        Log.d("stream_data", ":StreamViewModel INIT: dataWrapper: ${dataWrapper.value}, isLoading: ${dataWrapper.value.isLoading}, fileSize: ${dataWrapper.value.fileSize}")

        Log.d("stream_data", ":StreamViewModel INIT: ends===================")

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
        Log.d("stream_data", "StreamViewModel | storeStream: started===================")

        viewModelScope.launch {

            repository.storeStream(streamURL, fileLocation!!)

            Log.d("stream_data", "StreamViewModel | storeStream: dataWrapper: ${dataWrapper.value}, isLoading: ${dataWrapper.value.isLoading}, fileSize: ${dataWrapper.value.fileSize}")
        }

        dataWrapper.value = repository.dataWrapper
        loading.value = dataWrapper.value.isLoading

        Log.d("stream_data", "StreamViewModel | storeStream: ENDS===================")

    }

    fun playStream(context: Context){

        Log.d("stream_data", "StreamViewModel | playStream: started===================")


        val packageManager = context.packageManager
        val externalFilesDir = context.getExternalFilesDir(null)
        val file = File(externalFilesDir, "Stream/stream.ts")

        val uri = FileProvider.getUriForFile(context, "${context.applicationContext.packageName}.provider", file)

        val intent = Intent(Intent.ACTION_VIEW).apply {

            setDataAndType(uri, "video/*")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

            bufferJob = viewModelScope.launch{
            Log.d("stream_data", "StreamViewModel | playStream: | viewModelScope.job")

            Log.d("stream_data", "StreamViewModel | playStream: Nested Launch started: entering do - while loop")

            do {
                delay(500)
                dataWrapper.value.isLoading = true
                loading.value = true
            }while (dataWrapper.value.fileSize < 5)

            Log.d("stream_data", "StreamViewModel | playStream: exiting do - while loop")

        }

        if (dataWrapper.value.fileSize >= 5){
            bufferJob.cancel()
        }

        launchIntentJob = viewModelScope.launch(Dispatchers.Main) {

            Log.d("stream_data", "StreamViewModel |  viewModelScope.launch: job.join()")

            bufferJob.join()

            Log.d("stream_data", "StreamViewModel |  viewModelScope.launch: job.join() completed")

            Log.d("stream_data", "StreamViewModel |  viewModelScope.launch: job.join() completed,  dataWrapper.value.isLoading = ${ dataWrapper.value.isLoading}, repository.dataWrapper.fileSize: ${repository.dataWrapper.fileSize} starting intent")

            if (intent.resolveActivity(packageManager) != null) {
                loading.value = false
                startActivity(context, intent, null)
            }

        }

        Log.d("stream_data", "StreamViewModel | playStream: ends===================")

    }


}