package dev.hmh.nanopol.ui.main.pool.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hmh.nanopol.common.ApiResource
import dev.hmh.nanopol.data.model.NanoPoolResponse
import dev.hmh.nanopol.data.repository.NanoPoolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PoolHashRateViewModel
@Inject
constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {

    sealed class PoolHashRateEvent() {
        data class Success(val data: NanoPoolResponse) : PoolHashRateEvent()
        data class Failure(val message: String) : PoolHashRateEvent()
        object Loading : PoolHashRateEvent()
    }

    private val _responsePoolHashRate: MutableStateFlow<PoolHashRateEvent> =
        MutableStateFlow(PoolHashRateEvent.Loading)
    val responsePoolHashRate: StateFlow<PoolHashRateEvent> = _responsePoolHashRate
    fun getPoolHashRate() {
        viewModelScope.launch(Dispatchers.IO) {
            nanoPoolRepository.getPoolHashRate().collect {
                when (it) {
                    is ApiResource.Error -> {
                        _responsePoolHashRate.value =
                            PoolHashRateEvent.Failure("Error: ${it.message}?: UnexpectedError")
                    }
                    is ApiResource.Loading -> {
                        _responsePoolHashRate.value = PoolHashRateEvent.Loading
                    }
                    is ApiResource.Success -> {
                        if (it.data!!.status == true) {
                            _responsePoolHashRate.value = PoolHashRateEvent.Success(it.data)
                        } else {
                            _responsePoolHashRate.value =
                                PoolHashRateEvent.Failure("Error: Unexpected Error")
                        }
                    }
                }
            }
        }
    }
}