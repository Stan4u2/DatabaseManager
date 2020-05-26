package com.example.databasemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.databasemanager.Adapter.DatabaseAdapter
import com.example.databasemanager.database.MyDBHandler
import com.example.databasemanager.database.Tables.Databases

var dbHandler: MyDBHandler? = null

class MainMenu : AppCompatActivity() {

    lateinit var databaseRecyclerView : RecyclerView
    val databaseAdapter : DatabaseAdapter = DatabaseAdapter()
    private lateinit var AddDatabaseButton: Button
    var dbHandler: MyDBHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        databaseRecyclerView = findViewById(R.id.databaseRecyclerView)
        AddDatabaseButton = findViewById(R.id.AddDatabaseButton)
        dbHandler = MyDBHandler(this)
        setupRecyclerView()


        AddDatabaseButton.setOnClickListener {
            val intent = Intent(this, InsertDatabase::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }

    fun setupRecyclerView(){
        databaseRecyclerView.setHasFixedSize(true)
        databaseRecyclerView.layoutManager = LinearLayoutManager(this)
        databaseAdapter.DatabaseAdapter(getDatabases(), this)
        databaseRecyclerView.adapter = databaseAdapter
    }

    fun getDatabases ():MutableList<Databases> {
        dbHandler = MyDBHandler(this)
        var result = dbHandler!!.getDatabasesList()

        var databases:MutableList<Databases> = ArrayList()
        result.forEach {
            databases.add(Databases(it.id_database, it.name_database, it.version_database))
        }
        return databases
    }
}
