package com.example.prog7314_mobile_app_v2.utils

import com.google.gson.*
import java.lang.reflect.Type
import java.util.*

class FirestoreTimestampDeserializer : JsonDeserializer<Date> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Date? {
        if (json == null || json.isJsonNull) {
            return null
        }

        return try {
            val jsonObject = json.asJsonObject
            val secondsElement = jsonObject.get("seconds")
            val nanosElement = jsonObject.get("nanoseconds")

            if (secondsElement != null && !secondsElement.isJsonNull) {
                val seconds = secondsElement.asLong
                val nanos = nanosElement?.asLong ?: 0L
                Date(seconds * 1000 + nanos / 1_000_000)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
