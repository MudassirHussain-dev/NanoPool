package dev.hmh.nanopol.ui.main.dashboard.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hmh.nanopol.common.ApiResource
import dev.hmh.nanopol.data.model.CurrentHashRate
import dev.hmh.nanopol.data.repository.NanoPoolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrentHashRateViewModel
@Inject
constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {

    sealed class CurrentHashRateEvent() {
        data class Success(val balance: CurrentHashRate) : CurrentHashRateEvent()
        data class Failure(val message: String) : CurrentHashRateEvent()
        object Loading : CurrentHashRateEvent()
        object Empty : CurrentHashRateEvent()
    }

    private val _responseCurrentHashRate: MutableStateFlow<CurrentHashRateEvent> =
        MutableStateFlow(CurrentHashRateEvent.Empty)
    val responseCurrentHashRate: StateFlow<CurrentHashRateEvent> = _responseCurrentHashRate

    fun getCurrentHashRate(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (address.isBlank() || address.isEmpty()) {
                _responseCurrentHashRate.value = CurrentHashRateEvent.Failure("")
                return@launch
            }
            nanoPoolRepository.getCurrentHashRate(address).collect {
                _responseCurrentHashRate.value = CurrentHashRateEvent.Loading
                when (it) {
                    is ApiResource.Error -> {
                        _responseCurrentHashRate.value =
                            CurrentHashRateEvent.Failure("Error: " + it.message!!)
                    }
                    is ApiResource.Loading -> {
                        _responseCurrentHashRate.value = CurrentHashRateEvent.Loading
                    }
                    is ApiResource.Success -> {
                        if (it.data.let { it?.status == true }) {

                            _responseCurrentHashRate.value = CurrentHashRateEvent.Success(it.data!!)
                        } else {
                            _responseCurrentHashRate.value =
                                CurrentHashRateEvent.Failure("Error: Unexpected Error")
                        }
                    }
                }
            }
        }
    }
}