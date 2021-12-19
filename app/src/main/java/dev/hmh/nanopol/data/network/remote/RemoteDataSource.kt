package dev.hmh.nanopol.data.network.remote

import dev.hmh.nanopol.data.network.api.NanoPoolApi
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val nanoPoolApi: NanoPoolApi) {
    suspend fun checkAccount(address: String) = nanoPoolApi.checkAccount(address)
    suspend fun getBalance(address: String) = nanoPoolApi.getBalance(address)
    suspend fun getMinerPayment(address: String) = nanoPoolApi.getMinerPayment(address)
    suspend fun getLashReportHashRate(address: String) = nanoPoolApi.getLashReportHashRate(address)
    suspend fun getCurrentHashRate(address: String) = nanoPoolApi.getCurrentHashRate(address)
    suspend fun getGeneralInfo(address: String) = nanoPoolApi.getGeneralInfo(address)
    suspend fun getAvgHashRateLimited(address: String) = nanoPoolApi.getAvgHashRateLimited(address)
    suspend fun getLastReportHashRateWorker(address: String,worker:String) = nanoPoolApi.getLastReportHashRateWorker(address, worker)
    suspend fun getWorkerCurrentHashRate(address: String,worker:String) = nanoPoolApi.getWorkerCurrentHashRate(address, worker)
    suspend fun getWorkers(address: String)=nanoPoolApi.getWorkers(address)
    suspend fun getPoolHashRate() = nanoPoolApi.getPoolHashRate()
    suspend fun workerAvgHashRate(address: String,worker: String) = nanoPoolApi.workerAvgHashRate(address, worker)
    suspend fun poolNumberMinor()= nanoPoolApi.poolNumberMinor()
    suspend fun getPayoutLimit(address: String) = nanoPoolApi.getPayoutLimit(address)
    suspend fun getAccChart(address: String) = nanoPoolApi.getAccChart(address)
    suspend fun getWorkerChart(address: String,worker: String) = nanoPoolApi.getWorkerChart(address, worker)
    suspend fun calculator(hashRate: String) = nanoPoolApi.calculator(hashRate)
    suspend fun ethPrice()=nanoPoolApi.ethPrice()

}