package dev.hmh.nanopol.ui.main.dashboard.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hmh.nanopol.common.ApiResource
import dev.hmh.nanopol.data.model.Balance
import dev.hmh.nanopol.data.repository.NanoPoolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel
@Inject constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {
    sealed class FetchBalanceEvent() {
        data class Success(val balance: Balance) : FetchBalanceEvent()
        data class Failure(val message: String) : FetchBalanceEvent()
        object Loading : FetchBalanceEvent()
        object Empty : FetchBalanceEvent()
    }

    private val _responseFetchBalance: MutableStateFlow<FetchBalanceEvent> =
        MutableStateFlow(FetchBalanceEvent.Empty)
    val responseFetchBalance: StateFlow<FetchBalanceEvent> = _responseFetchBalance

    fun getBalance(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (address.isBlank() || address.isEmpty()) {
                _responseFetchBalance.value = FetchBalanceEvent.Failure("Account not found")
                return@launch
            }
            nanoPoolRepository.getBalance(address).collect {
                _responseFetchBalance.value = FetchBalanceEvent.Loading
                when (it) {
                    is ApiResource.Error -> {
                        _responseFetchBalance.value =
                            FetchBalanceEvent.Failure("Error: " + it.message!!)
                    }
                    is ApiResource.Loading -> {
                        _responseFetchBalance.value = FetchBalanceEvent.Loading
                    }
                    is ApiResource.Success -> {
                        if (it.data.let { it?.status == true }) {

                            _responseFetchBalance.value = FetchBalanceEvent.Success(it.data!!)
                        } else {
                            _responseFetchBalance.value =
                                FetchBalanceEvent.Failure("Error: Unexpected Error")
                        }
                    }
                }
            }
        }
    }



}