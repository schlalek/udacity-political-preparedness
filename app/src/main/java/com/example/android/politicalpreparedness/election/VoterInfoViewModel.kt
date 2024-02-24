package com.example.android.politicalpreparedness.election

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class VoterInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ElectionDatabase.getInstance(application)

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _isSaved = MutableLiveData<Boolean>(false)
    val isSaved: LiveData<Boolean>
        get() = _isSaved

    private val _urlToOpen = MutableLiveData<Uri?>(null)
    val urlToOpen: LiveData<Uri?>
        get() = _urlToOpen

    fun onUrlOpened() {
        _urlToOpen.value = null
    }

    fun openPollingLocationsURL() {
        val url = voterInfo.value?.pollingLocations
        _urlToOpen.value = Uri.parse(url)
    }

    fun openBallotInfoURL() {
        val url = _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
        _urlToOpen.value = Uri.parse(url)
    }

    private fun saveElection() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_voterInfo.value != null) {
                database.electionDao.InsertAll(_voterInfo.value!!.election)
                _isSaved.postValue(true)
            }
        }
    }

    private fun removeElection() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_voterInfo.value != null) {
                database.electionDao.deleteElections(_voterInfo.value!!.election)
                _isSaved.postValue(false)
            }
        }
    }

    fun followUnfollowButtonClicked() {
        if (_isSaved.value == true) removeElection() else saveElection()
    }

    fun getVoterInfo(electionId: Int, division: Division) {
        viewModelScope.launch {
            var address = division.country
            if (division.state.isNotEmpty()) {
                address += " - " + division.state
            }
            _voterInfo.value = CivicsApi.retrofitService.getVoterInfo(address, electionId)

            val election = database.electionDao.getElectionDirect(electionId)
            _isSaved.value = election != null
            Timber.i("Result from database with ID $electionId: ${election?.toString()}")
        }

    }

}