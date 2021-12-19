package dev.hmh.nanopol.ui.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.hmh.nanopol.R
import dev.hmh.nanopol.data.network.auth_api.api.AuthApi
import dev.hmh.nanopol.data.network.auth_api.response.LoginResponse
import dev.hmh.nanopol.databinding.FragmentLoginBinding
import retrofit2.Call
import retrofit2.Response

class LoginFragment : Fragment(R.layout.fragment_login) {

    lateinit var strUserName: String
    lateinit var strPassword: String

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = context?.getSharedPreferences(
            getString(R.string.PREF_FILE1),
            AppCompatActivity.MODE_PRIVATE
        )
        if (sharedPreferences != null) {
            binding.etUserName.setText(sharedPreferences.getString("username", "").toString())
            binding.etPassword.setText(sharedPreferences.getString("password", "").toString())

        }

        binding.btnLogin.setOnClickListener {

            strUserName = binding.etUserName.text.toString()
            strPassword = binding.etPassword.text.toString()

            if (strUserName.isEmpty()) {
                Toast.makeText(context, "username is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (strPassword.isEmpty()) {
                Toast.makeText(context, "password is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            backgroundLoginTask()

        }
        binding.txtLoginSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())

        }
    }

    private fun backgroundLoginTask() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            btnLogin.visibility = View.INVISIBLE
        }
        val authApi = AuthApi.create().login(strUserName, strPassword)
        authApi.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.apply {
                    progressBar.visibility = View.INVISIBLE
                    btnLogin.visibility = View.VISIBLE
                }
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.error == "200") {
                        Toast.makeText(context, "${result?.message}", Toast.LENGTH_SHORT).show()
                        val sharedPreferences = context?.getSharedPreferences(
                            getString(R.string.PREF_FILE1),
                            AppCompatActivity.MODE_PRIVATE
                        )
                        val editor = sharedPreferences!!.edit()
                        editor.putString("username", strUserName)
                        editor.putString("password", strPassword)
                        editor.apply()
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToWalletListFragment())
                    } else {
                        Toast.makeText(context, "${result?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Unexpected Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.apply {
                    progressBar.visibility = View.INVISIBLE
                    btnLogin.visibility = View.VISIBLE
                }
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}