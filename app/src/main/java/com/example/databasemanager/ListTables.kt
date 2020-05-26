package com.example.databasemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.databasemanager.Adapter.TableAdapter
import com.example.databasemanager.database.MyDBHandler
import com.example.databasemanager.database.Tables.DBHandlerCreateDatabases
import com.example.databasemanager.database.Tables.Databases
import com.example.databasemanager.database.Tables.Tables

class ListTables : AppCompatActivity() {

    var dbHandler: DBHandlerCreateDatabases? = null
    var database: Databases? = null

    val tableAdapter: TableAdapter = TableAdapter()

    lateinit var addTableButton: Button
    lateinit var tablesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_tables)

        addTableButton = findViewById(R.id.addTableButton)
        tablesRecyclerView = findViewById(R.id.tablesRecyclerView)
    }

    override fun onStart() {
        super.onStart()
        //This part will run once on create is done
        var bundle: Bundle = intent.extras!!
        if (!bundle.isEmpty) {
            database = bundle.getSerializable("Database") as Databases
            dbHandler = DBHandlerCreateDatabases(
                this,
                database!!.name_database,
                null,
                database!!.version_database
            )
            setupRecyclerView()
        }
    }

    override fun onRestart() {
        super.onRestart()
        setupRecyclerView()
    }

    fun setupRecyclerView() {
        tablesRecyclerView.setHasFixedSize(true)
        tablesRecyclerView.layoutManager = LinearLayoutManager(this)
        tableAdapter.TableAdapter(getListTables(), database, this)
        tablesRecyclerView.adapter = tableAdapter
    }

    private fun getListTables(): MutableList<Tables> {
        var result = dbHandler!!.getTableList()

        var tables: MutableList<Tables> = ArrayList()
        result.forEach {
            tables.add(Tables(it.name_table))
            println("Table name: ${it.name_table}")
        }
        return tables
    }

    fun addTable(view: View) {
        val intent = Intent(applicationContext, InsertTable::class.java)
        var bundle = Bundle()
        bundle.putSerializable("Database", database)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    fun customSearch(view: View) {
        val intent = Intent(applicationContext, SelectData::class.java)
        var bundle = Bundle()
        bundle.putSerializable("Database", database)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}
