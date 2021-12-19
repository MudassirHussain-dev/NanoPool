package dev.hmh.nanopol.ui.main.worker_detail.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hmh.nanopol.common.ApiResource
import dev.hmh.nanopol.data.model.LashHashRateReport
import dev.hmh.nanopol.data.repository.NanoPoolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LastHashRateReportViewModel
@Inject
constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {

    sealed class LastHashRateReportEvent() {
        data class Success(val data: LashHashRateReport) : LastHashRateReportEvent()
        data class Failure(val message: String) : LastHashRateReportEvent()
        object Empty : LastHashRateReportEvent()
        object Loading : LastHashRateReportEvent()
    }

    private val _responseLastHashRateReport: MutableStateFlow<LastHashRateReportEvent> =
        MutableStateFlow(LastHashRateReportEvent.Empty)
    val responseLastHashRateReport: StateFlow<LastHashRateReportEvent> = _responseLastHashRateReport

    fun getLastHashRateReport(
        address: String,
        worker: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (address.isEmpty() || address.isBlank()) {
                _responseLastHashRateReport.value =
                    LastHashRateReportEvent.Failure("wallet address is not found")
                return@launch
            }
            if (worker.isBlank() || worker.isEmpty()) {
                _responseLastHashRateReport.value =
                    LastHashRateReportEvent.Failure("Worker id not found")
                return@launch
            }
            nanoPoolRepository.getLastReportHashRateWorker(address, worker).collect {
                LastHashRateReportEvent.Loading
                when (it) {
                    is ApiResource.Error -> {
                        _responseLastHashRateReport.value =
                            LastHashRateReportEvent.Failure("Error: ${it.message ?: "Unexpected Error"}")
                    }
                    is ApiResource.Loading -> {
                        _responseLastHashRateReport.value = LastHashRateReportEvent.Loading
                    }
                    is ApiResource.Success -> {
                        if (it.data!!.status == true) {
                            _responseLastHashRateReport.value =
                                LastHashRateReportEvent.Success(it.data)
                        } else {
                            _responseLastHashRateReport.value =
                                LastHashRateReportEvent.Failure("Error: Unexpected Error")
                        }
                    }
                }
            }
        }
    }
}