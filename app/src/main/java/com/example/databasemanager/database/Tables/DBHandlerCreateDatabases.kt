package com.example.databasemanager.database.Tables

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHandlerCreateDatabases(
    context: Context,
    name: String,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getTableList(): ArrayList<Tables> {
        val db = this.writableDatabase
        val tables = ArrayList<Tables>()
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type = 'table' AND name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%'",
                null
            )
        } catch (e: SQLiteException) {
            Log.d("Error", e.toString())
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {

                var nameTable = cursor.getString(cursor.getColumnIndex("name"))

                var table: Tables = Tables(nameTable)

                tables.add(table)
                cursor.moveToNext()
            }
        }
        return tables
    }

    fun getColumnList(table: Tables): ArrayList<Column> {
        val db = this.writableDatabase
        val columns = ArrayList<Column>()
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(
                "PRAGMA table_info(${table.name_table})", null
            )
        } catch (e: SQLiteException) {
            Log.d("Error", e.toString())
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                var primaryKeyColumn = cursor.getString(cursor.getColumnIndex("pk")).toInt() == 1
                var nameColumn = cursor.getString(cursor.getColumnIndex("name"))
                var typeColumn = cursor.getString(cursor.getColumnIndex("type"))
                var nullColumn = cursor.getString(cursor.getColumnIndex("notnull")).toInt() == 1
                var defaultColumn: String = ""
                if (cursor.getString(cursor.getColumnIndex("dflt_value")) != null) {
                    defaultColumn = cursor.getString(cursor.getColumnIndex("dflt_value"))
                }

                val result = getColumnSpecificData(table.name_table, nameColumn)

                var column: Column = Column(
                    nameColumn,
                    typeColumn,
                    primaryKeyColumn,
                    result.first,
                    result.second,
                    nullColumn,
                    defaultColumn
                )

                columns.add(column)
                cursor.moveToNext()
            }
        }
        return columns
    }

    private fun getColumnSpecificData(
        tableName: String,
        columnName: String
    ): Pair<Boolean, Boolean> {
        val db = this.writableDatabase
        var sql: String = ""
        var hasAutoIncrement: Boolean = false
        var hasUnique: Boolean = false
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(
                //This sql gets the whole syntax from the table
                "SELECT sql FROM sqlite_master WHERE name = '$tableName'", null
            )
        } catch (e: SQLiteException) {
            Log.d("Error", e.toString())
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                sql = cursor.getString(cursor.getColumnIndex("sql"))
                cursor.moveToNext()
            }
        }

        //First I separate it by commas, parentheses
        var SQLSyntax = sql.split(",", "(", ")")

        for (x in SQLSyntax.indices) {
            //Finds the name of the column in the syntax
            if (SQLSyntax[x].contains(columnName, ignoreCase = true)) {
                //Checks if it has Autoincrement
                if (SQLSyntax[x].contains("AUTOINCREMENT", ignoreCase = true)) {
                    hasAutoIncrement = true
                }
                //Checks if it has Unique
                if (SQLSyntax[x].contains("UNIQUE", ignoreCase = true)) {
                    hasUnique = true
                }
            }
        }
        return Pair(hasAutoIncrement, hasUnique)
    }

    fun createTable(tables: Tables?, column: ArrayList<Column>) {
        var completeSytaxt: String = "("
        val db = this.writableDatabase
        var CREATE_DATABASE_TABLES =
            ("CREATE TABLE ${tables?.name_table} ")

        for (x in 0 until column.size) {
            completeSytaxt = completeSytaxt.plus(selectSyntaxt(column[x]))
            if (x != column.size.minus(1)) {
                completeSytaxt = completeSytaxt.plus(", ")
            }
        }
        completeSytaxt = completeSytaxt.plus(")")

        CREATE_DATABASE_TABLES = CREATE_DATABASE_TABLES.plus(completeSytaxt)
        println(CREATE_DATABASE_TABLES)

        db.execSQL(CREATE_DATABASE_TABLES)
        db.close()
    }

    fun selectSyntaxt(column: Column): String {
        //Name
        var columnSyntaxt: String = column.name_column
        //DataType
        columnSyntaxt = columnSyntaxt.plus(" ").plus(column.data_type)
        //Primary Key
        if (column.primary_key) {
            columnSyntaxt = columnSyntaxt.plus(" ").plus("PRIMARY KEY")
            if (column.auto_increment) {
                columnSyntaxt = columnSyntaxt.plus(" ").plus("AUTOINCREMENT")
            }
        }
        //Unique
        if (column.unique) {
            columnSyntaxt = columnSyntaxt.plus(" ").plus("UNIQUE")
        }
        //Not null
        if (column.not_null) {
            columnSyntaxt = columnSyntaxt.plus(" ").plus("NOT NULL")
        }
        println(column.default)
        if (column.default != "") {
            if (column.data_type == "TEXT") {
                columnSyntaxt = columnSyntaxt.plus(" DEFAULT '").plus(column.default).plus("'")
            } else if (column.data_type == "INTEGER") {
                columnSyntaxt = columnSyntaxt.plus(" DEFAULT ").plus(column.default)
            }
        }
        return columnSyntaxt
    }

    fun dropTable(table: Tables?) {
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS ${table?.name_table}")
        db.close()
    }

    fun insert_data_table(table: Tables, data: MutableList<Column>): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        for (x in 0 until data.size) {
            when (data[x].data_type) {
                "INTEGER" -> values.put(data[x].name_column, data[x].value.toInt())
                "TEXT" -> values.put(data[x].name_column, data[x].value)
                "NUMERIC" -> values.put(data[x].name_column, data[x].value.toInt())
            }
        }
        val success = db.insert(table.name_table, null, values)
        db.close()
        Log.v("Data Inserted", "$success")
        return (Integer.parseInt("$success") != -1)
    }

    fun getTableData(table: Tables, column: MutableList<Column>): MutableList<Column>? {

        val db = this.writableDatabase
        var columnSend: MutableList<Column> = ArrayList()
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(
                //This sql gets the whole syntax from the table
                "SELECT * FROM ${table.name_table}", null
            )
        } catch (e: SQLiteException) {
            Log.d("Error", e.toString())
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                for (x in 0 until column.size) {
                    println(
                        column[x].name_column + " " + cursor.getString(
                            cursor.getColumnIndex(
                                column[x].name_column
                            )
                        )
                    )

                    var columnCaught = Column(
                        column[x].name_column,
                        "",
                        false,
                        false,
                        false,
                        false,
                        "",
                        cursor.getString(cursor.getColumnIndex(column[x].name_column))
                    )
                    columnSend!!.add(columnCaught)
                }
                cursor.moveToNext()
            }
        }

        return columnSend
    }

    fun getCustomSelect(select: String, columns: MutableList<Column>): MutableList<Column> {
        val db = this.writableDatabase
        var resultSend: MutableList<Column> = ArrayList()
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(select, null)
        } catch (e: SQLiteException) {
            Log.d("Error ", e.toString())
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                for (x in 0 until columns.size) {
                    var columnCaught = Column(
                        columns[x].name_column,
                        "",
                        false,
                        false,
                        false,
                        false,
                        "",
                        cursor.getString(cursor.getColumnIndex(columns[x].name_column))
                    )
                    resultSend.add(columnCaught)
                }
                cursor.moveToNext()
            }
        }
        return resultSend
    }
}