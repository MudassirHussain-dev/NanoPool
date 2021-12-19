package dev.hmh.nanopol.ui.main.worker.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hmh.nanopol.common.ApiResource
import dev.hmh.nanopol.data.model.Worker
import dev.hmh.nanopol.data.repository.NanoPoolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class WorkerViewModel
@Inject
constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {
    sealed class WorkerEvent() {
        data class Success(val data: Worker) : WorkerEvent()
        data class Failure(val message: String) : WorkerEvent()
        object Empty : WorkerEvent()
        object Loading : WorkerEvent()
    }

    private val _responseWorkers: MutableStateFlow<WorkerEvent> =
        MutableStateFlow(WorkerEvent.Empty)
    val responseWorkers: StateFlow<WorkerEvent> = _responseWorkers

    fun getGeneralInfo(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (address.isBlank() || address.isEmpty()) {
                _responseWorkers.value = WorkerEvent.Failure("wallet address not found")
                return@launch
            }
            nanoPoolRepository.getWorkers(address).collect { api ->
                WorkerEvent.Loading
                when (api) {
                    is ApiResource.Error -> {
                        _responseWorkers.value = WorkerEvent.Failure("Error: ${api.message}")
                    }
                    is ApiResource.Loading -> {
                        _responseWorkers.value = WorkerEvent.Loading
                    }
                    is ApiResource.Success -> {
                        if (api.data.let { it!!.status }) {
                            _responseWorkers.value = WorkerEvent.Success(api.data!!)
                        } else {
                            _responseWorkers.value =
                                WorkerEvent.Failure("Error: Unexpected Error")
                        }
                    }
                }
            }
        }
    }
}