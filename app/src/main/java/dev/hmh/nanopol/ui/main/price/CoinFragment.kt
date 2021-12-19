package dev.hmh.nanopol.ui.main.price

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
import dev.hmh.nanopol.databinding.FragmentCoinBinding
import dev.hmh.nanopol.ui.main.dashboard.common.EthereumPriceViewModel
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CoinFragment : Fragment(R.layout.fragment_coin) {
    private val ethereumPriceViewModel: EthereumPriceViewModel by viewModels()
    private var _binding: FragmentCoinBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ethereumPriceViewModel.ethPrice()
        backgroundEthereumPriceTask()


    }

    private fun backgroundEthereumPriceTask() {
        lifecycleScope.launchWhenCreated {

            ethereumPriceViewModel.responseEthereum.collect {
                when (it) {
                    is EthereumPriceViewModel.EthereumPriceEvent.Failure -> {
                        Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is EthereumPriceViewModel.EthereumPriceEvent.Loading -> {
                    }
                    is EthereumPriceViewModel.EthereumPriceEvent.Success -> {
                        if (it.data.status) {
                            val prices = it.data.data
                            binding.apply {
                                txtUSDPrice.text = prices.price_usd.toString()
                                txtBTCPrice.text = prices.price_btc.toString()
                                txtRURPrice.text = prices.price_rur.toString()
                                txtEURPrice.text = prices.price_rur.toString()
                                txtCNYPrice.text = prices.price_cny.toString()
                                txtGBPPrice.text = prices.price_gbp.toString()
                            }
                        }
                    }
                }
            }
        }
    }
}