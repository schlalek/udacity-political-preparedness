package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun InsertAll(vararg elections: Election)

    @Query("select * from election_table ORDER BY electionDay DESC")
    fun getAllElections(): LiveData<List<Election>>

    @Query("select * from election_table WHERE id = :id ORDER BY electionDay DESC")
    fun getElection(id: Int): LiveData<Election>

    @Query("select * from election_table WHERE id = :id ORDER BY electionDay DESC")
    suspend fun getElectionDirect(id: Int): Election?

    @Delete
    fun deleteElections(vararg elections: Election)

    @Query("DELETE FROM election_table")
    fun deleteAll()

    @Update
    fun updateElection(election: Election)


}