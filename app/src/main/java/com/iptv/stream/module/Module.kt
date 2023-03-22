package com.iptv.stream.module

import com.iptv.stream.repository.StreamRepository
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@dagger.Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    @Singleton
    fun providesRepository(): StreamRepository {
        return StreamRepository()
    }


}