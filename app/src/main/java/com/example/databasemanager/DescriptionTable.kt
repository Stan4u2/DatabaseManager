package com.example.databasemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.databasemanager.Adapter.ColumnAdapter
import com.example.databasemanager.database.Tables.Column
import com.example.databasemanager.database.Tables.DBHandlerCreateDatabases
import com.example.databasemanager.database.Tables.Databases
import com.example.databasemanager.database.Tables.Tables

class DescriptionTable : AppCompatActivity() {

    lateinit var nameTableTextView: TextView
    lateinit var columnsRecyclerView: RecyclerView
    lateinit var insertDataTable: Button
    lateinit var columnDescriptionButton: Button
    lateinit var dataTableButton: Button

    var dbHandler: DBHandlerCreateDatabases? = null
    var database: Databases? = null
    private lateinit var table: Tables

    val columnAdapter: ColumnAdapter = ColumnAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description_table)

        nameTableTextView = findViewById(R.id.nameTableTextView)
        columnsRecyclerView = findViewById(R.id.columnsRecyclerView)
        insertDataTable = findViewById(R.id.insertDataTable)

        insertDataTable.setOnClickListener {
            val intent = Intent(this, InsertData::class.java)
            var bundle = Bundle()
            bundle.putSerializable("Database", database)
            bundle.putSerializable("Table", table.name_table)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        //This part will run once on create is done
        var bundle: Bundle = intent.extras!!
        if (!bundle.isEmpty) {
            database = bundle.getSerializable("Database") as Databases
            table = Tables(bundle.getSerializable("Table").toString())
            nameTableTextView.text = table.name_table
            dbHandler = DBHandlerCreateDatabases(
                this,
                database!!.name_database,
                null,
                database!!.version_database
            )
            setupRecyclerView()
        }
    }

    private fun setupRecyclerView() {
        columnsRecyclerView.setHasFixedSize(true)
        columnsRecyclerView.layoutManager = LinearLayoutManager(this)
        columnAdapter.ColumnAdapter(getListColumns(), database, applicationContext)
        columnsRecyclerView.adapter = columnAdapter
    }

    private fun getListColumns(): MutableList<Column> {
        var result = dbHandler!!.getColumnList(table)

        var columns: MutableList<Column> = ArrayList()
        result.forEach {
            columns.add(Column(it.name_column, it.data_type, it.primary_key, it.auto_increment, it.unique, it.not_null, it.default))
        }
        return columns
    }
}
