/*
 * Copyright (c) 2015. Make It Real S.L.
 */

package com.ricamgar.gamecover.android.networking

import com.ricamgar.gamecover.android.models.GameItem
import com.ricamgar.gamecover.android.models.ParseResults

import retrofit.http.GET
import retrofit.http.Query
import rx.Observable

interface GCService {

    @GET("/Games")
    fun getGamesList(@Query(value = "where", encodeValue = false) where: String,
                     @Query(value = "limit", encodeValue = false) limit: Int): Observable<ParseResults<GameItem>>
}
