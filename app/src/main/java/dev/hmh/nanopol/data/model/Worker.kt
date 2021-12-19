package dev.hmh.nanopol.data.model

data class Worker(
    val `data`: List<WorkerItem>,
    val status: Boolean
)

data class WorkerItem(
    val hashrate: Int,
    val id: String,
    val lastShare: Int,
    val rating: Int,
    val uid: Int
)