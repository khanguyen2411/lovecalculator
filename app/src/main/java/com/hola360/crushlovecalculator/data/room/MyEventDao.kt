package com.hola360.crushlovecalculator.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.hola360.crushlovecalculator.data.model.event.EventModel

@Dao
interface MyEventDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(myEventModel: EventModel)

    @Query("Select * from tblMyEvent order by created_date DESC")
    suspend fun getAll(): List<EventModel>

    @Query("UPDATE tblMyEvent SET thumb = :thumb  WHERE event_id = :id")
    suspend fun updateBg(id: Int, thumb: String)

    @Query("UPDATE tblMyEvent SET title = :title, date = :dateDay, calculation_type= :type  WHERE event_id = :id")
    suspend fun update(id: Int, title: String, dateDay: Long, type: Int)

    @Query("Delete from tblMyEvent where event_id = :eventId ")
    suspend fun delete(eventId: Int)

    @Query("select * from tblMyEvent where created_date = :createdDate")
    suspend fun getMyEventByDate(createdDate: Long) : EventModel

    @Query("select * from tblMyEvent where event_id = :id")
    suspend fun getMyEventById(id: Int) : EventModel
}