package com.learning.chatapp

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


fun formatTimestampToIST(timestampUtcMillis: Long): String {
    val utcDate = Date(timestampUtcMillis)
    val istTimeZone = TimeZone.getTimeZone("Asia/Kolkata")
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    sdf.timeZone = istTimeZone
    return sdf.format(utcDate)
}