package com.example.sampleapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleapp.Activity.GymInfoActivity
import com.example.sampleapp.Utils.Constants.DATE_FORMAT
import com.example.sampleapp.Utils.MyShare
import com.example.sampleapp.Utils.UtilityClass
import com.example.sampleapp.adapter.GymListAdapter
import com.example.sampleapp.databinding.ActivityMainBinding
import com.example.sampleapp.databse.TBL_STEP_COUNT
import com.example.sampleapp.gps.GPSTracker
import com.example.sampleapp.listener.StepListener
import com.example.sampleapp.modal.StepCountDBModel
import com.example.sampleapp.service.StepCallService
import com.example.sampleapp.viewModal.ExerciseSessionViewModel
import com.example.sampleapp.viewModal.GymListViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : ComponentActivity(), GymListAdapter.onClickListener {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(
            layoutInflater
        )
    }

    private val viewModel: GymListViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)
            .create(GymListViewModel::class.java)
    }

    private val exerciseSessionViewModel: ExerciseSessionViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)
            .create(ExerciseSessionViewModel::class.java)
    }

    companion object {
        private val SENSOR_CODE = 567

    }

    private var stepList: List<StepsRecord> = kotlin.collections.ArrayList()

    var STEPS = 0

    private val upperBounds = intArrayOf(500, 1000, 1500, 2000, 2500)
    private var adapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    var currentDate: String? = null
     var client: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionsLauncher =
            registerForActivityResult(exerciseSessionViewModel.permissionsLauncher) {}
        setContentView(binding.root)


        if (HealthConnectClient.isAvailable(this)) {
            var healthConnectClient = HealthConnectClient.getOrCreate(this)
            GlobalScope.launch {
                val granted =
                    healthConnectClient.permissionController.getGrantedPermissions(
                        exerciseSessionViewModel.permissions
                    )
                if (granted.containsAll(exerciseSessionViewModel.permissions)) {
                    exerciseSessionViewModel.let {
//                        if (STEPS != 0) {
//
//                        }
                        it.initialLoad()
                        lifecycleScope.launch {
                            withContext(Dispatchers.Main) {
                                it.stepRecordData.observeForever { data ->
                                    stepList = data!!
                                    println("fdsa::>>" + stepList)
                                    if (TBL_STEP_COUNT(this@MainActivity).delete()) {
                                        for (stepRecord in stepList) {
                                            val dateFormate = SimpleDateFormat("yyyy-MM-dd")
                                            val timeFormate = SimpleDateFormat("HH:mm")
                                            val myDate1 = Date.from(stepRecord.startTime)
                                            val todayDate = dateFormate.format(myDate1)
                                            val localTime = timeFormate.format(myDate1)
                                            TBL_STEP_COUNT(this@MainActivity).add_record(
                                                "2",
                                                stepRecord.count,
                                                todayDate,
                                                localTime
                                            )
                                        }
                                    }
                                    setupLineChartData()

                                }
                            }
                        }


                    }
                } else {
                    permissionsLauncher.launch(exerciseSessionViewModel.permissions)

                }
            }
        } else {

        }

        client = LocationServices.getFusedLocationProviderClient(this);

        val gpsTracker = GPSTracker(this)
        if (gpsTracker.isGPSTrackingEnabled) {

            if (androidx.core.app.ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && androidx.core.app.ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }

            client!!.lastLocation.addOnSuccessListener(
                this@MainActivity
            ) { location ->
                if (location != null) {
                    val stringLatitude = location.latitude.toString()
                    this.let {
                        MyShare(it).latitude = stringLatitude
                    }
                    val stringLongitude = location.longitude.toString()
                    this.let {
                        MyShare(it).logitude = stringLongitude
                    }
                }
            }

            val country = gpsTracker.getCountryName(this)
            val city = gpsTracker.getLocality(this)
            val postalCode = gpsTracker.getPostalCode(this)
            val addressLine = gpsTracker.getAddressLine(this)
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert()
        }
        this.let {
            if (MyShare(it).currentDate?.let { return@let it }
                    .equals(UtilityClass.getTodayDate(DATE_FORMAT))) {
                currentDate = MyShare(it).currentDate?.let { return@let it }
            } else {
                currentDate = UtilityClass.getTodayDate(DATE_FORMAT)
            }
        }

        this.let {
            MyShare(it).currentDate = currentDate
        }

        binding.recyleGymList.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        binding.recyleGymList.setLayoutManager(layoutManager)
        viewModel.let {
            it.getGymDetail("10", "1", this)
            it.gymDetailData.observeForever { data ->

                adapter = GymListAdapter(this, data.data, this)
                binding.recyleGymList.setAdapter(adapter)

            }
        }

        setupLineChartData()
        //  initializeGraph()
        val StepsIntent = Intent(applicationContext, StepListener::class.java)
        startService(StepsIntent)
        val stepCallService = Intent(applicationContext, StepCallService::class.java)
        startService(stepCallService)
        update()
    }


    fun update() {
        val handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                updateValues()
                val sdf = SimpleDateFormat("HH:mm:ss")
                sdf.timeZone = TimeZone.getTimeZone("IST")
                val timeNow = sdf.format(Date())
                if (timeNow == "00:00:00" || timeNow == "00:00:01" || timeNow == "00:00:02") {
                    STEPS = 0
                }
                handler.postDelayed(this, 1000)
            }
        })
    }

    fun updateValues() {

        stepCoutn()
        if (STEPS != 0) {
            binding.txtSteps.text = "$STEPS"
        } else {
            binding.txtSteps.text = "0"
        }

        val m = (STEPS * 0.762).toInt()
        for (i in upperBounds) {
            if (STEPS < i) {
                binding.progress!!.max = i
                break
            }
        }
        binding.progress.setProgress(STEPS)

    }

    fun stepCoutn() {
        this.let {
            if (MyShare(it).currentDate?.let { return@let it }
                    .equals(UtilityClass.getTodayDate(DATE_FORMAT))) {
                STEPS = MyShare(it).stepCount!!
            } else {
                STEPS = 0
            }
        }

    }

    var startWeekDate: String = ""
    var countDay: Int = 0
    var countWeek: Int = 0
    var countMonth: Int = 1
    var countYear: Int = 0
    var machineId: String = "2"

    private fun setupLineChartData() {
        val cale = Calendar.getInstance()
        cale[Calendar.DAY_OF_WEEK] = Calendar.SUNDAY
        cale[Calendar.HOUR_OF_DAY] = 0
        cale[Calendar.MINUTE] = 0
        cale[Calendar.SECOND] = 0
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val sundayPrev = df.format(cale.time)

        cale.add(Calendar.DATE, 7)
        val sunday = df.format(cale.time)

        cale.add(Calendar.DATE, 7)
        val today = df.format(cale.time)

        println("Today: $today")
        println("Monday of the Week: $sundayPrev")
        println("Sunday of the Week: $sunday")
        startWeekDate = sundayPrev.toString()

        val c: Calendar = Calendar.getInstance()
        val c1: Calendar = Calendar.getInstance()

        val dt = startWeekDate

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            c.setTime(sdf.parse(dt))
            c1.setTime(sdf.parse(dt))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        c.add(
            Calendar.DATE,
            countWeek
        )
        c1.add(
            Calendar.DATE,
            countWeek + 6
        )
        val sdf2 = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val sdf1 = SimpleDateFormat("dd", Locale.getDefault())
        val output: String = sdf1.format(c.getTime())
        val output1: String = sdf2.format(c1.getTime())

        getData("${sdf.format(c.getTime())}", "${sdf.format(c1.getTime())}", "w")

    }

    private fun getData(date1: String, date2: String, type: String) {
        var avgTemperature = 0.0

        var listUserSteps: MutableList<StepCountDBModel> = java.util.ArrayList()
        listUserSteps =
            TBL_STEP_COUNT(this).getGraph(
                date1,
                date2,
                "0",
                machineId
            ) as MutableList<StepCountDBModel>
        if (listUserSteps.size > 0) {
            if (type.equals("w")) {
                val y: ArrayList<String> = ArrayList()
                var tempDate = ""
                Collections.sort(listUserSteps,
                    Comparator<StepCountDBModel?> { o2, o1 ->
                        "${o1!!.workoutDate} ${o1.workouttime}".compareTo(
                            "${o2!!.workoutDate} ${o2.workouttime}"
                        )
                    })
                val list: MutableList<StepCountDBModel> = ArrayList()
                tempDate = listUserSteps[0].workoutDate
                var tempSteps = 0.0
                var count = 0
                for (j in 0 until listUserSteps.size) {
                    if (tempDate == listUserSteps[j].workoutDate) {
                        tempSteps = tempSteps.plus(listUserSteps[j].stepcount.toFloat())
                        count += 1
                    }
                }
                // if(tempDate ==listUserSteps[i].workoutDate ){
                list.add(
                    StepCountDBModel(
                        0,
                        "1",
                        (tempSteps / count).toString(),
                        tempDate,
                        tempSteps.toString(),

                        )
                )

                for (i in 0 until listUserSteps.size) {
                    if (listUserSteps[i].workoutDate != tempDate) {
                        tempDate = listUserSteps[i].workoutDate
                        //list.add(listUserSteps[i])
                        var tempHeart = 0.0
                        var count1 = 0
                        for (j in 0 until listUserSteps.size) {
                            if (tempDate == listUserSteps[j].workoutDate) {
                                tempHeart = tempHeart.plus(listUserSteps[j].stepcount.toFloat())
                                count1 += 1
                            }
                        }
                        list.add(
                            StepCountDBModel(
                                0,
                                "1",
                                (tempHeart / count1).toString(),
                                tempDate,
                                tempHeart.toString(),

                                )
                        )
                    }

                }
                list.reverse()
                for (i in 0 until list.size) {

                    avgTemperature = avgTemperature.plus(list.get(i).stepcount.toFloat())

                }

                var roubdOff = String.format("%.1f", (avgTemperature / list.size))
                updateGraphData(list, y, type)
            }

        } else {

        }
    }

//    private fun initializeGraph() {
//        binding.lineChart.setDrawBorders(false)
//        binding.lineChart.legend.isEnabled = false
//        binding.lineChart.description.isEnabled = false
//        // binding.lineChart.setTouchEnabled(false)
//
//        val xAxis = binding.lineChart.xAxis
//        xAxis.setDrawGridLines(false)
//        xAxis.setDrawLabels(true)
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.granularity = 1f
//
//        val yAxis = binding.lineChart.axisLeft
//        yAxis.axisLineWidth = 2.0F
//        yAxis.setDrawGridLines(false)
//        yAxis.setDrawLabels(true)
//        yAxis.setDrawTopYLabelEntry(true)
//        yAxis.axisMinimum = 0F
//        yAxis.setDrawZeroLine(true)
//        yAxis.zeroLineWidth = 2.0F
//        // Disable Right Y axis
//        binding.lineChart.axisRight.isEnabled = false
//    }

    private fun updateGraphData(
        stepsTableList: List<StepCountDBModel>,
        xAxisList: List<String>,
        type: String
    ) {

        println("fdsafd::>>" + xAxisList)
        //        val yVals = ArrayList<Entry>()
//        yVals.add(Entry(0f, 30f, "10 Mon"))
//        yVals.add(Entry(1f, 2f, "11 Tue"))
//        yVals.add(Entry(2f, 4f, "12 Wed"))
//        yVals.add(Entry(3f, 6f, "13 Thu"))
//        yVals.add(Entry(4f, 8f, "14 Fri"))
//        yVals.add(Entry(5f, 10f, "15 Sat"))
//        yVals.add(Entry(6f, 22f, "16 Sun"))
//
//        val set1: LineDataSet
//        set1 = LineDataSet(yVals, "Steps")
//
//        set1.color = resources.getColor(R.color.blue)
//        set1.setCircleColor(resources.getColor(R.color.purple_200))
//        set1.lineWidth = 5f
//        set1.circleRadius = 6f
//        set1.setDrawCircleHole(false)
//        set1.valueTextSize = 0f
//        set1.setDrawFilled(false)
//
//        val dataSets = ArrayList<ILineDataSet>()
//        dataSets.add(set1)
//        val data = LineData(dataSets)
//        val yAxisRight: YAxis = binding.lineChart.getAxisRight()
//        yAxisRight.isEnabled = false
//
//
//        val xAxisLabel: ArrayList<String> = ArrayList()
//        xAxisLabel.add("10 Mon")
//        xAxisLabel.add("11 Tue")
//        xAxisLabel.add("12 Wed")
//        xAxisLabel.add("13 Thu")
//        xAxisLabel.add("14 Fri")
//        xAxisLabel.add("15 Sat")
//        xAxisLabel.add("16 Sun")
//
//
//        val xAxis = binding.lineChart.xAxis
//        xAxis.setDrawGridLines(false)
//        xAxis.setDrawAxisLine(false)
//
//        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
//        xAxis.setDrawLabels(true)
//        xAxis.granularity = 1f
////        xAxis.labelRotationAngle = +90f
//
//        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabel)
//
//        // set data
//        binding.lineChart.setData(data)
//        binding.lineChart.description.isEnabled = false
//        binding.lineChart.legend.isEnabled = true
//        binding.lineChart.setPinchZoom(true)
//        binding.lineChart.setExtraOffsets(5f, 5f, 20F, 15f)
//
//        binding.lineChart.xAxis.enableGridDashedLine(5f, 5f, 0f)
//        //lineChart.axisRight.enableGridDashedLine(5f, 5f, 0f)
//        binding.lineChart.axisLeft.enableGridDashedLine(5f, 5f, 0f)
//        //lineChart.setDrawGridBackground()
//        binding.lineChart.xAxis.labelCount = 11
//        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM


        val stepsDataSet = LineDataSet(getStepsDataSet(stepsTableList), "Steps")
        stepsDataSet.color = resources.getColor(R.color.blue)
        stepsDataSet.setCircleColor(resources.getColor(R.color.purple_200))
        stepsDataSet.lineWidth = 5f
        stepsDataSet.circleRadius = 6f
        stepsDataSet.setDrawCircleHole(false)
        stepsDataSet.valueTextSize = 0f
        stepsDataSet.setDrawFilled(false)
        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(stepsDataSet)
        val data = LineData(dataSets)
        val yAxisRight: YAxis = binding.lineChart.getAxisRight()
        yAxisRight.isEnabled = false


        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        binding.lineChart.xAxis.setDrawLabels(true)
        binding.lineChart.xAxis.granularity = 1f
//        xAxis.labelRotationAngle = +90f

        val xAxisLabel: ArrayList<String> = ArrayList()

        val cal = Calendar.getInstance()
        cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
        val sdf = SimpleDateFormat("dd EEE")

        val format = SimpleDateFormat("yyyy-MM-dd")

        for (i in stepsTableList) {
            val date: Date = format.parse(i.workoutDate)
            xAxisLabel.add(sdf.format(date))
            cal.add(Calendar.DAY_OF_WEEK, 1)
        }

        val xAxis = binding.lineChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        val xAxisLeft: YAxis = binding.lineChart.axisLeft
        xAxisLeft.isEnabled = true

        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
//        xAxis.labelRotationAngle = +90f

        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabel)

        // set data
        binding.lineChart.setData(data)
        binding.lineChart.description.isEnabled = false
        binding.lineChart.legend.isEnabled = true
        binding.lineChart.setPinchZoom(true)
        binding.lineChart.setExtraOffsets(5f, 5f, 20F, 15f)

        binding.lineChart.xAxis.enableGridDashedLine(5f, 5f, 0f)
        //lineChart.axisRight.enableGridDashedLine(5f, 5f, 0f)
        binding.lineChart.axisLeft.enableGridDashedLine(5f, 5f, 0f)
        //lineChart.setDrawGridBackground()
        binding.lineChart.xAxis.labelCount = 11
        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM


    }


    private fun getStepsDataSet(stepsTableList: List<StepCountDBModel>): java.util.ArrayList<Entry> {
        val stepsEntries = java.util.ArrayList<Entry>()
        var i = 0
        val cal = Calendar.getInstance()
        cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
        val sdf = SimpleDateFormat("dd EEE")
        val format = SimpleDateFormat("yyyy-MM-dd")
        for (stepsTable in stepsTableList) {
            val date: Date = format.parse(stepsTable.workoutDate)
            stepsEntries.add(Entry(i.toFloat(), stepsTable.workouttime.toFloat(), sdf.format(date)))
            cal.add(Calendar.DAY_OF_WEEK, 1)
            i += 1

        }

        return stepsEntries
    }

    override fun onViewItemClick(gymId: String) {
        var Intenttt = Intent(this, GymInfoActivity::class.java)
        Intenttt.putExtra("ID", gymId)
        startActivity(Intenttt)
    }

    fun getTodayDate(format: String?): String {
        val dateFormat: DateFormat =
            SimpleDateFormat(format, Locale.ENGLISH)
        val date = Date()
        return dateFormat.format(date)
    }

}