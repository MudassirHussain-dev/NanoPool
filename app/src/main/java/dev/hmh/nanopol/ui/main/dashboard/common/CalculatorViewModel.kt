package dev.hmh.nanopol.ui.main.dashboard.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hmh.nanopol.common.ApiResource
import dev.hmh.nanopol.data.model.ApproximatedEarning
import dev.hmh.nanopol.data.repository.NanoPoolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel
@Inject
constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {

    sealed class CalculatorEvent() {
        data class Success(val data: ApproximatedEarning) : CalculatorEvent()
        data class Failure(val message: String) : CalculatorEvent()
        object Loading : CalculatorEvent()
        object Empty : CalculatorEvent()
    }

    private val _responseCalculator: MutableStateFlow<CalculatorEvent> =
        MutableStateFlow(CalculatorEvent.Empty)
    val responseCalculator: StateFlow<CalculatorEvent> = _responseCalculator

    fun calculator(hashRate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (hashRate.isBlank() || hashRate.isEmpty()) {
                _responseCalculator.value = CalculatorEvent.Failure("HashRate is empty")
                return@launch
            }
            nanoPoolRepository.calculator(hashRate).collect {
                _responseCalculator.value = CalculatorEvent.Loading
                when (it) {
                    is ApiResource.Error -> {
                        _responseCalculator.value =
                            CalculatorEvent.Failure("Error: " + it.message!!)
                    }
                    is ApiResource.Loading -> {
                        _responseCalculator.value = CalculatorEvent.Loading
                    }
                    is ApiResource.Success -> {
                        if (it.data.let { it?.status == true }) {

                            _responseCalculator.value = CalculatorEvent.Success(it.data!!)
                        } else {
                            _responseCalculator.value =
                                CalculatorEvent.Failure("Error: Unexpected Error")
                        }
                    }
                }
            }
        }
    }
}