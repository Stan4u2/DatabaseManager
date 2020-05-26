package com.example.databasemanager.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.databasemanager.R
import com.example.databasemanager.database.Tables.Column
import com.example.databasemanager.database.Tables.DBHandlerCreateDatabases
import com.example.databasemanager.database.Tables.Databases
import com.example.databasemanager.database.Tables.Tables

class ColumnSelectAdapter(private val callBackInterface: CallBackInterface) :
    RecyclerView.Adapter<ColumnSelectAdapter.ViewHolder>() {

    var table: Tables = Tables("")
    var column: MutableList<Column> = ArrayList()
    var columnsSelected = ArrayList<String>()
    lateinit var context: Context

    fun ColumnSelectAdapter(column: MutableList<Column>, columnsSelected: ArrayList<String>, table: Tables,  context: Context) {
        this.column = column
        this.columnsSelected = columnsSelected
        this.table = table
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_list_select, parent, false))
    }

    override fun getItemCount(): Int {
        return column.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameColumnSelectTextView.text = column[position].name_column

        //This if verifies if that column has already been selected if it has then it'll be already checked.
        if (columnsSelected.contains(table.name_table + "." + column[position].name_column)){
            holder.selectColumnCheckbox.isChecked = true
        }

        holder.selectColumnCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                callBackInterface.passDataCallBack(column[position].name_column, table.name_table, true)
            } else {
                callBackInterface.passDataCallBack(column[position].name_column, table.name_table, false)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameColumnSelectTextView =
            view.findViewById(R.id.nameColumnSelectTextView) as TextView
        val selectColumnCheckbox = view.findViewById(R.id.selectColumnCheckbox) as CheckBox
    }

    interface CallBackInterface {
        fun passDataCallBack(nameColumn: String, nameTable: String, keep: Boolean)
    }
}