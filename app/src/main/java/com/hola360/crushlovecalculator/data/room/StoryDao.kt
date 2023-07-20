package com.hola360.crushlovecalculator.data.room

import androidx.room.*
import com.hola360.crushlovecalculator.data.model.story.StoryModel

@Dao
interface StoryDao {
    @Query("select * from tblStory where event_id = (:eventId) order by date DESC")
    suspend fun getAllStory(eventId: Int): List<StoryModel>?

    @Insert
    suspend fun addStory(storyModel: StoryModel)

    @Update
    suspend fun updateStory(storyModel: StoryModel)

    @Delete
    suspend fun deleteStory(storyModel: StoryModel)

    @Query("delete from tblStory where event_id = :eventId")
    suspend fun deleteStoryByEventId(eventId: Int)

    @Query("select * from tblStory where story_id = :storyId")
    suspend fun getStory(storyId: Int): StoryModel
}