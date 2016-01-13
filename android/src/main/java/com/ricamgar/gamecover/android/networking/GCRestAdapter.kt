/*
 * Copyright (c) 2016. Make It Real S.L.
 */

package com.ricamgar.gamecover.android.networking

import android.content.Context
import com.google.gson.GsonBuilder
import com.ricamgar.gamecover.android.R
import com.ricamgar.gamecover.android.networking.converters.CalendarConverter
import retrofit.RestAdapter
import retrofit.converter.GsonConverter
import java.util.*

class GCRestAdapter(context: Context) {

    val gcService: GCService

    init {
        val gson = GsonBuilder().
                registerTypeAdapter(Calendar::class.java, CalendarConverter()).
                create()

        val restAdapter = RestAdapter.Builder().
                setEndpoint("https://api.parse.com/1/classes").
                setRequestInterceptor { request ->
                    request.addHeader("X-Parse-Application-ID", context.getString(R.string.parse_application_id))
                    request.addHeader("X-Parse-REST-API-Key", context.getString(R.string.parse_rest_api_key))
                }.
                setConverter(GsonConverter(gson)).setLogLevel(RestAdapter.LogLevel.BASIC).
                build()
        gcService = restAdapter.create(GCService::class.java)
    }
}
