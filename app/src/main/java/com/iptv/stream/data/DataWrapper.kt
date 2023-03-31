package com.iptv.stream.data

data class DataWrapper<LOADING: Boolean, SIZE: Int>(
    var isLoading: LOADING,
    var fileSize: SIZE,
)
