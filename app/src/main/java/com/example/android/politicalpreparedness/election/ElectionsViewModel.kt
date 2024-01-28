package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ElectionDatabase.getInstance(application)


    //TODO: Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections


    //TODO: Create live data val for saved elections
    val savedElections: LiveData<List<Election>> = database.electionDao.getAllElections()

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    suspend fun getUpcomingElections() {
        val elections = CivicsApi.retrofitService.getElections()
        _upcomingElections.value = elections.elections
    }

    init {
        viewModelScope.launch {
            getUpcomingElections()
        }
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info
    fun onElectionClicked(it: Election) {
        TODO("Not yet implemented")
    }

}