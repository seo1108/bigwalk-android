package kr.co.bigwalk.app.data.walk

import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import kr.co.bigwalk.app.data.DateConverter
import java.util.*

data class WalkFilter(
    var startTime : Date?,
    var endTime : Date?,
    var steps : Int
)
