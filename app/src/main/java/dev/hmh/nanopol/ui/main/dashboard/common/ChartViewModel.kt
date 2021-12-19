package dev.hmh.nanopol.ui.main.dashboard.common

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
class ChartViewModel
@Inject
constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {
    sealed class ChartEvent {
        data class Success(val data: Chart) : ChartEvent()
        data class Failure(val message: String) : ChartEvent()
        object Loading : ChartEvent()
        object Empty : ChartEvent()
    }

    private val _responseChart: MutableStateFlow<ChartEvent> = MutableStateFlow(ChartEvent.Empty)
    val responseChart: StateFlow<ChartEvent> = _responseChart
    fun getAccChart(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (address.isBlank() || address.isEmpty()) {
                _responseChart.value = ChartEvent.Failure("Account not found")
                return@launch
            }
            nanoPoolRepository.getAccChart(address).collect {
                ChartEvent.Loading
                when (it) {
                    is ApiResource.Error -> {
                        _responseChart.value =
                            ChartEvent.Failure("Error: ${it.message}?:Unexpected Error")
                    }
                    is ApiResource.Loading -> {
                        _responseChart.value = ChartEvent.Loading
                    }
                    is ApiResource.Success -> {
                        if (it.data.let { it?.status == true }) {

                            _responseChart.value = ChartEvent.Success(it.data!!)
                        } else {
                            _responseChart.value =
                                ChartEvent.Failure("Error: Unexpected Error")
                        }
                    }
                }
            }
        }
    }
}