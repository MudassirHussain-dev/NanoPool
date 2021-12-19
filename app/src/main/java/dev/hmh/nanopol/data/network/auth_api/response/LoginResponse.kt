package dev.hmh.nanopol.data.network.auth_api.response

data class LoginResponse(
    val error: String,
    val message: String,
    val user: User
)

data class User(
    val Id: String,
    val email: String,
    val username: String
)