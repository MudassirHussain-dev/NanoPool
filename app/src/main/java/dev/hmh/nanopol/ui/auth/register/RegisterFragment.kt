package dev.hmh.nanopol.ui.auth.register

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.hmh.nanopol.R
import dev.hmh.nanopol.data.network.auth_api.api.AuthApi
import dev.hmh.nanopol.data.network.auth_api.response.ApiResponse
import dev.hmh.nanopol.databinding.FragmentRegisterBinding
import retrofit2.Call
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class RegisterFragment : Fragment(R.layout.fragment_register) {

    lateinit var strUserName: String
    lateinit var strName: String
    lateinit var strEmail: String
    lateinit var strPassword: String
    lateinit var strConfirmedPassword: String
    lateinit var strDate: String

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val df: DateFormat = SimpleDateFormat("EEE, d MMM yyyy, HH:mm")
        strDate = df.format(Calendar.getInstance().getTime())
        binding.apply {
            btnRegister.setOnClickListener {

                strUserName = etUserName.text.toString()
                strName = etName.text.toString()
                strEmail = etEmail.text.toString()
                strPassword = etPassword.text.toString()
                strConfirmedPassword = etConfirmedPassword.text.toString()

                if (strUserName.isEmpty()) {
                    Toast.makeText(context, "UserName is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (strName.isEmpty()) {
                    Toast.makeText(context, "Name is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (strEmail.isEmpty()) {
                    Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (strPassword.isEmpty()) {
                    Toast.makeText(context, "password is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (strConfirmedPassword.isEmpty()) {
                    Toast.makeText(context, "confirmed password is empty", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                    Toast.makeText(context, "Invalid Email", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                if (strConfirmedPassword != strPassword) {
                    Toast.makeText(context, "password does not match", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                backgroundRegisterUserTask()
            }
        }


    }

    private fun backgroundRegisterUserTask() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            btnRegister.visibility = View.INVISIBLE
        }

        val authApi =
            AuthApi.create().register(strUserName, strName, strEmail, strPassword, strDate)
        authApi.enqueue(object : retrofit2.Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                binding.apply {
                    progressBar.visibility = View.INVISIBLE
                    btnRegister.visibility = View.VISIBLE
                }
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.error == "200") {
                        Toast.makeText(context, "${result!!.message}", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                    } else {
                        Toast.makeText(context, "${result!!.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Unexpected Error", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                binding.apply {
                    progressBar.visibility = View.INVISIBLE
                    btnRegister.visibility = View.VISIBLE
                }
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}