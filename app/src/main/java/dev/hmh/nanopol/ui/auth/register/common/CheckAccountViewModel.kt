package dev.hmh.nanopol.ui.auth.register.common

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
class CheckAccountViewModel
@Inject
constructor(
    private val nanoPoolRepository: NanoPoolRepository
) : ViewModel() {

    sealed class CheckAccountEvent() {
        data class Success(val data: NanoPoolResponse) : CheckAccountEvent()
        data class Failure(val message: String) : CheckAccountEvent()
        object Empty : CheckAccountEvent()
        object Loading : CheckAccountEvent()
    }

    private val _responseCheckAccount: MutableStateFlow<CheckAccountEvent> =
        MutableStateFlow(CheckAccountEvent.Empty)
    val responseCheckAccount: StateFlow<CheckAccountEvent> = _responseCheckAccount

    fun checkAccount(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (address.isBlank() || address.isEmpty()) {
                _responseCheckAccount.value = CheckAccountEvent.Failure("wallet is empty")
                return@launch
            }
            nanoPoolRepository.checkAccount(address).collect {
                _responseCheckAccount.value = CheckAccountEvent.Loading
                when (it) {
                    is ApiResource.Error -> {
                        _responseCheckAccount.value =
                            CheckAccountEvent.Failure("Error: " + it.message!!)
                    }
                    is ApiResource.Loading -> {
                        _responseCheckAccount.value = CheckAccountEvent.Loading
                    }
                    is ApiResource.Success -> {
                        if (it.data.let { it?.status == true }) {

                            _responseCheckAccount.value = CheckAccountEvent.Success(it.data!!)
                        } else {
                            _responseCheckAccount.value =
                                CheckAccountEvent.Failure("Account not found")
                        }
                    }
                }
            }
        }
    }
}