package dev.hmh.nanopol.data.network.api

import dev.hmh.nanopol.data.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface NanoPoolApi {

    @GET("accountexist/{address}")
    suspend fun checkAccount(
        @Path("address") address: String
    ): Response<NanoPoolResponse>

    @GET("balance/{address}")
    suspend fun getBalance(
        @Path("address") address: String
    ): Response<Balance>

    @GET("hashrate/{address}")
    suspend fun getCurrentHashRate(
        @Path("address") address: String
    ): Response<CurrentHashRate>

    @GET("reportedhashrate/{address}")
    suspend fun getLashReportHashRate(
        @Path("address") address: String
    ): Response<LashHashRateReport>


    @GET("payments/{address}")
    suspend fun getMinerPayment(
        @Path("address") address: String
    ): Response<Payment>

    @GET("user/{address}")
    suspend fun getGeneralInfo(
        @Path("address") address: String
    ): Response<GeneralInfo>

    @GET("avghashrate/{address}")
    suspend fun getAvgHashRateLimited(
        @Path("address") address: String,
    ): Response<AvgHashRate>

    @GET("reportedhashrate/{address}/{worker}")
    suspend fun getLastReportHashRateWorker(
        @Path("address") address: String,
        @Path("worker") worker: String,
    ): Response<LashHashRateReport>

    @GET("hashrate/{address}/{worker}")
    suspend fun getWorkerCurrentHashRate(
        @Path("address") address: String,
        @Path("worker") worker: String,
    ): Response<WorkerCurrentHashRate>

    @GET("workers/{address}")
    suspend fun getWorkers(
        @Path("address") address: String
    ): Response<Worker>

    @GET("pool/hashrate")
    suspend fun getPoolHashRate(): Response<NanoPoolResponse>

    @GET("avghashrate/{address}/{worker}")
    suspend fun workerAvgHashRate(
        @Path("address") address: String,
        @Path("worker") worker: String,
    ): Response<AvgHashRate>

    @GET("pool/activeminers")
    suspend fun poolNumberMinor(): Response<NanoPoolResponse>

    @GET("usersettings/{address}")
    suspend fun getPayoutLimit(
        @Path("address") address: String
    ): Response<Payout>

    @GET("hashratechart/{address}")
    suspend fun getAccChart(
        @Path("address") address: String
    ): Response<Chart>

    @GET("hashratechart/{address}/{worker}")
    suspend fun getWorkerChart(
        @Path("address") address: String,
        @Path("worker") worker: String,
    ): Response<Chart>

    @GET("approximated_earnings/{hashrate}")
    suspend fun calculator(
        @Path("hashrate") hashrate: String
    ): Response<ApproximatedEarning>

    @GET("prices")
    suspend fun ethPrice(): Response<EthereumResponse>
}