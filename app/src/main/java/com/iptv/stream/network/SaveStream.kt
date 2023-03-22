package com.iptv.stream.network
import android.annotation.SuppressLint
import android.util.Log
import java.io.File
import java.io.InputStream
import java.net.URL


class SaveStream{
        ///storage/emulated/0/Android/data/com.iptv.divatv/files/Stream/stream.ts

      @SuppressLint("SuspiciousIndentation")
      suspend  fun storeStream(channelUrl: String, fileArg: File) {

          Log.d("stream_data", "SaveStream | storeStream: fileArg Path: ${fileArg.absolutePath}")

            val url = URL(channelUrl)

            val inputStream: InputStream = url.openStream()

            val file = File(fileArg, "stream.ts")

          Log.d("stream_data", "SaveStream | storeStream: fileExists(): ${file.exists()}")

        if (file.exists()){
            file.delete()
            Log.d("stream_data", "SaveStream | storeStream: fileDeleted() -> Exists: ${file.exists()}")

        }

        if (!file.exists()){
            Log.d("stream_data", "SaveStream | storeStream: fileExists(): ${file.exists()}, CREATING NEW")

            file.createNewFile()
        }


          inputStream.use { input ->
                    file.outputStream().use { output ->
                        val buffer = ByteArray(4 * 1024)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
//                            Log.d("Bytes read", "Bytes read: $bytesRead |")
                        }
                        output.flush()
                    }
                }

            }

}