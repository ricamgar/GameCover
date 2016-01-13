package com.ricamgar.gamecover.android.models

import android.support.annotation.IntDef
import java.util.*

/**
 * Models
 */
class ParseResults<T>(val results: List<T>)

class Image(val name: String, val url: String)

class GameItem(val title: String, val developer: String, val image: Image, val packageId: String?, val createdAt: Calendar)

class ParseList {

    @IntDef(GAMES, EMULATORS, VR)
    @Retention(AnnotationRetention.SOURCE)
    @Target(AnnotationTarget.VALUE_PARAMETER)
    annotation class ListType

    companion object {
        const val GAMES = 1L
        const val EMULATORS = 2L
        const val VR = 3L
    }
}