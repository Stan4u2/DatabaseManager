package com.example.databasemanager

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.databasemanager.Adapter.ColumnAdapter
import com.example.databasemanager.Adapter.ColumnSelectAdapter
import com.example.databasemanager.Adapter.ColumnWhereAdapter
import com.example.databasemanager.database.Tables.Column
import com.example.databasemanager.database.Tables.DBHandlerCreateDatabases
import com.example.databasemanager.database.Tables.Databases
import com.example.databasemanager.database.Tables.Tables

class SelectData : AppCompatActivity(), ColumnSelectAdapter.CallBackInterface,
    ColumnWhereAdapter.CallBackInterface {

    lateinit var selectRecyclerView: RecyclerView
    lateinit var whereRecyclerView: RecyclerView
    lateinit var SearchSelectbutton: Button
    lateinit var tableLayout: TableLayout
    lateinit var SelectTableSpinner: Spinner
    lateinit var WhereTableSpinner: Spinner

    var dbHandler: DBHandlerCreateDatabases? = null
    var database: Databases? = null

    var selectSyntaxt = ""

    //ArrayLists
    var tables = ArrayList<Tables>()
    var tablesList = ArrayList<String>()
    var selectList = ArrayList<String>()
    var fromList = ArrayList<String>()
    var whereList = ArrayList<String>()
    var valueList = ArrayList<Column>()
    var columnListWhereRecyclerView: MutableList<Column> = ArrayList()
    var columnsSelect: MutableList<Column> = ArrayList()


    var tableSelectPartSelected: Tables = Tables("")
    var tableWherePartSelected: Tables = Tables("")

    //val columnAdapter: ColumnAdapter = ColumnAdapter()
    val columnSelectAdapter: ColumnSelectAdapter = ColumnSelectAdapter(this)
    val columnWhereAdapter: ColumnWhereAdapter = ColumnWhereAdapter(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_data)

        //RecyclerView
        selectRecyclerView = findViewById(R.id.selectRecyclerView)
        whereRecyclerView = findViewById(R.id.whereRecyclerView)
        //Button
        SearchSelectbutton = findViewById(R.id.SearchSelectbutton)
        //TableLayout
        tableLayout = findViewById(R.id.tableLayout)
        //Spinner
        SelectTableSpinner = findViewById(R.id.SelectTableSpinner)
        WhereTableSpinner = findViewById(R.id.WhereTableSpinner)
    }

    override fun onStart() {
        super.onStart()
        var bundle: Bundle = intent.extras!!
        if (!bundle.isEmpty) {
            database = bundle.getSerializable("Database") as Databases
            dbHandler = DBHandlerCreateDatabases(
                this,
                database!!.name_database,
                null,
                database!!.version_database
            )
            getListTables()
        }
    }

    private fun getListTables() {
        var result = dbHandler!!.getTableList()
        tables.clear()
        result.forEach {
            tables.add(Tables(it.name_table))
        }
        fillSpinners()
    }

    private fun fillSpinners() {
        setListTablesSpinner()

        val adapter =
            ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, tablesList)

        SelectTableSpinner.adapter = adapter
        WhereTableSpinner.adapter = adapter

        SelectTableSpinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                tableSelectPartSelected = Tables("")
                setupSelectRecyclerView()
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                tableSelectPartSelected = if (position != 0) {
                    Tables(tables[position - 1].name_table)
                } else {
                    Tables("")
                }
                setupSelectRecyclerView()
            }
        }

        WhereTableSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
                tableWherePartSelected = Tables("")
                setupWhereRecyclerView()
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                tableWherePartSelected = if (position != 0) {
                    Tables(tables[position - 1].name_table)
                } else {
                    Tables("")
                }
                setupWhereRecyclerView()
            }
        }
    }

    private fun setListTablesSpinner() {
        tablesList = ArrayList()
        tablesList.add("Select Table")

        for (x in 0 until tables.size) {
            tablesList.add(tables[x].name_table)
        }
    }

    private fun setupSelectRecyclerView() {
        var columnList: MutableList<Column> = ArrayList()

        if (tableSelectPartSelected.name_table != "") {
            columnList = getListColumns(tableSelectPartSelected)
        }

        selectRecyclerView.setHasFixedSize(true)
        selectRecyclerView.layoutManager = LinearLayoutManager(this)
        columnSelectAdapter.ColumnSelectAdapter(
            columnList,
            selectList,
            tableSelectPartSelected,
            applicationContext
        )
        selectRecyclerView.adapter = columnSelectAdapter
    }

    private fun setupWhereRecyclerView() {
        columnListWhereRecyclerView.clear()

        if (tableWherePartSelected.name_table != "") {
            columnListWhereRecyclerView = getListColumns(tableWherePartSelected)
            createValueList()
        }

        whereRecyclerView.setHasFixedSize(true)
        whereRecyclerView.layoutManager = LinearLayoutManager(this)
        columnWhereAdapter.ColumnWhereAdapter(
            columnListWhereRecyclerView,
            whereList,
            valueList,
            tableWherePartSelected,
            applicationContext
        )
        whereRecyclerView.adapter = columnWhereAdapter
    }

    private fun createValueList() {
        //Int his function a create a list of values which is used in the adapter to set automatically the values when the table is changed
        for (x in 0 until columnListWhereRecyclerView.size) {
            //I check if the value list is bigger than 0, if not then I'll add this column to the list
            //But if it's bigger than 0 then I want to check if this column has already been added.
            if (valueList.size > 0) {
                var itContainsIt = false
                for (y in 0 until valueList.size) {
                    if (valueList[y].table == tableWherePartSelected.name_table) {
                        itContainsIt = true
                        break
                    }
                }
                //If it doesn't contains this column then I'll add all the columns from that table to the list
                if (!itContainsIt) {
                    addAllColumnsList()
                }
            } else {
                addAllColumnsList()
                break
            }
        }
    }

    private fun addAllColumnsList() {
        for (x in 0 until columnListWhereRecyclerView.size) {
            valueList.add(
                Column(
                    columnListWhereRecyclerView[x].name_column, "",
                    primary_key = false,
                    auto_increment = false,
                    unique = false,
                    not_null = false,
                    default = "",
                    value = "",
                    table = tableWherePartSelected.name_table
                )
            )
        }
    }

    private fun getListColumns(table: Tables): MutableList<Column> {
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

    override fun passDataCallBack(columnSent: String, nameTable: String, keep: Boolean) {
        if (keep) {
            //Add the column in a list of which will be used in the select part
            selectList.add(tableSelectPartSelected.name_table + "." + columnSent)

            columnsSelect.add(Column(columnSent, "", false, false, false, false, "", "", nameTable))
            //Add the table in a list which will be used in the where part
            if (!fromList.contains(tableSelectPartSelected.name_table)) {
                fromList.add(tableSelectPartSelected.name_table)
            }

        } else {
            if (selectList.contains(tableSelectPartSelected.name_table + "." + columnSent)) {
                selectList.remove(tableSelectPartSelected.name_table + "." + columnSent)

                checkIfTableIsUsed(tableSelectPartSelected.name_table)

                for (x in 0 until columnsSelect.size){
                    if (columnsSelect[x].name_column == columnSent && columnsSelect[x].table == nameTable){
                        columnsSelect.removeAt(x)
                        break
                    }
                }
            }
        }
    }

    override fun passWhereDataCallBack(columnSent: String, valueToFind: String, keep: Boolean) {
        if (keep) {
            //Add the column in a list of which will be used in the select part
            //If the column has already been inserted then just modify that column
            var alreadyInserted = false
            for (x in 0 until whereList.size) {
                if (whereList[x].substringBefore(" = ") == tableWherePartSelected.name_table + "." + columnSent) {
                    whereList[x] =
                        tableWherePartSelected.name_table + "." + columnSent + " = " + valueToFind
                    alreadyInserted = true
                    break
                }
            }
            if (!alreadyInserted) {
                whereList.add(tableWherePartSelected.name_table + "." + columnSent + " = " + valueToFind)
            }

            //Set the value to find in the value list
            for (x in 0 until valueList.size) {
                if (valueList[x].name_column == columnSent && valueList[x].table == tableWherePartSelected.name_table) {
                    valueList[x].value = valueToFind
                }
            }


            //Add the table in a list which will be used in the where part
            if (!fromList.contains(tableWherePartSelected.name_table)) {
                fromList.add(tableWherePartSelected.name_table)
            }
        } else {
            for (x in 0 until whereList.size) {
                if (whereList[x].substringBefore(" =") == tableWherePartSelected.name_table + "." + columnSent) {
                    whereList.removeAt(x)
                    break
                }
            }
            checkIfTableIsUsed(tableWherePartSelected.name_table)
        }
    }

    private fun checkIfTableIsUsed(tableName: String) {
        //Check if Table is used in the select part
        var containsTable = false
        for (x in 0 until selectList.size) {
            var separated = selectList[x].substringBefore(".")
            if (separated.contains(tableName)) {
                containsTable = true
                break
            }
        }
        //Check if Table is used in the where part
        for (x in 0 until whereList.size) {
            var separated = whereList[x].substringBefore(".")
            if (separated.contains(tableName)) {
                containsTable = true
                break
            }
        }

        //If the table is not used anymore in the select or where list then it'll be removed from the "from list"
        if (!containsTable) {
            fromList.remove(tableSelectPartSelected.name_table)
            fromList.remove(tableWherePartSelected.name_table)
        }
    }

    fun searchData(view: View) {
        if (selectList.isEmpty() && fromList.isEmpty() && whereList.isEmpty()) {
            Toast.makeText(applicationContext, "No Columns Selected", Toast.LENGTH_SHORT).show()
        } else {
            var select = "SELECT"
            for (x in 0 until selectList.size) {
                select = if (x == selectList.size - 1) {
                    select.plus(" " + selectList[x] + " ")
                } else {
                    select.plus(" " + selectList[x] + ",")
                }
            }
            select = select.plus("FROM")
            for (x in 0 until fromList.size) {
                select = if (x == fromList.size - 1) {
                    select.plus(" " + fromList[x] + " ")
                } else {
                    select.plus(" " + fromList[x] + ",")
                }
            }
            if (whereList.isNotEmpty()) {
                select = select.plus("WHERE")
                for (x in 0 until whereList.size) {
                    select = if (x == whereList.size - 1) {
                        select.plus(" " + whereList[x])
                    } else {
                        select.plus(" " + whereList[x] + " AND")
                    }
                }
            }
            selectSyntaxt = select
            println(select)

            addTopRowTable()
            addRowsData()
        }
    }

    private fun addTopRowTable() {
        tableLayout.removeAllViews()
        var tr_head = TableRow(applicationContext)
        tr_head.layoutParams = (ViewGroup.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ))
        tr_head.setBackgroundColor(Color.GRAY)

        for (x in 0 until columnsSelect.size) {
            var column: TextView = TextView(applicationContext)
            column.text = columnsSelect[x].name_column
            column.setTextColor(Color.WHITE)
            column.setPadding(5, 5, 5, 5)
            tr_head.addView(column)
        }

        tableLayout.addView(tr_head)
    }

    private fun addRowsData() {
        var data = getDataTable()

        var pos1 = 0

        for (y in 0 until (data.size/columnsSelect.size)){
            var table_row = TableRow(applicationContext)
            table_row.layoutParams = (ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ))
            table_row.setBackgroundColor(
                Color.BLACK
            )
            for (x in pos1 until columnsSelect.size + pos1) {
                var column: TextView = TextView(applicationContext)
                column.text = data[x].value
                column.setTextColor(Color.WHITE)
                column.setPadding(2, 0, 5, 0)
                table_row.addView(column)
            }
            tableLayout.addView(table_row)
            pos1 += columnsSelect.size
        }
    }

    private fun getDataTable(): MutableList<Column> {
        var result = dbHandler!!.getCustomSelect(selectSyntaxt, columnsSelect)

        var columns: MutableList<Column> = ArrayList()
        result!!.forEach {
            columns.add(Column(it.name_column, "", false, false, false, false, "", it.value))
        }
        return columns
    }

}
