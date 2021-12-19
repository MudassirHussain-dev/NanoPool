package dev.hmh.nanopol.data.repository

import dev.hmh.nanopol.common.ApiResource
import retrofit2.Response
import java.lang.Exception

abstract class BaseApiResponse {
    suspend fun <T> safeNanoPoolApiCall(apiCall: suspend () -> Response<T>): ApiResource<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body.let {
                    return ApiResource.Success(body)
                }
            }
            return error("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(errorMessage: String): ApiResource<T> {
        return ApiResource.Error("Api call failed $errorMessage")
    }

}