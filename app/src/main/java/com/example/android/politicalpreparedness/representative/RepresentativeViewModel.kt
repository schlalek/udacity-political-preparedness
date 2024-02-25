package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.PoliticalApp
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import timber.log.Timber

class RepresentativeViewModel(application: Application) : AndroidViewModel(application) {

    var address = MutableLiveData<Address>()

    private val _representatives: MutableLiveData<List<Representative>> = MutableLiveData()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _showToast: MutableLiveData<String?> = MutableLiveData()
    val showToast: LiveData<String?>
        get() = _showToast

    fun onToastShown() {
        _showToast.value = null
    }

    private val _showLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val showLoading: LiveData<Boolean>
        get() = _showLoading

    private fun checkIfAddressIsValid(): Boolean {
        if (address.value == null) return false

        return !(address.value!!.line1.isBlank() || address.value!!.line2.isNullOrBlank() || address.value!!.city.isBlank() || address.value!!.zip.isBlank())

    }

    fun searchRepresentatives() {
        Timber.v("search Representatives called")
        if (!checkIfAddressIsValid()) {
            Timber.v("entered address is invalid")
            _showToast.value = getApplication<PoliticalApp>().getString(R.string.error_in_address)
            return
        }
        viewModelScope.launch {
            _showLoading.value = true
            Timber.v("start getting values from API")
            val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(address.value!!.toFormattedString())
            _representatives.value =
                offices.flatMap { office -> office.getRepresentatives(officials) }
            Timber.v("api call done")
            Timber.v("received representatives: ${_representatives.value!!.size}")
            _showLoading.value = false
        }
    }

}
