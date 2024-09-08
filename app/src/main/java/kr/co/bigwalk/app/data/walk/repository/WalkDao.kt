package kr.co.bigwalk.app.data.walk.repository

import androidx.room.*
import kr.co.bigwalk.app.data.walk.Walk

@Dao
interface WalkDao {

    @Insert
    fun insert(walk: Walk): Long

    @Update
    fun update(walk: Walk)

    @Query("SELECT * FROM Walk GROUP BY startTime")
    fun findAll(): List<Walk>

    @Query("SELECT * FROM Walk WHERE id LIKE :id")
    fun findById(id: Long): Walk

    @Delete
    fun deleteAll(walks : List<Walk>)


}