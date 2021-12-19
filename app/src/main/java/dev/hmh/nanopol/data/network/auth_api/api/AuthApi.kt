package dev.hmh.nanopol.data.network.auth_api.api

import dev.hmh.nanopol.data.network.auth_api.response.ApiResponse
import dev.hmh.nanopol.data.network.auth_api.response.LoginResponse
import dev.hmh.nanopol.data.network.auth_api.response.Wallet
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi {

    @FormUrlEncoded
    @POST("user_register.php")
    fun register(
        @Field("username") username: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("date") date: String,
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("add_wallet.php")
    fun addWallet(
        @Field("walletId") walletId: String,
        @Field("wallet_name") wallet_name: String,
        @Field("username") username: String,
        @Field("date") date: String,
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("fetch_wallets.php")
    fun getWallets(
        @Field("username") username: String
    ): Call<Wallet>

    @FormUrlEncoded
    @POST("update_wallet.php")
    fun updateWallet(
        @Field("walletId") walletId: String,
        @Field("wallet_name") wallet_name: String,
        @Field("Id") Id: String,
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("delete_wallet.php")
    fun deleteWallet(
        @Field("Id") Id: String
    ): Call<ApiResponse>


    companion object {
        fun create(): AuthApi {

            val BASE_URL = "https://thewebcoders.com/nanopool/"
            val retrofit =
                Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build()
            return retrofit.create(AuthApi::class.java)
        }


    }

}