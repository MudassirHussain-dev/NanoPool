package dev.hmh.nanopol.ui.main.pool

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.hmh.nanopol.R
import dev.hmh.nanopol.databinding.FragmentPoolBinding
import dev.hmh.nanopol.ui.main.pool.common.PoolHashRateViewModel
import dev.hmh.nanopol.ui.main.pool.common.PoolNumberMinorViewModel
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PoolFragment : Fragment(R.layout.fragment_pool) {

    private val poolHashRateViewModel: PoolHashRateViewModel by viewModels()
    private val poolNumberMinorViewModel: PoolNumberMinorViewModel by viewModels()

    private var _binding: FragmentPoolBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPoolBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        poolHashRateViewModel.getPoolHashRate()
        poolNumberMinorViewModel.getPoolNumberMinor()

        backgroundGetPoolHashRateReportTask()
        backgroundGetPoolNumberMinorTask()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            poolHashRateViewModel.getPoolHashRate()
            poolNumberMinorViewModel.getPoolNumberMinor()

            backgroundGetPoolHashRateReportTask()
            backgroundGetPoolNumberMinorTask()
        }
    }

    private fun backgroundGetPoolNumberMinorTask() {
        lifecycleScope.launchWhenCreated {
            poolNumberMinorViewModel.responsePoolNumberMinor.collect {
                when (it) {
                    is PoolNumberMinorViewModel.PoolNumberMinorEvent.Failure -> {
                        Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is PoolNumberMinorViewModel.PoolNumberMinorEvent.Loading -> {
                    }
                    is PoolNumberMinorViewModel.PoolNumberMinorEvent.Success -> {
                        binding.apply {
                            txtMinorCount.text =it.data.data
                        }
                    }
                }
            }
        }
    }

    private fun backgroundGetPoolHashRateReportTask() {
        lifecycleScope.launchWhenCreated {
            poolHashRateViewModel.responsePoolHashRate.collect {
                when (it) {
                    is PoolHashRateViewModel.PoolHashRateEvent.Failure -> {
                        Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is PoolHashRateViewModel.PoolHashRateEvent.Loading -> {
                    }
                    is PoolHashRateViewModel.PoolHashRateEvent.Success -> {
                     binding.apply {
                         txtPoolHashRate.text = it.data.data
                     }
                    }
                }
            }
        }
    }

}