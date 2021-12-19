package dev.hmh.nanopol.ui.main.dashboard

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
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import dev.hmh.nanopol.R
import dev.hmh.nanopol.data.model.CustomCalculate
import dev.hmh.nanopol.data.model.HashRateChart
import dev.hmh.nanopol.data.model.PayoutViewModel
import dev.hmh.nanopol.data.model.SharedChart
import dev.hmh.nanopol.databinding.FragmentDashboardBinding
import dev.hmh.nanopol.ui.main.dashboard.common.*
import kotlinx.coroutines.flow.collect
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var strAccountNo: String
    private val balanceViewModel: BalanceViewModel by viewModels()
    private val hashRateViewModel: CurrentHashRateViewModel by viewModels()
    private val lastHashRateViewModel: LastHashRateReportViewModel by viewModels()
    private val avgHashRateViewModel: AvgHashRateLimitedViewModel by viewModels()
    private val calculateViewModel: CalculatorViewModel by viewModels()
    private val chartViewModel: ChartViewModel by viewModels()
    private val payoutViewModel: PayoutViewModel by viewModels()
    private val ethereumPriceViewModel: EthereumPriceViewModel by viewModels()
    private var strBalance: String? = "0.00"
    private var str6HourAve: String? = "0"


    lateinit var arrCalculator: ArrayList<CustomCalculate>
    lateinit var arrHashRateBarChart: ArrayList<HashRateChart>
    lateinit var arrSharedBarChart: ArrayList<SharedChart>
    private val df = DecimalFormat("0.00")
    private val df2 = DecimalFormat("0.000")
    private val df1 = DecimalFormat("0")
    private val sdf = SimpleDateFormat("HH:mm")


    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arrCalculator = ArrayList()
        arrHashRateBarChart = ArrayList()
        arrSharedBarChart = ArrayList()

        val sharedPreferences = context?.getSharedPreferences(
            getString(R.string.PREF_FILE),
            AppCompatActivity.MODE_PRIVATE
        )
        strAccountNo = sharedPreferences?.getString("wallet", "").toString()


        // strAccountNo = "0x0d539bf5c170e7dcfd74a50b377febee4fbc912b"

        balanceViewModel.getBalance(strAccountNo)
        backgroundGetBalanceTask()
        hashRateViewModel.getCurrentHashRate(strAccountNo)
        backgroundGetCurrentHashRateTask()
        lastHashRateViewModel.getLastHashRateReport(strAccountNo)
        backgroundGetLastHashRateTask()
        avgHashRateViewModel.getAvgHashRateLimited(strAccountNo)
        backgroundGetAvgHashRateTask()
        payoutViewModel.getPayoutLimit(strAccountNo)
        backgroundPayoutLimitTask()

        chartViewModel.getAccChart(strAccountNo)
        backgroundGetChartDataTask()


        binding.apply {
            etSearchHashRate.visibility = View.GONE
            txtCurrent.setOnClickListener {
                etSearchHashRate.visibility = View.GONE
                calculateViewModel.calculator(str6HourAve!!)
            }
            txtCaculate.setOnClickListener {
                etSearchHashRate.visibility = View.VISIBLE

            }

            binding.swipeRefreshLayout.setOnRefreshListener {
                binding.swipeRefreshLayout.isRefreshing = false
                balanceViewModel.getBalance(strAccountNo)
                backgroundGetBalanceTask()
                hashRateViewModel.getCurrentHashRate(strAccountNo)
                backgroundGetCurrentHashRateTask()
                lastHashRateViewModel.getLastHashRateReport(strAccountNo)
                backgroundGetLastHashRateTask()
                avgHashRateViewModel.getAvgHashRateLimited(strAccountNo)
                backgroundGetAvgHashRateTask()

                chartViewModel.getAccChart(strAccountNo)
                backgroundGetChartDataTask()
                payoutViewModel.getPayoutLimit(strAccountNo)
                backgroundPayoutLimitTask()


            }

            etSearchHashRate.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.isNotBlank() || s.isNotEmpty()) {
                        calculateViewModel.calculator(s.toString())
                        backgroundGetCalculateTask()
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })
        }


    }


    private fun backgroundGetCalculateTask() {
        lifecycleScope.launchWhenCreated {
            calculateViewModel.responseCalculator.collect {
                when (it) {
                    is CalculatorViewModel.CalculatorEvent.Empty -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is CalculatorViewModel.CalculatorEvent.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is CalculatorViewModel.CalculatorEvent.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is CalculatorViewModel.CalculatorEvent.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Log.d("dash", "backgroundGetCalculateTask: " + it.data.status)
                        val minute = it.data.data?.minute
                        val hour = it.data.data?.hour
                        val week = it.data.data?.week
                        val month = it.data.data?.month
                        val year = it.data.data?.day

                        binding.apply {

                            txtMiniCoin.text = (df.format(minute?.coins)).toString()
                            txtMinuteBTC.text = (df.format(minute?.bitcoins)).toString()
                            txtMinitPrice.text = (df.format(minute?.dollars)).toString()

                            txtHourCoins.text = (df.format(hour?.coins)).toString()
                            txtHourBTC.text = (df.format(hour?.bitcoins)).toString()
                            txtHourPrice.text = (df.format(hour?.dollars)).toString()

                            txtWeekCoins.text = (df.format(week?.coins)).toString()
                            txtWeekBTC.text = (df.format(week?.bitcoins)).toString()
                            txtWeekPrice.text = (df.format(week?.dollars)).toString()

                            txtMonthCoints.text = (df.format(month?.coins)).toString()
                            txtMonthBTC.text = (df.format(month?.bitcoins)).toString()
                            txtMonthPrice.text = (df.format(month?.dollars)).toString()

                            txtYearCoins.text = (df.format(year?.coins)).toString()
                            txtYearBTC.text = (df.format(year?.bitcoins)).toString()
                            txtYearPrice.text = (df.format(year?.dollars)).toString()


                        }
                    }
                }
            }
        }

    }


    private fun backgroundGetAvgHashRateTask() {
        lifecycleScope.launchWhenCreated {
            avgHashRateViewModel.responseAvgHashRateLimited.collect {
                when (it) {
                    is AvgHashRateLimitedViewModel.AvgHashRateLimitedEvent.Empty -> {
                    }
                    is AvgHashRateLimitedViewModel.AvgHashRateLimitedEvent.Failure -> {
                        Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is AvgHashRateLimitedViewModel.AvgHashRateLimitedEvent.Loading -> {
                    }
                    is AvgHashRateLimitedViewModel.AvgHashRateLimitedEvent.Success -> {
                        binding.apply {
                            val result = it.data.data
                            if (result != null) {
                                txtAverageHashRate.text =
                                    (df.format(result.h6.toDouble())).toString() + " Mh/s"
                                str6HourAve = result.h6.toString()
                                txtAverageLast24Hour.text =
                                    (df.format(result.h24.toDouble())).toString() + " Mh/s"
                                calculateViewModel.calculator(result.h6)
                                backgroundGetCalculateTask()
                            }

                        }
                    }
                }
            }
        }
    }

    private fun backgroundGetLastHashRateTask() {
        lifecycleScope.launchWhenCreated {
            lastHashRateViewModel.responseLastHashRateReport.collect {
                when (it) {
                    is LastHashRateReportViewModel.LastHashRateReportEvent.Empty -> {
                    }
                    is LastHashRateReportViewModel.LastHashRateReportEvent.Failure -> {
                        Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is LastHashRateReportViewModel.LastHashRateReportEvent.Loading -> {
                    }
                    is LastHashRateReportViewModel.LastHashRateReportEvent.Success -> {
                        binding.apply {
                            val result = it.data.data
                            if (result != null) {
                                txtLastReportedHashRate.text =
                                    (df.format(result.toDouble())).toString() + " Mh/s"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun backgroundGetCurrentHashRateTask() {
        lifecycleScope.launchWhenCreated {
            hashRateViewModel.responseCurrentHashRate.collect {
                when (it) {
                    is CurrentHashRateViewModel.CurrentHashRateEvent.Empty -> {
                    }
                    is CurrentHashRateViewModel.CurrentHashRateEvent.Failure -> {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is CurrentHashRateViewModel.CurrentHashRateEvent.Loading -> {
                    }
                    is CurrentHashRateViewModel.CurrentHashRateEvent.Success -> {
                        val result = it.balance.data
                        if (result != null) {
                            binding.txtCurrentHashRate.text =
                                (df.format(result.toDouble())).toString() + " Mh/s"
                        }
                    }
                }
            }
        }
    }

    private fun backgroundEthereumPriceTask(balance: String) {
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
                          val   strDoller = it.data.data.price_usd.toString()
                            val convert = (balance.toDouble() * strDoller.toDouble()).toString()
                            binding.txtBalance.text =
                                (df.format(balance.toDouble())).toString() + " ETH\n" + "~ ${df2.format(convert.toDouble()).toString()} $"
                        }
                    }
                }
            }
        }
    }

    private fun backgroundGetBalanceTask() {
        lifecycleScope.launchWhenCreated {

            balanceViewModel.responseFetchBalance.collect {
                when (it) {
                    is BalanceViewModel.FetchBalanceEvent.Empty -> {

                    }
                    is BalanceViewModel.FetchBalanceEvent.Failure -> {
                        Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is BalanceViewModel.FetchBalanceEvent.Loading -> {

                    }
                    is BalanceViewModel.FetchBalanceEvent.Success -> {
                        val result = it.balance.data
                        if (result != null) {
                            strBalance = result.toString()
                            ethereumPriceViewModel.ethPrice()
                            backgroundEthereumPriceTask(strBalance!!)


                        }
                    }

                }
            }
        }
    }

    private fun backgroundPayoutLimitTask() {
        lifecycleScope.launchWhenCreated {
            payoutViewModel.responsePayout.collect {
                when (it) {
                    is PayoutViewModel.PayoutEvent.Empty -> {
                    }
                    is PayoutViewModel.PayoutEvent.Failure -> {
                    }
                    is PayoutViewModel.PayoutEvent.Loading -> {
                    }
                    is PayoutViewModel.PayoutEvent.Success -> {
                        val result = it.data.data.payout.toString()
                        if (result.isNotEmpty()) {
                            binding.apply {
                                val per =
                                    ((strBalance!!.toDouble() / result.toDouble()) * 100).toString()
                                txtPercentageDetail.text =
                                    "${(df.format(per.toDouble()))}% of your $result ETH payout limit reached"
                                txtPercentageValue.text =
                                    df1.format(per.toDouble()).toString() + "%"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun backgroundGetChartDataTask() {
        lifecycleScope.launchWhenCreated {
            chartViewModel.responseChart.collect {
                when (it) {
                    is ChartViewModel.ChartEvent.Empty -> {
                    }
                    is ChartViewModel.ChartEvent.Failure -> {
                        Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is ChartViewModel.ChartEvent.Loading -> {
                    }
                    is ChartViewModel.ChartEvent.Success -> {

                        arrHashRateBarChart = ArrayList()
                        arrSharedBarChart = ArrayList()

                        val result = it.data.data

                        for (i in result.indices) {
                            arrHashRateBarChart.add(
                                HashRateChart(
                                    (sdf.format(Date((result[i].date) * 1000))).toString(),
                                    result[i].hashrate
                                )
                            )
                            arrSharedBarChart.add(
                                SharedChart(
                                    (sdf.format(Date((result[i].date) * 1000))).toString(),
                                    result[i].shares
                                ),
                            )
                        }

                        Log.d("no", "backgroundGetChartDataTask: Array1 " + arrHashRateBarChart)
                        Log.d("no", "backgroundGetChartDataTask: Array2 " + arrSharedBarChart)

                        initHashRateBarChart()

                        val hashRateEntries: ArrayList<BarEntry> = ArrayList()
                        //you can replace this data object with  your custom object
                        for (i in arrHashRateBarChart.indices) {
                            val hashRate = arrHashRateBarChart[i]
                            hashRateEntries.add(BarEntry(i.toFloat(), hashRate.hashRate.toFloat()))
                        }
                        val hashRateBarDataSet = BarDataSet(hashRateEntries, "Hashrate")
                        hashRateBarDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
                        val hashRateData = BarData(hashRateBarDataSet)
                        binding.barChartHashRate.data = hashRateData
                        binding.barChartHashRate.invalidate()

                        //.........................................//

                        initSharedBarChart()
                        val sharedEntries: ArrayList<BarEntry> = ArrayList()

                        for (i in arrSharedBarChart.indices) {
                            val shared = arrSharedBarChart[i]
                            sharedEntries.add(BarEntry(i.toFloat(), shared.sharedChart.toFloat()))
                        }

                        val sharedBarDataSet = BarDataSet(sharedEntries, "Shared")
                        sharedBarDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

                        val sharedData = BarData(sharedBarDataSet)
                        binding.barChartShared.data = sharedData

                        binding.barChartShared.invalidate()


                    }
                }
            }
        }
    }

    private fun initSharedBarChart() {

//        hide grid lines
        binding.barChartShared.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = binding.barChartShared.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        //remove right y-axis
        binding.barChartShared.axisRight.isEnabled = false

        //remove legend
        binding.barChartShared.legend.isEnabled = false


        //remove description label
        binding.barChartShared.description.isEnabled = false


        //add animation
        binding.barChartShared.animateY(3000)

        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.valueFormatter = MyAxisFormatter2()
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = +90f

    }

    private fun initHashRateBarChart() {


//        hide grid lines
        binding.barChartHashRate.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = binding.barChartHashRate.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        //remove right y-axis
        binding.barChartHashRate.axisRight.isEnabled = false

        //remove legend
        binding.barChartHashRate.legend.isEnabled = false


        //remove description label
        binding.barChartHashRate.description.isEnabled = false


        //add animation
        binding.barChartHashRate.animateY(3000)

        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.valueFormatter = MyAxisFormatter()
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = +90f

    }

    inner class MyAxisFormatter() : IndexAxisValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            Log.d("text", "getAxisLabel: index $index")
            return if (index < arrHashRateBarChart.size) {
                arrHashRateBarChart[index].dateTime
            } else {
                ""
            }
        }
    }

    inner class MyAxisFormatter2() : IndexAxisValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            Log.d("text", "getAxisLabel: index $index")
            return if (index < arrSharedBarChart.size) {
                arrSharedBarChart[index].dateTime
            } else {
                ""
            }
        }
    }
}
