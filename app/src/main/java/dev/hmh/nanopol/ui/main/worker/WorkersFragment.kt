package dev.hmh.nanopol.ui.main.worker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import dev.hmh.nanopol.action.Listener
import dev.hmh.nanopol.data.adapter.WorkersAdapter
import dev.hmh.nanopol.data.model.WorkerItem
import dev.hmh.nanopol.databinding.FragmentWorkersBinding
import dev.hmh.nanopol.ui.main.dashboard.common.BalanceViewModel
import dev.hmh.nanopol.ui.main.worker.common.WorkerViewModel
import dev.hmh.nanopol.ui.main.worker_detail.WorkerDetailActivity
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class WorkersFragment : Fragment() {

    private val workerViewModel: WorkerViewModel by viewModels()
    private val balanceViewModel: BalanceViewModel by viewModels()
    lateinit var strAccountNo: String

    private lateinit var workersAdapter: WorkersAdapter


    private var _binding: FragmentWorkersBinding? = null
    private val binding: FragmentWorkersBinding
        get() = _binding!!

    override
    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWorkersBinding.inflate(inflater, container, false)

        return _binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = context?.getSharedPreferences(
            getString(R.string.PREF_FILE),
            AppCompatActivity.MODE_PRIVATE
        )
        strAccountNo=sharedPreferences?.getString("wallet", "").toString()

        workerViewModel.getGeneralInfo(strAccountNo)
        backgroundWorkerTask()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            workerViewModel.getGeneralInfo(strAccountNo)
            backgroundWorkerTask()
        }





    }

    private fun backgroundWorkerTask() {
        lifecycleScope.launchWhenCreated {
            workerViewModel.responseWorkers.collect {
                when (it) {
                    is WorkerViewModel.WorkerEvent.Empty -> {
                        binding.apply {
                            txtRecordNotFound.visibility = View.GONE
                            progressBar.visibility = View.VISIBLE
                            rvWorkers.visibility = View.GONE
                            etSearchWorkers.visibility=View.GONE
                        }
                    }
                    is WorkerViewModel.WorkerEvent.Failure -> {
                        binding.apply {
                            txtRecordNotFound.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            rvWorkers.visibility = View.GONE
                            etSearchWorkers.visibility=View.GONE
                        }
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is WorkerViewModel.WorkerEvent.Loading -> {
                        binding.apply {
                            txtRecordNotFound.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            rvWorkers.visibility = View.VISIBLE
                            etSearchWorkers.visibility=View.GONE
                        }
                    }
                    is WorkerViewModel.WorkerEvent.Success -> {
                        Log.d("workers", "backgroundWorkerTask: " + it.data.data)
                        binding.apply {
                            txtRecordNotFound.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            rvWorkers.visibility = View.VISIBLE
                            etSearchWorkers.visibility=View.VISIBLE
                        }
                        initRecyclerview(it.data.data as ArrayList<WorkerItem>)

                        binding.etSearchWorkers.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                if (workersAdapter != null) {
                                    workersAdapter.getFilter()!!.filter(s)
                                }
                            }

                            override fun afterTextChanged(s: Editable?) {

                            }
                        })
                    }
                }
            }
        }
    }

    private fun initRecyclerview(arrayList: ArrayList<WorkerItem>) {
        workersAdapter = WorkersAdapter(requireContext(), arrayList, object : Listener {
            override fun getWorkerId(id: String) {
                val bundle = Bundle()
                bundle.putString("id", id)
                bundle.putString("Acc", strAccountNo)
                startActivity(
                    Intent(
                        requireContext(),
                        WorkerDetailActivity::class.java
                    ).putExtras(bundle)
                )
            }
        })
        binding.apply {
            rvWorkers.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = workersAdapter
            }
        }
    }

    protected fun displayReceivedData(message: String) {
        Toast.makeText(context, "$message YES", Toast.LENGTH_SHORT).show()
    }

}