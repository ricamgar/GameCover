package com.ricamgar.gamecover.android.networking.converters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException

import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Converts the RFC3339 date string to calendar
 */
class CalendarConverter : JsonDeserializer<Calendar> {

    private val RCF3339_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Calendar {
        val timeString = json.asString
        val calendar = Calendar.getInstance()
        try {
            calendar.time = RCF3339_FORMAT.parse(timeString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return calendar
    }
}
