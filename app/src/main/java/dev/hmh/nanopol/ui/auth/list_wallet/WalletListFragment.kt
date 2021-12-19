package dev.hmh.nanopol.ui.auth.list_wallet

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.hmh.nanopol.ui.main.MainActivity
import dev.hmh.nanopol.R
import dev.hmh.nanopol.action.Listener
import dev.hmh.nanopol.action.UpdateListener
import dev.hmh.nanopol.data.adapter.WalletListAdapter
import dev.hmh.nanopol.data.network.auth_api.api.AuthApi
import dev.hmh.nanopol.data.network.auth_api.response.ApiResponse
import dev.hmh.nanopol.data.network.auth_api.response.Wallet
import dev.hmh.nanopol.data.network.auth_api.response.WalletDetail
import dev.hmh.nanopol.databinding.FragmentWalletListBinding
import dev.hmh.nanopol.ui.auth.args.ArgumentWallet
import retrofit2.Call
import retrofit2.Response

class WalletListFragment : Fragment(R.layout.fragment_wallet_list) {

    lateinit var arrWalletList: ArrayList<WalletDetail>
    lateinit var walletListAdapter: WalletListAdapter
    lateinit var strUserName: String

    private var _binding: FragmentWalletListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWalletListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = context?.getSharedPreferences(
            getString(R.string.PREF_FILE1),
            AppCompatActivity.MODE_PRIVATE
        )
        strUserName = sharedPreferences?.getString("username", "").toString()

        binding.txtAddNewAccount.setOnClickListener {
            findNavController().navigate(
                WalletListFragmentDirections.actionWalletListFragmentToAddNewWalletFragment(
                    ArgumentWallet("", "", "1")
                )
            )
        }

        arrWalletList = ArrayList()
        // arrWalletList.add(WalletModel("Name", "0x0d539bf5c170e7dcfd74a50b377febee4fbc912b"))

        backgroundGetWalletListTask()

        initRecyclerView(arrWalletList)


    }

    private fun backgroundGetWalletListTask() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            txtRecordNotFound.visibility = View.GONE
            rvWalletList.visibility = View.GONE

        }
        val authApi = AuthApi.create().getWallets(strUserName)
        authApi.enqueue(object : retrofit2.Callback<Wallet> {
            override fun onResponse(call: Call<Wallet>, response: Response<Wallet>) {
                if (response.isSuccessful) {
                    val result = response.body()

                    if (result?.error == "200") {

                        if (result.wallet.isNotEmpty()) {
                            binding.apply {
                                progressBar.visibility = View.GONE
                                txtRecordNotFound.visibility = View.GONE
                                rvWalletList.visibility = View.VISIBLE

                            }
                            initRecyclerView(result.wallet as ArrayList<WalletDetail>)
                        } else {
                            binding.apply {
                                progressBar.visibility = View.GONE
                                txtRecordNotFound.visibility = View.VISIBLE
                                rvWalletList.visibility = View.GONE

                            }
                        }
                    } else {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            txtRecordNotFound.visibility = View.VISIBLE
                            rvWalletList.visibility = View.GONE

                        }
                    }

                } else {
                    binding.apply {
                        progressBar.visibility = View.GONE
                        txtRecordNotFound.visibility = View.VISIBLE
                        rvWalletList.visibility = View.GONE

                    }
                    Toast.makeText(context, "Unexpected Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Wallet>, t: Throwable) {
                binding.apply {
                    progressBar.visibility = View.GONE
                    txtRecordNotFound.visibility = View.VISIBLE
                    rvWalletList.visibility = View.GONE

                }
            }
        })
    }

    private fun initRecyclerView(arrayList: ArrayList<WalletDetail>) {
        walletListAdapter = WalletListAdapter(requireContext(), arrayList, object : Listener {
            override fun getWorkerId(id: String) {
                val sharedPreferences = context?.getSharedPreferences(
                    getString(R.string.PREF_FILE),
                    AppCompatActivity.MODE_PRIVATE
                )
                val editor = sharedPreferences!!.edit()
                editor.putString("wallet", id)
                editor.apply()
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
        }, object : UpdateListener {
            override fun getAccountId(Id: String, wallet: String, action: String) {
                if (action == "d") {
                    backgroundDeleteWalletTask(Id)
                } else {
                    findNavController().navigate(
                        WalletListFragmentDirections.actionWalletListFragmentToAddNewWalletFragment(
                            ArgumentWallet(Id, wallet, "2")
                        )
                    )
                }
            }
        })
        binding.apply {
            rvWalletList.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = walletListAdapter
            }
        }
    }

    private fun backgroundDeleteWalletTask(id: String) {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm delete?")
        builder.setMessage("Your are going to delete this account from list")

        builder.setPositiveButton("DELETE") { dialog, which ->
            dialog.dismiss()
            val authApi = AuthApi.create().deleteWallet(id)
            authApi.enqueue(object : retrofit2.Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result?.error == "200") {
                            backgroundGetWalletListTask()
                        } else {
                            Toast.makeText(context, "${result?.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Unexpected Error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        builder.setNegativeButton("CANCEL") { dialog, which ->
            dialog.dismiss()
        }


        builder.show()

    }
}