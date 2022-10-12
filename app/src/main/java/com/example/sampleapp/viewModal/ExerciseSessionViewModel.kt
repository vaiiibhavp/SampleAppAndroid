package com.example.sampleapp.viewModal

import android.os.RemoteException
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.sampleapp.app.ServiceApp
import com.example.sampleapp.app.ServiceApp.Companion.context
import com.example.sampleapp.modal.gymListResponce.GymListResponce
import com.example.sampleapp.modal.health.stepCountModal
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.List
import kotlin.random.Random

class ExerciseSessionViewModel : BaseViewModel() {

    val healthConnectManager = (context as ServiceApp).healthConnectManager

    val permissions = setOf(
        HealthPermission.createReadPermission(StepsRecord::class),
        HealthPermission.createWritePermission(StepsRecord::class),
        HealthPermission.createReadPermission(HeartRateRecord::class),
        HealthPermission.createWritePermission(HeartRateRecord::class)
    )

    var permissionsGranted = mutableStateOf(false)
        private set

    internal var stepRecordData = MutableLiveData<List<StepsRecord>>()

    var uiState: UiState by mutableStateOf(UiState.Uninitialized)
        private set

    val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()

    fun initialLoad() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                // healthConnectManager.deleteStepsByTimeRange()

                readExerciseSessions()
            }
        }
    }

    fun TotalStepDayCount(startTime: Instant, endTime: Instant) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                healthConnectManager.aggregateStepsIntoMonths(startTime, endTime)

            }
        }
    }

    fun insertExerciseSession(stepCount: Int) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
                val latestStartOfSession = ZonedDateTime.now().minusMinutes(30)
                val offset = Random.nextDouble()

                val startOfSession = startOfDay.plusSeconds(
                    (Duration.between(startOfDay, latestStartOfSession).seconds * offset).toLong()
                )
                val endOfSession = startOfSession.plusMinutes(30)

                healthConnectManager.writeExerciseSession(startOfSession, endOfSession, stepCount)
            }
        }
    }

    fun deleteExerciseSession(uid: String) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                healthConnectManager.deleteExerciseSession(uid)
                readExerciseSessions()
            }
        }
    }

    private suspend fun readExerciseSessions() {
        healthConnectManager.readStepsByTimeRange().observeForever {
            it?.let {
                stepRecordData.value = it
            }
        }

    }

    private suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
        permissionsGranted.value = healthConnectManager.hasAllPermissions(permissions)
        uiState = try {
            if (permissionsGranted.value) {
                block()
            }
            UiState.Done
        } catch (remoteException: RemoteException) {
            UiState.Error(remoteException)
        } catch (securityException: SecurityException) {
            UiState.Error(securityException)
        } catch (ioException: IOException) {
            UiState.Error(ioException)
        } catch (illegalStateException: IllegalStateException) {
            UiState.Error(illegalStateException)
        }
    }

    sealed class UiState {
        object Uninitialized : UiState()
        object Done : UiState()

        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }
}


