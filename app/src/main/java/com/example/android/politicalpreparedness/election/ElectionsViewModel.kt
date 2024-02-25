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

class ElectionsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ElectionDatabase.getInstance(application)


    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    val savedElections: LiveData<List<Election>> = database.electionDao.getAllElections()

    private val _navigateToVoterInfo = MutableLiveData<Election?>(null)
    val navigateToVoterInfo: LiveData<Election?>
        get() = _navigateToVoterInfo

    suspend fun getUpcomingElections() {
        val elections = CivicsApi.retrofitService.getElections()
        _upcomingElections.value = elections.elections
    }

    init {
        viewModelScope.launch {
            getUpcomingElections()
        }
    }

    fun onElectionClicked(it: Election) {
        _navigateToVoterInfo.value = it
    }

    fun onNavDone() {
        _navigateToVoterInfo.value = null
    }

}