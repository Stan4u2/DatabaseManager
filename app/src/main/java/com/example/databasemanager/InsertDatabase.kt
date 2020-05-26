package com.example.databasemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.databasemanager.database.MyDBHandler
import com.example.databasemanager.database.Tables.DBHandlerCreateDatabases
import com.example.databasemanager.database.Tables.Databases

class InsertDatabase : AppCompatActivity() {

    lateinit var initiateCreationDatabase: Button
    lateinit var cancelCreationDatabase: Button
    lateinit var nameDatabaseEditText: EditText

    var dbHandler: MyDBHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_database)

        dbHandler = MyDBHandler(this)

        //Buttons
        initiateCreationDatabase = findViewById(R.id.initiateCreationDatabase)
        cancelCreationDatabase = findViewById(R.id.cancelCreationDatabase)

        //EditText
        nameDatabaseEditText = findViewById(R.id.nameDatabaseEditText)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels

        window.setLayout(((width * .50).toInt()), ((height * .50).toInt()))

        initiateCreationDatabase.setOnClickListener { createDatabase() }
    }

    private fun createDatabase() {
        var nameDatabase = nameDatabaseEditText.text.toString()
        if (nameDatabase.isEmpty()) {
            Toast.makeText(this, "Name Database Empty", Toast.LENGTH_SHORT).show()
            return
        }

        val databases: Databases = Databases(null, nameDatabase, 1)
        var exists = dbHandler!!.find_database(databases)

        if (exists) {
            Toast.makeText(this, "This Database Already Exists", Toast.LENGTH_SHORT).show()
            return
        } else {
            dbHandler!!.insert_new_database(databases)
            finish()
            getBack(true)
        }
    }

    fun cancelCreation(view: View) {
        finish()
        getBack(false)
    }

    private fun getBack(result: Boolean) {
        val intent = Intent(this, MainMenu::class.java)
        intent.putExtra("DatabaseCreated", result)
        startActivity(intent)
    }
}
