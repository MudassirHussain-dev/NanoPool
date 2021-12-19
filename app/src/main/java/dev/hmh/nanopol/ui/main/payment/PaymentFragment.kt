package dev.hmh.nanopol.ui.main.payment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.hmh.nanopol.R
import dev.hmh.nanopol.data.adapter.PaymentAdapter
import dev.hmh.nanopol.data.model.PaymentItem
import dev.hmh.nanopol.databinding.FragmentPaymentBinding
import dev.hmh.nanopol.ui.main.payment.common.PaymentViewModel
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PaymentFragment : Fragment() {

    private val paymentViewModel: PaymentViewModel by viewModels()

    lateinit var strAccountNo: String

    private lateinit var paymentAdapter: PaymentAdapter
    private lateinit var arrayList: ArrayList<PaymentItem>

    private var _binding: FragmentPaymentBinding? = null
    private val binding: FragmentPaymentBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arrayList = ArrayList()
        arrayList.clear()
        val sharedPreferences = context?.getSharedPreferences(
            getString(R.string.PREF_FILE),
            AppCompatActivity.MODE_PRIVATE
        )
        strAccountNo=sharedPreferences?.getString("wallet", "").toString()
        paymentViewModel.getMinerPayment(strAccountNo)
        backgroundMinerPaymentTask()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            backgroundMinerPaymentTask()
        }


    }

    private fun backgroundMinerPaymentTask() {
        lifecycleScope.launchWhenCreated {
            paymentViewModel.responsePayment.collect {
                when (it) {
                    is PaymentViewModel.PaymentEvent.Empty -> {
                        binding.apply {
                            progressBar.visibility=View.VISIBLE
                            txtRecordNotFound.visibility=View.GONE
                            rvPayment.visibility = View.GONE
                        }
                    }
                    is PaymentViewModel.PaymentEvent.Failure -> {
                        binding.apply {
                            progressBar.visibility=View.GONE
                            txtRecordNotFound.visibility=View.VISIBLE
                            rvPayment.visibility = View.GONE
                        }
                        Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is PaymentViewModel.PaymentEvent.Loading -> {
                        binding.apply {
                            progressBar.visibility=View.VISIBLE
                            txtRecordNotFound.visibility=View.GONE
                            rvPayment.visibility = View.GONE
                        }
                    }
                    is PaymentViewModel.PaymentEvent.Success -> {
                        Log.d("pay", "backgroundMinerPaymentTask: " + it.payment.data.toString())
                        if (it.payment.status) {
                            binding.apply {
                                progressBar.visibility=View.GONE
                                txtRecordNotFound.visibility=View.GONE
                                rvPayment.visibility = View.VISIBLE
                            }
                            initRecyclerView(it.payment.data as ArrayList<PaymentItem>)
                        } else {
                            binding.apply {
                                progressBar.visibility=View.GONE
                                txtRecordNotFound.visibility=View.VISIBLE
                                rvPayment.visibility = View.GONE
                            }
                            Toast.makeText(requireContext(), "Record Not found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun initRecyclerView(arrayList: ArrayList<PaymentItem>) {
        paymentAdapter = PaymentAdapter(arrayList)
        binding.apply {
            rvPayment.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = paymentAdapter
            }
        }

    }
}