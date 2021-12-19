package dev.hmh.nanopol.ui.main.payment.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hmh.nanopol.common.ApiResource
import dev.hmh.nanopol.data.model.Payment
import dev.hmh.nanopol.data.repository.NanoPoolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel
@Inject
constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {

    sealed class PaymentEvent() {
        data class Success(val payment: Payment) : PaymentEvent()
        data class Failure(val message: String) : PaymentEvent()
        object Loading : PaymentEvent()
        object Empty : PaymentEvent()
    }


    private val _responsePayment: MutableStateFlow<PaymentEvent> =
        MutableStateFlow(PaymentEvent.Empty)
    val responsePayment: StateFlow<PaymentEvent> = _responsePayment

        fun getMinerPayment(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (address.isEmpty() || address.isBlank()) {
                _responsePayment.value = PaymentEvent.Failure("Account not found")
                return@launch
            }
            nanoPoolRepository.getMinerPayment(address).collect { it ->
                PaymentEvent.Loading
                when (it) {
                    is ApiResource.Error -> {
                        _responsePayment.value = PaymentEvent.Failure("Error: ${it.message}")
                    }
                    is ApiResource.Loading -> {
                        _responsePayment.value = PaymentEvent.Loading
                    }
                    is ApiResource.Success -> {
                        if (it.data.let { data -> data?.status == true }) {
                            _responsePayment.value = PaymentEvent.Success(it.data!!)
                        } else {
                            _responsePayment.value = PaymentEvent.Failure("Error: Unexpected Error")
                        }
                    }
                }
            }
        }
    }

}