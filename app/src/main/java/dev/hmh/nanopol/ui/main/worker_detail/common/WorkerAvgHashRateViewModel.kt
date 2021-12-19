package dev.hmh.nanopol.ui.main.worker_detail.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hmh.nanopol.common.ApiResource
import dev.hmh.nanopol.data.model.AvgHashRate
import dev.hmh.nanopol.data.repository.NanoPoolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerAvgHashRateViewModel
@Inject
constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {

    sealed class WorkerAvgHashRateEvent() {
        data class Success(val data: AvgHashRate) : WorkerAvgHashRateEvent()
        data class Failure(val message: String) : WorkerAvgHashRateEvent()
        object Empty : WorkerAvgHashRateEvent()
        object Loading : WorkerAvgHashRateEvent()
    }

    private val _responseWorkerAvgHashRate: MutableStateFlow<WorkerAvgHashRateEvent> =
        MutableStateFlow(WorkerAvgHashRateEvent.Empty)
    val responseWorkerAvgHashRate: StateFlow<WorkerAvgHashRateEvent> =
        _responseWorkerAvgHashRate

    fun workerAvgHashRate(
        address: String,
        worker: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (address.isEmpty() || address.isBlank()) {
                _responseWorkerAvgHashRate.value =
                    WorkerAvgHashRateEvent.Failure("wallet address is not found")
                return@launch
            }
            if (worker.isBlank() || worker.isEmpty()) {
                _responseWorkerAvgHashRate.value =
                    WorkerAvgHashRateEvent.Failure("Worker id not found")
                return@launch
            }
            nanoPoolRepository.workerAvgHashRate(address, worker).collect {
                LastHashRateReportViewModel.LastHashRateReportEvent.Loading
                when (it) {
                    is ApiResource.Error -> {
                        _responseWorkerAvgHashRate.value =
                            WorkerAvgHashRateEvent.Failure("Error: ${it.message ?: "Unexpected Error"}")
                    }
                    is ApiResource.Loading -> {
                        WorkerAvgHashRateEvent.Loading
                    }
                    is ApiResource.Success -> {
                        if (it.data!!.status == true) {
                            _responseWorkerAvgHashRate.value =
                                WorkerAvgHashRateEvent.Success(it.data)
                        } else {
                            _responseWorkerAvgHashRate.value =
                                WorkerAvgHashRateEvent.Failure("Error: Unexpected Error")
                        }
                    }
                }
            }
        }
    }
}