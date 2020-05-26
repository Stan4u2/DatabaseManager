package com.example.databasemanager.database.Tables

import android.provider.BaseColumns

object DatabaseTable {
    class DatabaseEntry : BaseColumns{
        companion object{
            val TABLE_NAME = "Databases"
            val ID_DATABASE = "id_database"
            val DATABASE_NAME_COL = "database_name"
            val DATABASE_VERSION_COL = "database_version"
        }
    }
}