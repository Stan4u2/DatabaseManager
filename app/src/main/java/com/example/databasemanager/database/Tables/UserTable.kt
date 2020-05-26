package com.example.databasemanager.database.Tables

import android.provider.BaseColumns

object UserTable {
    class UserEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "User"
            val ID_USER = "id_user"
            val USERNAME_COL = "username"
            val PASSWORD_COL = "password"
        }
    }
}