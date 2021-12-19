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
class PoolNumberMinorViewModel
@Inject
constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {

    sealed class PoolNumberMinorEvent() {
        data class Success(val data: NanoPoolResponse) : PoolNumberMinorEvent()
        data class Failure(val message: String) : PoolNumberMinorEvent()
        object Loading : PoolNumberMinorEvent()

    }

    private val _responsePoolNumberMinor: MutableStateFlow<PoolNumberMinorEvent> =
        MutableStateFlow(PoolNumberMinorEvent.Loading)
    val responsePoolNumberMinor: StateFlow<PoolNumberMinorEvent> = _responsePoolNumberMinor
    fun getPoolNumberMinor() {
        viewModelScope.launch(Dispatchers.IO) {
            nanoPoolRepository.poolNumberMinor().collect {
                when (it) {
                    is ApiResource.Error -> {
                        _responsePoolNumberMinor.value =
                            PoolNumberMinorEvent.Failure("Error: ${it.message}?: UnexpectedError")
                    }
                    is ApiResource.Loading -> {
                        PoolNumberMinorEvent.Loading
                    }
                    is ApiResource.Success -> {
                        if (it.data!!.status == true) {
                            _responsePoolNumberMinor.value = PoolNumberMinorEvent.Success(it.data)
                        } else {
                            _responsePoolNumberMinor.value =
                                PoolNumberMinorEvent.Failure("Error: Unexpected Error")
                        }
                    }
                }
            }
        }
    }
}