package com.example.databasemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.databasemanager.Adapter.ColumnAdapter
import com.example.databasemanager.database.Tables.Column
import com.example.databasemanager.database.Tables.DBHandlerCreateDatabases
import com.example.databasemanager.database.Tables.Databases
import com.example.databasemanager.database.Tables.Tables

class InsertTable : AppCompatActivity() {

    lateinit var nameTableEditText: EditText
    lateinit var nameColumnEditText: EditText
    lateinit var defaultEditText: EditText

    lateinit var dataTypeSpinner: Spinner

    lateinit var primaryKeyCheckBox: CheckBox
    lateinit var autoIncrementCheckBox: CheckBox
    lateinit var uniqueCheckBox: CheckBox
    lateinit var notnullCheckBox: CheckBox
    lateinit var defaultCheckBox: CheckBox

    lateinit var columnRecyclerView: RecyclerView

    lateinit var createTableButton: Button

    var dataType: String = ""
    var name_table: String = ""
    var name_column: String = ""
    var primary_key: Boolean = false
    var auto_increment: Boolean = false
    var unique: Boolean = false
    var not_null: Boolean = false
    var default: String = ""

    val columnAdapter: ColumnAdapter = ColumnAdapter()

    var dbHandler: DBHandlerCreateDatabases? = null

    var columns: MutableList<Column> = ArrayList()
    var database: Databases? = null
    var table: Tables? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_table)

        //EditText
        nameTableEditText = findViewById(R.id.nameTableEditText)
        nameColumnEditText = findViewById(R.id.nameColumnEditText)
        defaultEditText = findViewById(R.id.defaultEditText)
        //Spinner
        dataTypeSpinner = findViewById(R.id.dataTypeSpinner)
        //CheckBox
        primaryKeyCheckBox = findViewById(R.id.primaryKeyCheckBox)
        autoIncrementCheckBox = findViewById(R.id.autoIncrementCheckBox)
        uniqueCheckBox = findViewById(R.id.uniqueCheckBox)
        notnullCheckBox = findViewById(R.id.notnullCheckBox)
        defaultCheckBox = findViewById(R.id.defaultCheckBox)
        //RecyclerView
        columnRecyclerView = findViewById(R.id.columnRecyclerView)
        //Button
        createTableButton = findViewById(R.id.createTableButton)

        fillSpinner()

        //CheckBox Listeners
        primaryKeyCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            primary_key = isChecked
        }
        autoIncrementCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            auto_increment = isChecked
        }
        uniqueCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            unique = isChecked
        }
        notnullCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            not_null = isChecked
        }
        defaultCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            defaultEditText.isEnabled = isChecked
            if (isChecked) {
                default = defaultEditText.text.toString()
            } else {
                default = ""
                defaultEditText.setText("")
            }
        }

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
        }
    }


    fun checkInputsForColumn(view: View) {
        var errorText: String = ""
        if (nameTableEditText.text.isEmpty()) {
            errorText = "Empty Input in Name Table"
        } else if (nameColumnEditText.text.isEmpty()) {
            errorText = "Empty Input in Name Column"
        } else if (dataType.isEmpty()) {
            errorText = "No Data Type Selected"
        } else if (defaultCheckBox.isChecked) {
            default = defaultEditText.text.toString()
            if (default.isEmpty()) {
                errorText = "Default Input Empty"
            }
        }

        if (errorText.isNotEmpty()) {
            Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show()
            return
        }

        addColumn()
    }

    private fun fillSpinner() {
        val itemsInSpinner = arrayOf<String>("Select Option", "INTEGER", "TEXT", "NUMERIC")

        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemsInSpinner)

        dataTypeSpinner.adapter = adapter

        dataTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                dataType = ""
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                dataType = if (position == 0) {
                    ""
                } else {
                    itemsInSpinner[position]
                }

                autoIncrementCheckBox.isEnabled = false
                autoIncrementCheckBox.isChecked = false

                when (dataType) {
                    "INTEGER" -> {
                        defaultEditText.inputType = InputType.TYPE_CLASS_NUMBER
                        autoIncrementCheckBox.isEnabled = true
                    }
                    "TEXT" -> defaultEditText.inputType = InputType.TYPE_CLASS_TEXT
                    "NUMERIC" -> defaultEditText.inputType = InputType.TYPE_CLASS_NUMBER
                }
            }

        }
    }

    private fun setupRecyclerView() {
        columnRecyclerView.setHasFixedSize(true)
        columnRecyclerView.layoutManager = LinearLayoutManager(this)
        columnAdapter.ColumnAdapter(columns, database, applicationContext)
        columnRecyclerView.adapter = columnAdapter
    }

    private fun addColumn() {
        name_column = nameColumnEditText.text.toString()

        var column =
            Column(name_column, dataType, primary_key, auto_increment, unique, not_null, default)
        columns.add(column)
        clearInputs()

        setupRecyclerView()
    }

    private fun clearInputs() {
        nameColumnEditText.setText("")

        dataTypeSpinner.setSelection(0)
        primaryKeyCheckBox.isChecked = false
        autoIncrementCheckBox.isChecked = false
        uniqueCheckBox.isChecked = false
        notnullCheckBox.isChecked = false
        defaultCheckBox.isChecked = false
    }

    fun insertTable(view: View) {
        if (columns.size > 0) {
            if (nameTableEditText.text.toString().isEmpty()) {
                Toast.makeText(this, "Input for the Name of Table is Empty", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            table = Tables(nameTableEditText.text.toString())
            dbHandler?.createTable(table, columns as ArrayList<Column>)
            finish()
        }
    }
}
