package dev.hmh.nanopol.ui.main.worker_detail

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import dev.hmh.nanopol.data.model.HashRateChart
import dev.hmh.nanopol.data.model.SharedChart
import dev.hmh.nanopol.databinding.ActivityWorkerDetailBinding
import dev.hmh.nanopol.ui.main.worker_detail.common.LastHashRateReportViewModel
import dev.hmh.nanopol.ui.main.worker_detail.common.WorkerAvgHashRateViewModel
import dev.hmh.nanopol.ui.main.worker_detail.common.WorkerChartViewModel
import dev.hmh.nanopol.ui.main.worker_detail.common.WorkerCurrentHashRateViewModel
import kotlinx.coroutines.flow.collect
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class WorkerDetailActivity : AppCompatActivity() {

    private val lastHashRateReportViewModel: LastHashRateReportViewModel by viewModels()
    private val workerCurrentHashRateViewModel: WorkerCurrentHashRateViewModel by viewModels()
    private val workerAvgHashRateViewModel: WorkerAvgHashRateViewModel by viewModels()
    private val workerChartViewModel: WorkerChartViewModel by viewModels()


    lateinit var strWorkerId: String
    lateinit var strAccountNO: String

    lateinit var arrHashRateBarChart: ArrayList<HashRateChart>
    lateinit var arrSharedBarChart: ArrayList<SharedChart>
    private val df = DecimalFormat("0.00")
    private val sdf = SimpleDateFormat("HH:mm")


    private val binding: ActivityWorkerDetailBinding by lazy {
        ActivityWorkerDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide()


        val bundle = intent.extras!!
        if (!bundle.isEmpty) {
            binding.apply {
                strWorkerId = bundle.getString("id").toString()
                strAccountNO = bundle.getString("Acc").toString()
                txtTitle.text = "Worker: " + strWorkerId
            }

        }
        lastHashRateReportViewModel.getLastHashRateReport(strAccountNO, strWorkerId)
        backgroundGetLastHashRateResponseTask()
        workerCurrentHashRateViewModel.getWorkerCurrentHashRate(strAccountNO, strWorkerId)
        backgroundGetCurrentHashRate()
        workerAvgHashRateViewModel.workerAvgHashRate(strAccountNO, strWorkerId)
        backgroundGetWorkerAvgHashRateTask()
        workerChartViewModel.getWorkerChart(strAccountNO, strWorkerId)
        backgroundGetChartDetailTask()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            lastHashRateReportViewModel.getLastHashRateReport(strAccountNO, strWorkerId)
            backgroundGetLastHashRateResponseTask()
            workerCurrentHashRateViewModel.getWorkerCurrentHashRate(strAccountNO, strWorkerId)
            backgroundGetCurrentHashRate()
            workerAvgHashRateViewModel.workerAvgHashRate(strAccountNO, strWorkerId)
            backgroundGetWorkerAvgHashRateTask()
            workerChartViewModel.getWorkerChart(strAccountNO, strWorkerId)
            backgroundGetChartDetailTask()
        }


    }


    private fun backgroundGetWorkerAvgHashRateTask() {
        lifecycleScope.launchWhenCreated {
            workerAvgHashRateViewModel.responseWorkerAvgHashRate.collect {
                when (it) {
                    is WorkerAvgHashRateViewModel.WorkerAvgHashRateEvent.Empty -> {
                    }
                    is WorkerAvgHashRateViewModel.WorkerAvgHashRateEvent.Failure -> {
                        Toast.makeText(
                            this@WorkerDetailActivity,
                            "${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is WorkerAvgHashRateViewModel.WorkerAvgHashRateEvent.Loading -> {
                    }

                    is WorkerAvgHashRateViewModel.WorkerAvgHashRateEvent.Success -> {
                        binding.apply {
                            txtAverageHashRate.text = it.data.let { data ->
                                df.format(data.data!!.h6.toDouble()).toString()+" Mh/s"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun backgroundGetCurrentHashRate() {
        lifecycleScope.launchWhenCreated {
            workerCurrentHashRateViewModel.responseWorkerCurrentHashRate.collect {
                when (it) {
                    is WorkerCurrentHashRateViewModel.WorkerCurrentHashRateEvent.Empty -> {
                    }
                    is WorkerCurrentHashRateViewModel.WorkerCurrentHashRateEvent.Failure -> {
                        Toast.makeText(
                            this@WorkerDetailActivity,
                            "${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is WorkerCurrentHashRateViewModel.WorkerCurrentHashRateEvent.Loading -> {
                    }
                    is WorkerCurrentHashRateViewModel.WorkerCurrentHashRateEvent.Success -> {
                        it.data.let { data ->
                            binding.apply {
                                txtCurrentHashRate.text =
                                    df.format(data.data?.toDouble()).toString()+" Mh/s"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun backgroundGetLastHashRateResponseTask() {
        lifecycleScope.launchWhenCreated {
            lastHashRateReportViewModel.responseLastHashRateReport.collect {
                when (it) {
                    is LastHashRateReportViewModel.LastHashRateReportEvent.Empty -> {
                    }
                    is LastHashRateReportViewModel.LastHashRateReportEvent.Failure -> {
                        Toast.makeText(
                            this@WorkerDetailActivity,
                            "${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is LastHashRateReportViewModel.LastHashRateReportEvent.Loading -> {

                    }
                    is LastHashRateReportViewModel.LastHashRateReportEvent.Success -> {
                        it.data.let { data ->
                            binding.apply {
                                txtLashReport.text = df.format(data.data?.toDouble()).toString()+" Mh/s"
                            }
                        }
                    }
                }
            }
        }

    }

    private fun backgroundGetChartDetailTask() {
        lifecycleScope.launchWhenCreated {
            workerChartViewModel.responseWorkerChart.collect {
                when (it) {
                    is WorkerChartViewModel.WorkerChartEvent.Empty -> {
                    }
                    is WorkerChartViewModel.WorkerChartEvent.Failure -> {
                        Toast.makeText(
                            this@WorkerDetailActivity,
                            "${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is WorkerChartViewModel.WorkerChartEvent.Loading -> {
                    }
                    is WorkerChartViewModel.WorkerChartEvent.Success -> {

                        arrHashRateBarChart = ArrayList()
                        arrSharedBarChart = ArrayList()

                        arrHashRateBarChart.clear()
                        arrSharedBarChart.clear()

                        val result = it.data.data
                        if (result.isNotEmpty()) {

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
                                hashRateEntries.add(
                                    BarEntry(
                                        i.toFloat(),
                                        hashRate.hashRate.toFloat()
                                    )
                                )
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
                                sharedEntries.add(
                                    BarEntry(
                                        i.toFloat(),
                                        shared.sharedChart.toFloat()
                                    )
                                )
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