package dev.hmh.nanopol.ui.auth.add_wallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.hmh.nanopol.R
import dev.hmh.nanopol.data.network.auth_api.api.AuthApi
import dev.hmh.nanopol.data.network.auth_api.response.ApiResponse
import dev.hmh.nanopol.databinding.FragmentAddNewWalletBinding
import dev.hmh.nanopol.ui.auth.register.common.CheckAccountViewModel
import kotlinx.coroutines.flow.collect
import retrofit2.Call
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddNewWalletFragment : Fragment(R.layout.fragment_add_new_wallet) {

    lateinit var strUserName: String
    lateinit var strWallet: String
    lateinit var strWalletName: String
    lateinit var strDate: String
    private val args by navArgs<AddNewWalletFragmentArgs>()
    lateinit var strActionType: String

    private val checkAccountViewModel: CheckAccountViewModel by viewModels()

    private var _binding: FragmentAddNewWalletBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNewWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        strActionType = args.argumentWallet.strType

        if (strActionType == "1") {
            binding.btnAddWallet.visibility = View.VISIBLE
            binding.btnUpdateWallet.visibility = View.GONE
        } else {
            binding.btnAddWallet.visibility = View.GONE
            binding.btnUpdateWallet.visibility = View.VISIBLE
            binding.etWalletAddress.setText(args.argumentWallet.strWallet)
        }

        val df: DateFormat = SimpleDateFormat("EEE, d MMM yyyy, HH:mm")
        strDate = df.format(Calendar.getInstance().time)

        val sharedPreferences = context?.getSharedPreferences(
            getString(R.string.PREF_FILE1),
            AppCompatActivity.MODE_PRIVATE
        )
        strUserName = sharedPreferences?.getString("username", "").toString()

        binding.apply {
            btnAddWallet.setOnClickListener {
                strWallet = etWalletAddress.text.toString()
                strWalletName = etAccountName.text.toString()

                if (strWallet.isEmpty()) {
                    Toast.makeText(context, "your wallet is empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (strWalletName.isEmpty()) {
                    Toast.makeText(context, "account name is empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                checkAccountViewModel.checkAccount(strWallet)
                backgroundCheckAccountTask()
            }
            btnUpdateWallet.setOnClickListener {
                strWallet = etWalletAddress.text.toString()
                strWalletName = etAccountName.text.toString()

                if (strWallet.isEmpty()) {
                    Toast.makeText(context, "your wallet is empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (strWalletName.isEmpty()) {
                    Toast.makeText(context, "account name is empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                checkAccountViewModel.checkAccount(strWallet)
                backgroundCheckAccountTask()
            }
        }
    }

    private fun backgroundCheckAccountTask() {
        lifecycleScope.launchWhenCreated {

            checkAccountViewModel.responseCheckAccount.collect {
                when (it) {
                    is CheckAccountViewModel.CheckAccountEvent.Empty -> {
                        binding.apply {
                            progressBar.visibility = View.VISIBLE
                            btnAddWallet.visibility = View.INVISIBLE
                        }
                    }
                    is CheckAccountViewModel.CheckAccountEvent.Failure -> {
                        binding.apply {
                            progressBar.visibility = View.INVISIBLE
                            btnAddWallet.visibility = View.VISIBLE
                        }
                        Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is CheckAccountViewModel.CheckAccountEvent.Loading -> {
                        binding.apply {
                            progressBar.visibility = View.VISIBLE
                            btnAddWallet.visibility = View.INVISIBLE
                        }
                    }
                    is CheckAccountViewModel.CheckAccountEvent.Success -> {
                        val result = it.data.data
                        if (result == "Account exist") {
                            if (strActionType == "1") {

                                backgroundAddWalletTask()
                            } else {
                                backgroundUpdateWalletTask()
                            }
                        } else {
                            Toast.makeText(context, "Account does not exist", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun backgroundUpdateWalletTask() {
        val authApi =
            AuthApi.create().updateWallet(strWallet, strWalletName, args.argumentWallet.Id)
        authApi.enqueue(object : retrofit2.Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                binding.apply {
                    progressBar.visibility = View.INVISIBLE
                    btnUpdateWallet.visibility = View.VISIBLE
                }
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.error == "200") {
                        Toast.makeText(context, "record has been modified", Toast.LENGTH_SHORT)
                            .show()
                        //findNavController().navigate(AddNewWalletFragmentDirections.actionAddNewWalletFragmentToWalletListFragment())
                    } else {
                        Toast.makeText(context, "${result?.message}", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(context, "Unexpected Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                binding.apply {
                    progressBar.visibility = View.INVISIBLE
                    btnUpdateWallet.visibility = View.VISIBLE
                }
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun backgroundAddWalletTask() {
        val authApi = AuthApi.create().addWallet(strWallet, strWalletName, strUserName, strDate)
        authApi.enqueue(object : retrofit2.Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                binding.apply {
                    progressBar.visibility = View.INVISIBLE
                    btnAddWallet.visibility = View.VISIBLE
                }
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.error == "200") {
                        findNavController().navigate(AddNewWalletFragmentDirections.actionAddNewWalletFragmentToWalletListFragment())
                    } else {
                        Toast.makeText(context, "${result?.message}", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(context, "Unexpected Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                binding.apply {
                    progressBar.visibility = View.INVISIBLE
                    btnAddWallet.visibility = View.VISIBLE
                }
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}