package dev.hmh.nanopol.ui.main.worker_detail.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hmh.nanopol.common.ApiResource
import dev.hmh.nanopol.data.model.Chart
import dev.hmh.nanopol.data.repository.NanoPoolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerChartViewModel
@Inject
constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {
    sealed class WorkerChartEvent {
        data class Success(val data: Chart) : WorkerChartEvent()
        data class Failure(val message: String) : WorkerChartEvent()
        object Loading : WorkerChartEvent()
        object Empty : WorkerChartEvent()
    }

    private val _responseWorkerChart: MutableStateFlow<WorkerChartEvent> =
        MutableStateFlow(WorkerChartEvent.Empty)
    val responseWorkerChart: StateFlow<WorkerChartEvent> = _responseWorkerChart
    fun getWorkerChart(address: String, worker: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (address.isBlank() || address.isEmpty()) {
                _responseWorkerChart.value = WorkerChartEvent.Failure("Account not found")
                return@launch
            }
            if (worker.isBlank() || worker.isEmpty()) {
                _responseWorkerChart.value = WorkerChartEvent.Failure("worker not found")
                return@launch
            }
            nanoPoolRepository.getWorkerChart(address,worker).collect {
                WorkerChartEvent.Loading
                when (it) {
                    is ApiResource.Error -> {
                        _responseWorkerChart.value =
                            WorkerChartEvent.Failure("Error: ${it.message}?:Unexpected Error")
                    }
                    is ApiResource.Loading -> {
                        _responseWorkerChart.value = WorkerChartEvent.Loading
                    }
                    is ApiResource.Success -> {
                        if (it.data.let { it?.status == true }) {

                            _responseWorkerChart.value = WorkerChartEvent.Success(it.data!!)
                        } else {
                            _responseWorkerChart.value =
                                WorkerChartEvent.Failure("Error: Unexpected Error")
                        }
                    }
                }
            }
        }
    }
}