package com.iptv.stream.repository

import android.util.Log
import com.iptv.stream.data.DataWrapper
import com.iptv.stream.network.SaveStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class StreamRepository {

    private val saveStream = SaveStream.returnSaveStreamInstance()

    var dataWrapper : DataWrapper<Boolean,Int> = DataWrapper(false,0)

    suspend fun storeStream(channelUrl: String, file: File){

        Log.d("stream_data", "StreamRepository | storeStream: started")

        dataWrapper.fileSize = 0

        Log.d("stream_data", "StreamRepository | storeStream: dataWrapper: ${dataWrapper}")

        withContext(Dispatchers.IO) {

            Log.d("stream_data", "StreamRepository | storeStream: dataWrapper.isLoading = ${dataWrapper.isLoading}")

            saveStream.storeStream(channelUrl,file){
                dataWrapper.fileSize = it
            }

            if (dataWrapper.fileSize >= 5){

                dataWrapper.isLoading = false

            }else{

                dataWrapper.isLoading = true

            }

        }
        Log.d("stream_data", "StreamRepository | storeStream: dataWrapper.isLoading before ends = ${dataWrapper.isLoading}")

        Log.d("stream_data", "StreamRepository | storeStream: ENDS")
    }

}