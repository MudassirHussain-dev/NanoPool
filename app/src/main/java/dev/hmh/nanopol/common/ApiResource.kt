package dev.hmh.nanopol.common

sealed class ApiResource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : ApiResource<T>(data = data)
    class Error<T>(message: String?,data: T?=null):ApiResource<T>(message=message,data = data)
    class Loading<T> : ApiResource<T>()
}
