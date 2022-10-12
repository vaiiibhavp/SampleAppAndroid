package com.example.sampleapp.databse

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DB_Handler(con: Context) : SQLiteOpenHelper(con, DB_NAME, null, Version) {
    val TAG = "DB_Handler"
    val instanceWriter: SQLiteDatabase?
        get() {
            if (db_writer == null) {
                db_writer = this.writableDatabase
            }
            return db_writer
        }
    val instanceReader: SQLiteDatabase?
        get() {
            if (db_reader == null) {
                db_reader = this.writableDatabase
            }
            return db_reader
        }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(TBL_STEP_COUNT.CREATE_TABLE)

        } catch (ex: Exception) {
            Log.e(TAG, "Error: " + ex.message)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion) {
            db.execSQL(TBL_STEP_COUNT.DELETE_TABLE)

        }
        onCreate(db)
    }

    @Throws(SQLException::class)
    fun open(): SQLiteDatabase {
        return DB_Handler(context).writableDatabase
    }

    override fun close() {
        //  getInstance().close();
        close()
    }

    fun insert(cv: ContentValues?, tbl: String): Long {
        Log.i(TAG, "TABLE :$tbl")
        var i: Long = 0
        var db: SQLiteDatabase? = null
        try {
            db = open()
            i = db.insert(tbl, null, cv)
            db.close()
        } catch (ex: Exception) {
            Log.e(TAG, "Error: 172: " + ex.message)
        } finally {
            if (db != null) {
                if (db.isOpen) {
                    db.close()
                }
            }
        }
        return i
    }

    fun fetch(query: String): Cursor? {
        Log.i(TAG, "STR :$query")
        var cursor: Cursor? = null
        try {
            cursor = instanceReader!!.rawQuery(query, null)
            return cursor
        } catch (ex: Exception) {
            Log.e(TAG, "Error: 182: " + ex.message)
        }
        return cursor
    }

    fun update(tbl: String, cv: ContentValues?, whereClause: String?, arr: Array<String?>?): Int {
        Log.i(TAG, "TABLE :$tbl")
        var i = 0
        var db: SQLiteDatabase? = null
        try {
            db = open()
            i = db.update(tbl, cv, whereClause, arr)
            db.close()
        } catch (ex: Exception) {
            Log.e(TAG, "Error: 182: " + ex.message)
        } finally {
            if (db != null) {
                if (db.isOpen) {
                    db.close()
                }
            }
        }
        return i
    }

    fun query(str: String) {
        Log.i(TAG, "SQL :$str")
        var db: SQLiteDatabase? = null
        try {
            db = open()
            db.execSQL(str)
            db.close()
        } catch (ex: Exception) {
            Log.e(TAG, "Error: 182: " + ex.message)
        } finally {
            if (db != null) {
                if (db.isOpen) {
                    db.close()
                }
            }
        }
    }

    companion object {
        const val DB_NAME = "db_sample.db"
        private lateinit var context: Context
        private const val Version = 4
        private val mInstance: DB_Handler? = null
        private var db_reader: SQLiteDatabase? = null
        private var db_writer: SQLiteDatabase? = null
    }

    init {
        context = con
    }
}