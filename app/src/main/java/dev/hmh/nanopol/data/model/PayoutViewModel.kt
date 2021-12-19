package dev.hmh.nanopol.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hmh.nanopol.common.ApiResource
import dev.hmh.nanopol.data.repository.NanoPoolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PayoutViewModel
@Inject
constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {
    sealed class PayoutEvent {
        data class Success(val data: Payout) : PayoutEvent()
        data class Failure(val message: String) : PayoutEvent()
        object Loading : PayoutEvent()
        object Empty : PayoutEvent()
    }

    private val _responsePayout: MutableStateFlow<PayoutEvent> = MutableStateFlow(PayoutEvent.Empty)
    val responsePayout: StateFlow<PayoutEvent> = _responsePayout

    fun getPayoutLimit(address:String) {

        viewModelScope.launch(Dispatchers.IO) {
            if (address.isBlank() || address.isEmpty()) {
                _responsePayout.value = PayoutEvent.Failure("Account not found")
                return@launch
            }
            nanoPoolRepository.getPayoutLimit(address).collect {
                PayoutEvent.Loading
                when(it){
                    is ApiResource.Error ->{
                        _responsePayout.value =
                            PayoutEvent.Failure("Error: ${it.message}?:Unexpected Error")
                    }
                    is ApiResource.Loading -> {
                        _responsePayout.value = PayoutEvent.Loading
                    }
                    is ApiResource.Success ->{
                        if (it.data.let { it?.status == true }) {

                            _responsePayout.value = PayoutEvent.Success(it.data!!)
                        } else {
                            _responsePayout.value = PayoutEvent.Failure("Error: Unexpected Error")
                        }
                    }
                }
            }
        }
    }
}