package com.iptv.stream.data

import android.content.Context
import android.util.Log
import java.io.InputStream

class Data(
    private val file: String,
    private val context: Context
) {

    fun returnData(): MutableMap<String,String>{

        val lines = readAsset(context,file)

        val channels: MutableMap<String, String> = mutableMapOf()

        var channelName = "";
        var channelURL = "";

        var isPair = false;

        for (channelLine in lines){

            if (channelLine.contains("#EXTINF:-1")){

                channelName = channelLine.replace("#EXTINF:-1,","").trim();
                if (isPair == true){
                    isPair = false;
                }

            }
            if (channelLine.contains("http")){
                channelURL = channelLine;
                if (isPair == false){
                    isPair = true;
                }

            }

            if (isPair == true){
                channels.put(channelName,channelURL);
            }

        }

        return channels

    }

    private fun readAsset(context: Context, fileName: String): MutableList<String> {

        val listLines: MutableList<String> = mutableListOf()
        val inputStream: InputStream = context.assets.open(fileName)

        val line = inputStream.bufferedReader().readLines().forEach {line ->
            listLines.add(line)
        }

        Log.d("DATA", "readAsset: =================================")

        return listLines
    }

}
