package dev.hmh.nanopol.ui.main.dashboard.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hmh.nanopol.common.ApiResource
import dev.hmh.nanopol.data.model.EthereumResponse
import dev.hmh.nanopol.data.repository.NanoPoolRepository
import dev.hmh.nanopol.ui.main.worker_detail.common.LastHashRateReportViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EthereumPriceViewModel
@Inject
constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {

    sealed class EthereumPriceEvent() {
        data class Success(val data: EthereumResponse) : EthereumPriceEvent()
        data class Failure(val message: String) : EthereumPriceEvent()
        object Loading : EthereumPriceEvent()
    }

    private val _responseEthereum: MutableStateFlow<EthereumPriceEvent> =
        MutableStateFlow(EthereumPriceEvent.Loading)
    val responseEthereum: StateFlow<EthereumPriceEvent> = _responseEthereum

    fun ethPrice() {
        viewModelScope.launch(Dispatchers.IO) {

            nanoPoolRepository.ethPrice().collect {
                LastHashRateReportViewModel.LastHashRateReportEvent.Loading
                when (it) {
                    is ApiResource.Error -> {
                        _responseEthereum.value =
                            EthereumPriceEvent.Failure("Error: ${it.message ?: "Unexpected Error"}")
                    }
                    is ApiResource.Loading -> {
                        AvgHashRateLimitedViewModel.AvgHashRateLimitedEvent.Loading
                    }
                    is ApiResource.Success -> {
                        if (it.data!!.status) {
                            _responseEthereum.value =
                                EthereumPriceEvent.Success(it.data)
                        } else {
                            _responseEthereum.value =
                                EthereumPriceEvent.Failure("Error: Unexpected Error")
                        }
                    }

                }
            }
        }
    }
}