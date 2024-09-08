package kr.co.bigwalk.app.data.walk

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kr.co.bigwalk.app.data.DateConverter
import java.util.*

@Entity(tableName = "walk")
data class Walk (
    @NonNull @PrimaryKey(autoGenerate = true) var id : Int?,
    @ColumnInfo(name = "startTime") @TypeConverters(DateConverter::class) var startTime : Date?,
    @ColumnInfo(name = "endTime") @TypeConverters(DateConverter::class) var endTime : Date?,
    @ColumnInfo(name = "steps") var steps : Int
) {
    constructor():this(null, null, null,0)
}
