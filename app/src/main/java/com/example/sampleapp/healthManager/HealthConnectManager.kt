package com.example.sampleapp.healthManager

import android.content.Context
import android.os.Build
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.changes.Change
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sampleapp.modal.health.stepCountModal
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.Period
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*
import kotlin.reflect.KClass


const val MIN_SUPPORTED_SDK = Build.VERSION_CODES.O_MR1

class HealthConnectManager(private val context: Context) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    var availability = mutableStateOf(HealthConnectAvailability.NOT_SUPPORTED)
        private set

    init {
        checkAvailability()
    }

    fun checkAvailability() {
        availability.value = when {
            HealthConnectClient.isAvailable(context) -> HealthConnectAvailability.INSTALLED
            isSupported() -> HealthConnectAvailability.NOT_INSTALLED
            else -> HealthConnectAvailability.NOT_SUPPORTED
        }
    }

    suspend fun hasAllPermissions(permissions: Set<HealthPermission>): Boolean {
        return permissions == healthConnectClient.permissionController.getGrantedPermissions(
            permissions
        )
    }

    fun requestPermissionsActivityContract(): ActivityResultContract<Set<HealthPermission>, Set<HealthPermission>> {
        return healthConnectClient.permissionController.createRequestPermissionActivityContract()
    }

    suspend fun deleteStepsByTimeRange() {
        val truncatedToMonth: ZonedDateTime = ZonedDateTime.now(UTC).truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1)
        val startOfDay = truncatedToMonth.toInstant()
        val now = Instant.now()
        healthConnectClient.deleteRecords(
            StepsRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
        )
    }

    suspend fun readStepsByTimeRange() : LiveData<List<StepsRecord>> {
        var stepList : MutableLiveData<List<StepsRecord>> = MutableLiveData()
        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).with(TemporalAdjusters.previous(WeekFields.of(Locale.getDefault()).getFirstDayOfWeek()));
        val now = Instant.now()
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startOfDay.toInstant(), now)
                )
            )
        stepList.value = response.records

        return stepList
    }

    suspend fun aggregateStepsIntoMonths(startTime: Instant, endTime: Instant) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )
        for (monthlyResult in response) {
            // The result may be null if no data is available in the time range.
            val totalSteps = monthlyResult.result[StepsRecord.COUNT_TOTAL]
            println("dsadsa::>>"+totalSteps)
        }
    }

    suspend fun writeExerciseSession(start: ZonedDateTime, end: ZonedDateTime, stepCount: Int) {
        healthConnectClient.insertRecords(
            listOf(
                StepsRecord(
                    startTime = start.toInstant(),
                    startZoneOffset = start.offset,
                    endTime = end.toInstant(),
                    endZoneOffset = end.offset,
                    count = (stepCount).toLong()
                )
            )
        )
    }

    suspend fun deleteExerciseSession(uid: String) {
        val exerciseSession = healthConnectClient.readRecord(ExerciseSessionRecord::class, uid)
        healthConnectClient.deleteRecords(
            ExerciseSessionRecord::class,
            uidsList = listOf(uid),
            clientRecordIdsList = emptyList()
        )
        val timeRangeFilter = TimeRangeFilter.between(
            exerciseSession.record.startTime,
            exerciseSession.record.endTime
        )
        val rawDataTypes: Set<KClass<out Record>> = setOf(
            StepsRecord::class
        )
        rawDataTypes.forEach { rawType ->
            healthConnectClient.deleteRecords(rawType, timeRangeFilter)
        }
    }

    private fun isSupported() = Build.VERSION.SDK_INT >= MIN_SUPPORTED_SDK

    sealed class ChangesMessage {
        data class NoMoreChanges(val nextChangesToken: String) : ChangesMessage()
        data class ChangeList(val changes: List<Change>) : ChangesMessage()
    }
}

enum class HealthConnectAvailability {
    INSTALLED,
    NOT_INSTALLED,
    NOT_SUPPORTED
}
