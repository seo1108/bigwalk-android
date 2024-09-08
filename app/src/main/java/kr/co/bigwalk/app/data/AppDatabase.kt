package kr.co.bigwalk.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kr.co.bigwalk.app.data.feedComment.dto.CommentDao
import kr.co.bigwalk.app.data.feedComment.dto.FeedCommentResponse
import kr.co.bigwalk.app.data.organization.OrganizationTypeConverter
import kr.co.bigwalk.app.data.walk.Walk
import kr.co.bigwalk.app.data.walk.repository.WalkDao

@Database(entities = [Walk::class, FeedCommentResponse::class], version = 3, exportSchema = false)
@TypeConverters(DateConverter::class, OrganizationTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walkDao(): WalkDao
    abstract fun commentDao(): CommentDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        /* kotlin invoke operator
         * 메소드 호출없이 클래스 호출만으로 동작하는 operator 오버로딩의 한 예
         * AppDatabase.invoke(context) -> AppDatabase(context)
         * AppDatabase 인스턴스가 없으면 새로 생성 */
        operator fun invoke(context: Context): AppDatabase {
            synchronized(AppDatabase::class.java) {
                return instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "bigwalk"
        ).fallbackToDestructiveMigration().build()

    }

}