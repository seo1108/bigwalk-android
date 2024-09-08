package kr.co.bigwalk.app.data

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

object DateConverter {

    private val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
    @TypeConverter
    @JvmStatic
    fun toDate(timeFormat: String?): Date? {
        return timeFormat?.let { formatter.parse(timeFormat) }
    }

    @TypeConverter
    @JvmStatic
    fun toTimeFormat(date: Date?): String? {
        return date?.let { formatter.format(date) }
    }
}