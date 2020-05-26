package com.example.databasemanager.database.Tables

class Column (var name_column: String, var data_type: String, var primary_key: Boolean, var auto_increment: Boolean, var unique: Boolean, var not_null: Boolean, var default: String, var value: String = "", var table: String = "")