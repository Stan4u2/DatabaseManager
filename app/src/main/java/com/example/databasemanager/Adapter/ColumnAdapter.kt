package com.example.databasemanager.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.databasemanager.R
import com.example.databasemanager.database.Tables.Column
import com.example.databasemanager.database.Tables.DBHandlerCreateDatabases
import com.example.databasemanager.database.Tables.Databases

class ColumnAdapter : RecyclerView.Adapter<ColumnAdapter.ViewHolder>(){

    var database: Databases? = null
    var column: MutableList<Column> = ArrayList()
    lateinit var context: Context
    private var dbHandler: DBHandlerCreateDatabases? = null

    fun ColumnAdapter(column: MutableList<Column>, databases: Databases?, context: Context){
        this.column = column
        this.database = databases
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        dbHandler = DBHandlerCreateDatabases(context, database?.name_database.toString(), null, database?.version_database!!)
        return ViewHolder(layoutInflater.inflate(R.layout.item_list_columns, parent, false))
    }

    override fun getItemCount(): Int {
        return column.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameColumn_textView.text = column[position].name_column
        holder.dataType_textView.text = column[position].data_type
        when (column[position].primary_key){
            true -> holder.primaryKey_textView.text = "True"
            false -> holder.primaryKey_textView.text = "False"
        }
        when (column[position].auto_increment){
            true -> holder.autoIncrement_textView.text = "True"
            false -> holder.autoIncrement_textView.text = "False"
        }
        when (column[position].unique){
            true -> holder.unique_textView.text = "True"
            false -> holder.unique_textView.text = "False"
        }
        when (column[position].not_null){
            true -> holder.notnull_textView.text = "True"
            false -> holder.notnull_textView.text = "False"
        }
        if (column[position].default.isEmpty()){
            holder.default_textView.visibility = View.GONE
        } else {
            holder.default_textView.visibility = View.VISIBLE
            holder.default_textView.text = column[position].default
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view){
        val nameColumn_textView = view.findViewById(R.id.nameColumn_textView) as TextView
        val dataType_textView = view.findViewById(R.id.dataType_textView) as TextView
        val primaryKey_textView = view.findViewById(R.id.primaryKey_textView) as TextView
        val autoIncrement_textView = view.findViewById(R.id.autoIncrement_textView) as TextView
        val unique_textView = view.findViewById(R.id.unique_textView) as TextView
        val notnull_textView = view.findViewById(R.id.notnull_textView) as TextView
        val default_textView = view.findViewById(R.id.default_textView) as TextView
        val editColumn_Button = view.findViewById(R.id.editColumn_Button) as Button
        val deleteColumn_Button = view.findViewById(R.id.deleteColumn_Button) as Button
    }





}