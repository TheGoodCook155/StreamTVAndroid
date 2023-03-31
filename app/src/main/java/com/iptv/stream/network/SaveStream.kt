package com.iptv.stream.network
import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import java.io.File
import java.io.InputStream
import java.net.URL


object SaveStream {

    var file: File = File("")

      @SuppressLint("SuspiciousIndentation")
      suspend  fun storeStream(channelUrl: String, fileArg: File, fileSizeCallBack: (Int) -> Unit) {

            var fileSize = 0


            val url = URL(channelUrl)

            val inputStream: InputStream = url.openStream()

            val file = File(fileArg, "stream.ts")

        if (file.exists()){
            file.delete()

        }

        if (!file.exists()){
            Log.d("stream_data", "SaveStream | storeStream: fileExists(): ${file.exists()}, CREATING NEW")

            file.createNewFile().let {
                this.file = file
            }
        }


          inputStream.use { input ->
                    file.outputStream().use { output ->
                        val buffer = ByteArray(4 * 1024)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            fileSize = returnFileSizeToInt()
                            if (fileSize > 5) {
                                fileSizeCallBack(fileSize)
                            }
                        }
                        output.flush()
                    }
                }

    }

    fun returnSaveStreamInstance(): SaveStream{
        return SaveStream
    }
    fun returnFileSizeToInt(): Int{
        val fileLengthInBytes = file.length()
        val fileLengthInMB = fileLengthInBytes.toDouble() / (1024 * 1024)
        return fileLengthInMB.toInt()
    }

}