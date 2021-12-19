package dev.hmh.nanopol.data.repository

import dev.hmh.nanopol.common.ApiResource
import dev.hmh.nanopol.data.model.*
import dev.hmh.nanopol.data.network.remote.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NanoPoolRepository
@Inject
constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {

    suspend fun checkAccount(address: String): Flow<ApiResource<NanoPoolResponse>> = flow {
        emit(safeNanoPoolApiCall { remoteDataSource.checkAccount(address) })
    }.flowOn(Dispatchers.IO)

    suspend fun getBalance(address: String): Flow<ApiResource<Balance>> = flow {
        emit(safeNanoPoolApiCall { remoteDataSource.getBalance(address) })
    }.flowOn(Dispatchers.IO)

    suspend fun getMinerPayment(address: String): Flow<ApiResource<Payment>> = flow {
        emit(safeNanoPoolApiCall { remoteDataSource.getMinerPayment(address) })
    }.flowOn(Dispatchers.IO)

    suspend fun getLashReportHashRate(address: String): Flow<ApiResource<LashHashRateReport>> =
        flow {
            emit(safeNanoPoolApiCall { remoteDataSource.getLashReportHashRate(address) })
        }.flowOn(Dispatchers.IO)

    suspend fun getCurrentHashRate(address: String): Flow<ApiResource<CurrentHashRate>> =
        flow<ApiResource<CurrentHashRate>> {
            emit(safeNanoPoolApiCall { remoteDataSource.getCurrentHashRate(address) })
        }.flowOn(Dispatchers.IO)

    suspend fun getGeneralInfo(address: String): Flow<ApiResource<GeneralInfo>> = flow {
        emit(safeNanoPoolApiCall { remoteDataSource.getGeneralInfo(address) })
    }.flowOn(Dispatchers.IO)

    suspend fun getAvgHashRateLimited(
        address: String,
    ): Flow<ApiResource<AvgHashRate>> = flow {
        emit(safeNanoPoolApiCall { remoteDataSource.getAvgHashRateLimited(address) })
    }.flowOn(Dispatchers.IO)

    suspend fun getLastReportHashRateWorker(
        address: String,
        worker: String
    ): Flow<ApiResource<LashHashRateReport>> = flow {
        emit(safeNanoPoolApiCall { remoteDataSource.getLastReportHashRateWorker(address, worker) })
    }.flowOn(Dispatchers.IO)

    suspend fun getWorkerCurrentHashRate(
        address: String,
        worker: String
    ): Flow<ApiResource<WorkerCurrentHashRate>> =
        flow {
            emit(safeNanoPoolApiCall { remoteDataSource.getWorkerCurrentHashRate(address, worker) })
        }.flowOn(Dispatchers.IO)

    suspend fun getWorkers(address: String): Flow<ApiResource<Worker>> = flow {
        emit(safeNanoPoolApiCall { remoteDataSource.getWorkers(address) })
    }.flowOn(Dispatchers.IO)

    suspend fun getPoolHashRate(): Flow<ApiResource<NanoPoolResponse>> = flow {
        emit(safeNanoPoolApiCall { remoteDataSource.getPoolHashRate() })
    }.flowOn(Dispatchers.IO)

    suspend fun workerAvgHashRate(
        address: String,
        worker: String
    ): Flow<ApiResource<AvgHashRate>> = flow {
        emit(safeNanoPoolApiCall { remoteDataSource.workerAvgHashRate(address, worker) })
    }.flowOn(Dispatchers.IO)

    suspend fun poolNumberMinor(): Flow<ApiResource<NanoPoolResponse>> = flow {
        emit(safeNanoPoolApiCall { remoteDataSource.poolNumberMinor() })
    }.flowOn(Dispatchers.IO)

    suspend fun getPayoutLimit(address: String): Flow<ApiResource<Payout>> = flow {
        emit(safeNanoPoolApiCall { remoteDataSource.getPayoutLimit(address) })
    }.flowOn(Dispatchers.IO)

    suspend fun getAccChart(address: String): Flow<ApiResource<Chart>> = flow {
        emit(safeNanoPoolApiCall { remoteDataSource.getAccChart(address) })
    }.flowOn(Dispatchers.IO)

    suspend fun getWorkerChart(address: String, worker: String): Flow<ApiResource<Chart>> = flow {
        emit(safeNanoPoolApiCall { remoteDataSource.getWorkerChart(address, worker) })
    }.flowOn(Dispatchers.IO)

    suspend fun calculator(hashRate: String): Flow<ApiResource<ApproximatedEarning>> = flow {
        emit(safeNanoPoolApiCall { remoteDataSource.calculator(hashRate) })
    }.flowOn(Dispatchers.IO)
    suspend fun ethPrice():Flow<ApiResource<EthereumResponse>> = flow {
        emit(safeNanoPoolApiCall { remoteDataSource.ethPrice() })
    }.flowOn(Dispatchers.IO)
}