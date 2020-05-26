package com.example.databasemanager.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.databasemanager.database.Tables.*
import com.example.databasemanager.database.Tables.Databases
import java.sql.SQLOutput

class MyDBHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "PrimaryDatabase"

        val CREATE_USER_TABLE = ("CREATE TABLE ${UserTable.UserEntry.TABLE_NAME} (" +
                " ${UserTable.UserEntry.ID_USER} INTEGER PRIMARY KEY AUTOINCREMENT, ${UserTable.UserEntry.USERNAME_COL} TEXT, ${UserTable.UserEntry.PASSWORD_COL} TEXT" + ")")

        val INSERT_ADMIN_USER =
            ("INSERT INTO ${UserTable.UserEntry.TABLE_NAME} (${UserTable.UserEntry.USERNAME_COL}, ${UserTable.UserEntry.PASSWORD_COL}) VALUES ('admin', 'admin')")

        val CREATE_DATABASE_TABLES =
            ("CREATE TABLE ${DatabaseTable.DatabaseEntry.TABLE_NAME} (" +
                    " ${DatabaseTable.DatabaseEntry.ID_DATABASE} INTEGER PRIMARY KEY AUTOINCREMENT, ${DatabaseTable.DatabaseEntry.DATABASE_NAME_COL} TEXT, ${DatabaseTable.DatabaseEntry.DATABASE_VERSION_COL} TEXT)")
    }

    override fun onCreate(db: SQLiteDatabase) {
        //creating table with fields
        db.execSQL(CREATE_USER_TABLE)
        db.execSQL(INSERT_ADMIN_USER)
        db.execSQL(CREATE_DATABASE_TABLES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        db.execSQL("DROP TABLE IF EXISTS ${UserTable.UserEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseTable.DatabaseEntry.TABLE_NAME}")
        onCreate(db)
    }

    fun addUser(user: User): Boolean {
        //Create and/or open a database that will be used for reading and writing.
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(UserTable.UserEntry.USERNAME_COL, user.username)
        values.put(UserTable.UserEntry.PASSWORD_COL, user.password)
        val _success = db.insert(UserTable.UserEntry.TABLE_NAME, null, values)
        db.close()
        Log.v("User Inserted", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }

    fun login(user: User): ArrayList<User> {
        val db = this.writableDatabase
        val users = ArrayList<User>()
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(
                "SELECT * FROM user WHERE username = '${user.username}' and password = '${user.password}'",
                null
            )
        } catch (e: SQLiteException) {
            Log.d("Error", e.toString())
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                user.id_user = cursor.getInt(cursor.getColumnIndex(UserTable.UserEntry.ID_USER))
                user.username =
                    cursor.getString(cursor.getColumnIndex(UserTable.UserEntry.USERNAME_COL))
                user.password =
                    cursor.getString(cursor.getColumnIndex(UserTable.UserEntry.PASSWORD_COL))

                users.add(user)
                cursor.moveToNext()
            }
        }
        return users
    }

    fun getDatabasesList(): ArrayList<Databases> {
        val db = this.writableDatabase
        val database = ArrayList<Databases>()
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM ${DatabaseTable.DatabaseEntry.TABLE_NAME}", null)
        } catch (e: SQLiteException) {
            Log.d("Error", e.toString())
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {

                var id =
                    cursor.getInt(cursor.getColumnIndex(DatabaseTable.DatabaseEntry.ID_DATABASE))
                var name =
                    cursor.getString(cursor.getColumnIndex(DatabaseTable.DatabaseEntry.DATABASE_NAME_COL))
                var version =
                    cursor.getInt(cursor.getColumnIndex(DatabaseTable.DatabaseEntry.DATABASE_VERSION_COL))

                var databases: Databases = Databases(id, name, version)

                database.add(databases)
                cursor.moveToNext()
            }
        }
        return database
    }

    fun find_database(databases: Databases): Boolean{

        val db = this.writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(
                "SELECT " +
                        "COUNT(*) " +
                        "FROM " +
                        "${DatabaseTable.DatabaseEntry.TABLE_NAME} " +
                        "WHERE " +
                        "${DatabaseTable.DatabaseEntry.DATABASE_NAME_COL} = '${databases.name_database}' COLLATE NOCASE",
                null
            )
        } catch (e: SQLiteException) {
            Log.d("Error", e.toString())
        }

        return if (cursor!!.moveToFirst()) {
            var count: Int = cursor.getInt(0)
            count > 0
        } else {
            true
        }
    }

    fun insert_new_database(databases: Databases): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DatabaseTable.DatabaseEntry.DATABASE_NAME_COL, databases.name_database)
        values.put(DatabaseTable.DatabaseEntry.DATABASE_VERSION_COL, databases.version_database)
        val _success = db.insert(DatabaseTable.DatabaseEntry.TABLE_NAME, null, values)
        db.close()
        Log.v("Database Inserted", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }

    fun delete_database(databases: Databases): Boolean {
        val db = this.writableDatabase
        return try {
            db.delete(DatabaseTable.DatabaseEntry.TABLE_NAME, "${DatabaseTable.DatabaseEntry.ID_DATABASE} = ${databases.id_database}", null)>0
        } catch (e: SQLiteException){
            Log.d("Error", e.toString())
            false
        }
    }
}