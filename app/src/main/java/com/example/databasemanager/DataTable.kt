package com.example.databasemanager

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import com.example.databasemanager.database.Tables.Column
import com.example.databasemanager.database.Tables.DBHandlerCreateDatabases
import com.example.databasemanager.database.Tables.Databases
import com.example.databasemanager.database.Tables.Tables

class DataTable : AppCompatActivity() {

    lateinit var tableLayout: TableLayout
    lateinit var nameTableTextView: TextView
    lateinit var insertDataTable: Button

    var dbHandler: DBHandlerCreateDatabases? = null
    var database: Databases? = null
    private lateinit var table: Tables

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_table)

        //Table Layout
        tableLayout = findViewById(R.id.tableLayout)
        //TextView
        nameTableTextView = findViewById(R.id.nameTableTextView)
        //Button
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
            addTopRowTable()
            addRowsData()
        }
    }

    private fun addTopRowTable() {
        var tr_head = TableRow(applicationContext)
        tr_head.layoutParams = (ViewGroup.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ))
        tr_head.setBackgroundColor(Color.GRAY)

        var columnName = getListColumns()
        for (x in 0 until columnName.size) {
            var column: TextView = TextView(applicationContext)
            column.text = columnName[x].name_column
            column.setTextColor(Color.WHITE)
            column.setPadding(5, 5, 5, 5)
            tr_head.addView(column)
        }

        tableLayout.addView(tr_head)
    }

    private fun addRowsData() {
        var data = getDataTable()
        var columnName = getListColumns()

        var pos1 = 0

        for (y in 0 until (data.size/columnName.size)){
            var table_row = TableRow(applicationContext)
            table_row.layoutParams = (ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ))
            table_row.setBackgroundColor(
                Color.BLACK
            )
            for (x in pos1 until columnName.size + pos1) {
                var column: TextView = TextView(applicationContext)
                column.text = data[x].value
                column.setTextColor(Color.WHITE)
                column.setPadding(2, 0, 5, 0)
                table_row.addView(column)
            }
            tableLayout.addView(table_row)
            pos1 += columnName.size
        }
    }

    private fun getListColumns(): MutableList<Column> {
        var result = dbHandler!!.getColumnList(table)

        var columns: MutableList<Column> = ArrayList()
        result.forEach {
            columns.add(
                Column(
                    it.name_column,
                    it.data_type,
                    it.primary_key,
                    it.auto_increment,
                    it.unique,
                    it.not_null,
                    it.default
                )
            )
        }
        return columns
    }

    private fun getDataTable(): MutableList<Column> {
        var result = dbHandler!!.getTableData(table, getListColumns())

        var columns: MutableList<Column> = ArrayList()
        result!!.forEach {
            columns.add(Column(it.name_column, "", false, false, false, false, "", it.value))
        }
        return columns
    }
}
