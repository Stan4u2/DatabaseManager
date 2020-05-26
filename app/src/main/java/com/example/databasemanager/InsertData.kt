package com.example.databasemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.databasemanager.Adapter.DataInsertAdapter
import com.example.databasemanager.database.Tables.Column
import com.example.databasemanager.database.Tables.DBHandlerCreateDatabases
import com.example.databasemanager.database.Tables.Databases
import com.example.databasemanager.database.Tables.Tables

class InsertData : AppCompatActivity(), DataInsertAdapter.CallBackInterface {

    lateinit var columnsInsertDataRecyclerView: RecyclerView
    lateinit var insertDataToTableButton: Button

    var dbHandler: DBHandlerCreateDatabases? = null
    var database: Databases? = null
    private lateinit var table: Tables

    val dataInsertAdapter: DataInsertAdapter = DataInsertAdapter(this)

    var columns: MutableList<Column> = ArrayList()
    //var input: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_data)

        columnsInsertDataRecyclerView = findViewById(R.id.columnsInsertDataRecyclerView)
        insertDataToTableButton = findViewById(R.id.insertDataToTableButton)
    }

    override fun onStart() {
        super.onStart()
        var bundle: Bundle = intent.extras!!
        if (!bundle.isEmpty) {
            database = bundle.getSerializable("Database") as Databases
            table = Tables(bundle.getSerializable("Table").toString())
            dbHandler = DBHandlerCreateDatabases(
                this,
                database!!.name_database,
                null,
                database!!.version_database
            )
            setupRecyclerView()
        }
    }

    override fun passDataCallBack(position: Int, text: String) {
        columns[position].value = text
        //input[position] = text
    }

    private fun setupRecyclerView() {
        columnsInsertDataRecyclerView.setHasFixedSize(true)
        columnsInsertDataRecyclerView.layoutManager = LinearLayoutManager(this)
        dataInsertAdapter.DataInsertAdapter(getListColumns(), applicationContext)
        columnsInsertDataRecyclerView.adapter = dataInsertAdapter
    }

    private fun getListColumns(): MutableList<Column> {
        var result = dbHandler!!.getColumnList(table)

        columns.clear()
        result.forEach {
            columns.add(Column(it.name_column, it.data_type, it.primary_key, it.auto_increment, it.unique, it.not_null, it.default))
            //input.add("")
        }
        return columns
    }

    fun checkInputs(view: View){
        for (x in 0 until columns.size){
            if (columns[x].value.isEmpty()){
                Toast.makeText(this, "The Input ${columns[x].name_column} is empty", Toast.LENGTH_SHORT).show()
                return
            }
        }
        insertData()
    }

    private fun insertData(){
        if(dbHandler!!.insert_data_table(table, columns)){
            Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Error Inserting Data", Toast.LENGTH_SHORT).show()
        }
        finish()
    }
}
