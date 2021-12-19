package dev.hmh.nanopol.ui.main.dashboard.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hmh.nanopol.common.ApiResource
import dev.hmh.nanopol.data.model.AvgHashRate
import dev.hmh.nanopol.data.repository.NanoPoolRepository
import dev.hmh.nanopol.ui.main.worker_detail.common.LastHashRateReportViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvgHashRateLimitedViewModel
@Inject
constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {
    sealed class AvgHashRateLimitedEvent() {
        data class Success(val data: AvgHashRate) : AvgHashRateLimitedEvent()
        data class Failure(val message: String) : AvgHashRateLimitedEvent()
        object Loading : AvgHashRateLimitedEvent()
        object Empty : AvgHashRateLimitedEvent()
    }

    private val _responseAvgHashRateLimited: MutableStateFlow<AvgHashRateLimitedEvent> =
        MutableStateFlow(AvgHashRateLimitedEvent.Empty)
    val responseAvgHashRateLimited: StateFlow<AvgHashRateLimitedEvent> = _responseAvgHashRateLimited

    fun getAvgHashRateLimited(
        address: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (address.isEmpty() || address.isBlank()) {
                _responseAvgHashRateLimited.value =
                    AvgHashRateLimitedEvent.Failure("wallet address is not found")
                return@launch
            }

            nanoPoolRepository.getAvgHashRateLimited(address).collect {
                LastHashRateReportViewModel.LastHashRateReportEvent.Loading
                when (it) {
                    is ApiResource.Error -> {
                        _responseAvgHashRateLimited.value =
                            AvgHashRateLimitedEvent.Failure("Error: ${it.message ?: "Unexpected Error"}")
                    }
                    is ApiResource.Loading -> {
                        AvgHashRateLimitedEvent.Loading
                    }
                    is ApiResource.Success -> {
                        if (it.data!!.status == true) {
                            _responseAvgHashRateLimited.value =
                                AvgHashRateLimitedEvent.Success(it.data)
                        } else {
                            _responseAvgHashRateLimited.value =
                                AvgHashRateLimitedEvent.Failure("Error: Unexpected Error")
                        }
                    }
                }
            }
        }
    }

}