package com.iptv.stream.repository

import android.util.Log
import com.iptv.stream.network.SaveStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class StreamRepository {

    val saveStream = SaveStream()

    suspend fun storeStream(channelUrl: String, file: File){
        Log.d("stream_data", "StreamRepository | storeStream: started")
        withContext(Dispatchers.IO) {
            saveStream.storeStream(channelUrl,file)
        }
    }



}