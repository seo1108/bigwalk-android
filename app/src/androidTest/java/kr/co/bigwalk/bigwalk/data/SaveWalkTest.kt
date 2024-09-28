package kr.co.bigwalk.app.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kr.co.bigwalk.app.data.AppDatabase
import kr.co.bigwalk.app.data.walk.Walk
import kr.co.bigwalk.app.data.walk.repository.WalkDao
import org.apache.commons.lang3.time.DateUtils
import org.hamcrest.core.IsEqual.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class SaveWalkTest {

    private var db: AppDatabase? = null
    private var walkDao: WalkDao? = null

    @Before
    fun createAppDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        if (db == null) return
        walkDao = db?.walkDao()!!
    }

    @Test
    @Throws(Exception::class)
    fun saveWalkTest() {
        val endTime = Date()
        val startTime = DateUtils.addHours(endTime, -1)
        val walk = Walk()
        walk.startTime = startTime
        walk.endTime = endTime
        walk.steps = 5000
        val id = walkDao?.insert(walk)
        val savedWalk = walkDao?.findById(id!!)
        assertThat(savedWalk?.steps, equalTo(walk.steps))
        assertThat(walkDao?.findAll()?.size, equalTo(1))
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        db?.close()
    }

}