package com.iptv.stream.entity

class Channel(
    val channelName: String,
    val channelURL: String
){

    override fun toString(): String {
        return "Channel(channelName='$channelName', channelURL='$channelURL')"
    }
}