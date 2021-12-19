package dev.hmh.nanopol.ui.main.worker_detail.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hmh.nanopol.common.ApiResource
import dev.hmh.nanopol.data.model.WorkerCurrentHashRate
import dev.hmh.nanopol.data.repository.NanoPoolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerCurrentHashRateViewModel
@Inject
constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {
    sealed class WorkerCurrentHashRateEvent() {
        data class Success(val data: WorkerCurrentHashRate) : WorkerCurrentHashRateEvent()
        data class Failure(val message: String) : WorkerCurrentHashRateEvent()
        object Empty : WorkerCurrentHashRateEvent()
        object Loading : WorkerCurrentHashRateEvent()
    }

    private val _responseWorkerCurrentHashRate: MutableStateFlow<WorkerCurrentHashRateEvent> =
        MutableStateFlow(WorkerCurrentHashRateEvent.Empty)
    val responseWorkerCurrentHashRate: StateFlow<WorkerCurrentHashRateEvent> =
        _responseWorkerCurrentHashRate

    fun getWorkerCurrentHashRate(
        address: String,
        worker: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (address.isEmpty() || address.isBlank()) {
                _responseWorkerCurrentHashRate.value =
                    WorkerCurrentHashRateEvent.Failure("wallet address is not found")
                return@launch
            }
            if (worker.isBlank() || worker.isEmpty()) {
                _responseWorkerCurrentHashRate.value =
                    WorkerCurrentHashRateEvent.Failure("wallet address is not found")
                return@launch
            }
            nanoPoolRepository.getWorkerCurrentHashRate(address, worker).collect {
                WorkerCurrentHashRateEvent.Loading
                when (it) {
                    is ApiResource.Error -> {
                        _responseWorkerCurrentHashRate.value =
                            WorkerCurrentHashRateEvent.Failure("Error: ${it.message ?: "Unexpected Error"}")
                    }
                    is ApiResource.Loading -> {
                        _responseWorkerCurrentHashRate.value = WorkerCurrentHashRateEvent.Loading
                    }
                    is ApiResource.Success -> {
                        if (it.data!!.status == true) {
                            _responseWorkerCurrentHashRate.value = WorkerCurrentHashRateEvent.Success(it.data)
                        } else {
                            _responseWorkerCurrentHashRate.value =
                                WorkerCurrentHashRateEvent.Failure("Error: Unexpected Error")
                        }
                    }
                }
            }
        }
    }

}