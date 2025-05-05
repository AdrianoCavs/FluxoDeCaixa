package com.cavstecnologia.fluxodecaixa_pos2025.database

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getDoubleOrNull
import com.cavstecnologia.fluxodecaixa_pos2025.entity.CashFlowEntry

class DatabaseHandler (context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        public const val DATABASE_NAME = "dbfile.sqlite";
        public const val DATABASE_VERSION = 1;
        public const val TABLE_NAME = "cash_flow";
        public const val COLUMN_TYPE = 1;
        public const val COLUMN_DETAIL = 2;
        public const val COLUMN_VALUE = 3;
        public const val COLUMN_DATE = 4;

    }

    //SELECT (SELECT SUM(value) FROM cash_flow WHERE type LIKE 'C%')-(SELECT SUM(value) FROM cash_flow WHERE type LIKE 'D%');

    fun getCashFlowBalance() : Double {
        val db = this.readableDatabase;
        //val cursor : Cursor = db.rawQuery("SELECT (SELECT SUM(value) FROM ${TABLE_NAME} WHERE type LIKE 'C%')-(SELECT SUM(value) FROM ${TABLE_NAME} WHERE type LIKE 'D%') AS BALANCE;", null);

        var cursor : Cursor = db.rawQuery("SELECT SUM(value) FROM cash_flow WHERE type LIKE 'C%';", null);
        cursor.moveToFirst();
        val credit = cursor.getDouble(0);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(cursor));
        Log.d(TAG, "Valor lido do cursor CREDITO = " + cursor.getDouble(0));

        cursor =  db.rawQuery("SELECT SUM(value) FROM cash_flow WHERE type LIKE 'D%';", null)
        cursor.moveToFirst();
        val debit = cursor.getDouble(0);

        Log.d(TAG, DatabaseUtils.dumpCursorToString(cursor));
        Log.d(TAG, "Valor lido do cursor DEBITO = " + cursor.getDouble(0));

        val balance = credit - debit;

        //Imprimindo o resultado do cursor resultado do select para o logcat

        cursor.close();
        Log.d(TAG, "Valor na var balance = " + balance);
        return balance;
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS ${TABLE_NAME} (_id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, detail TEXT, value REAL, date TEXT)");
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${TABLE_NAME}")
        onCreate(db);
    }

    fun insert(cashFlowEntry: CashFlowEntry){
        val db : SQLiteDatabase = this.writableDatabase;

        val record = ContentValues();
        record.put("type", cashFlowEntry.type);
        record.put("detail", cashFlowEntry.detail);
        record.put("value", cashFlowEntry.value);
        record.put("date", cashFlowEntry.date);

        db.insert(TABLE_NAME, null, record);
    }

    fun update (cashFlowEntry: CashFlowEntry ){
        val db : SQLiteDatabase = this.writableDatabase;

        val record = ContentValues();
        record.put("type", cashFlowEntry.type);
        record.put("detail", cashFlowEntry.detail);
        record.put("value", cashFlowEntry.value);
        record.put("date", cashFlowEntry.date);

        db.update(TABLE_NAME, record,"_id=${cashFlowEntry._id}", null);
    }

    fun list() : Cursor{
        val db = this.readableDatabase;
        val records = db.query(TABLE_NAME, null, null, null, null, null, null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(records));
        return records;
    }
}

