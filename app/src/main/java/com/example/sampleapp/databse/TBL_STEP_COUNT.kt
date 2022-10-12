package com.example.sampleapp.databse

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.sampleapp.modal.StepCountDBModel

class TBL_STEP_COUNT(var context: Context) {
    var mdb: DB_Handler? = null
    var db: SQLiteDatabase? = null
    fun add_record(
        machine_id: String?,
        steps: Long?,
        workoutDate: String?,
        workoutTime: String?
    ): Long {
        var last_id: Long = 0
        try {
            val cv = ContentValues()
            cv.put(KEY_Machine_Id, machine_id)
            cv.put(KEY_STEP_COUNT, steps)
            cv.put(KEY_Date, workoutDate)
            cv.put(KEY_WorkoutTime, workoutTime)

            last_id = mdb!!.insert(cv, TBL_NAME)
        } catch (ex: Exception) {
            Log.e(TAG, "Error :" + ex.message)
        }
        return last_id
    }

    fun getGraph(date1: String, date2: String, isSport: String?,machineId:String?): List<StepCountDBModel> {
        val array: MutableList<StepCountDBModel> = ArrayList()
        try {
            var str = ""
            // var str = "select * from " + TBL_NAME + " where "

            if (!date1.isEmpty()) {
                if (!date2.isEmpty()) {
                    str = "SELECT * FROM " + TBL_NAME +
                            " WHERE " + KEY_Date +
                            " BETWEEN '" + date1 + "' AND '" + date2 + "' AND " +
                            KEY_Machine_Id+ " = '"+ machineId + "'"
                } else {
                    str = "SELECT * FROM " + TBL_NAME +
                            " WHERE " + KEY_Date +
                            " = '" + date1+ "' AND " +
                            KEY_Machine_Id+ " = '"+ machineId + "'"
                }
            }
            /* if (is_uploaded) {
                 str = " and " + KEY_isUploaded + " = '1'  "
             }*/
            // Log.e(TAG, "Query:" + str)
            val cr = mdb!!.fetch(str)
            if (cr != null) {
                if (cr.moveToFirst()) {
                    do {
                        array.add(
                            StepCountDBModel(
                                cr.getInt(cr.getColumnIndex(KEY_ID)), cr.getString(
                                    cr.getColumnIndex(KEY_Machine_Id)
                                ), cr.getString(cr.getColumnIndex(KEY_STEP_COUNT)),
                                cr.getString(cr.getColumnIndex(KEY_Date)),
                                cr.getString(cr.getColumnIndex(KEY_WorkoutTime))
                            )
                        )
                    } while (cr.moveToNext())
                }
                cr.close()
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error- update :" + ex.message)
        }
        return array
    }

    fun delete(): Boolean {
        var flag = true
        try {
            val str = "DELETE FROM " + TBL_NAME +
                    " WHERE " + KEY_Date

            mdb!!.query(str)
        } catch (ex: Exception) {
            Log.e(TAG, "Error- delete :" + ex.message)
            flag = false
        }
        return flag
    }

    fun update_key(id: String, key: String, `val`: String): Boolean {
        var flag = true
        try {
            val str =
                "update " + TBL_NAME + " set " + key + " = '" + `val` + "' where  " + KEY_ID + "=" + id
            mdb!!.query(str)
        } catch (ex: Exception) {
            Log.e(TAG, "Error- delete :" + ex.message)
            flag = false
        }
        return flag
    }

    companion object {
        const val TAG = "TBL_location"
        const val TBL_NAME = "tbl_step"
        const val KEY_ID = "id"
        const val KEY_Machine_Id = "machine_id"
        const val KEY_STEP_COUNT = "step_count"
        const val KEY_WorkoutTime = "time_workout"
        const val KEY_Date = "date_workout"
        const val CREATE_TABLE =
            "Create table if not exists " + TBL_NAME + "(" + KEY_ID + " integer primary key , " + KEY_Machine_Id + "  text,  " + KEY_STEP_COUNT + " text, " +
                      KEY_WorkoutTime + " real default(0), "  + KEY_Date + " text);"
        const val DELETE_TABLE = "Drop table if exists " + TBL_NAME + ";"
    }

    init {
        mdb = DB_Handler(context)
    }
}