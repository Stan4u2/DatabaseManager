package com.example.databasemanager.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.databasemanager.*
import com.example.databasemanager.database.Tables.DBHandlerCreateDatabases
import com.example.databasemanager.database.Tables.Databases
import com.example.databasemanager.database.Tables.Tables
import kotlinx.android.synthetic.main.select_option_table_dialog.view.*

class TableAdapter : RecyclerView.Adapter<TableAdapter.ViewHolder>() {


    var database: Databases? = null
    var table: MutableList<Tables> = ArrayList()
    lateinit var context: Context
    private var dbHandler: DBHandlerCreateDatabases? = null

    fun TableAdapter(table: MutableList<Tables>, databases: Databases?, context: Context) {
        this.table = table
        this.database = databases
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        dbHandler = DBHandlerCreateDatabases(
            context,
            database?.name_database.toString(),
            null,
            database?.version_database!!
        )
        return ViewHolder(layoutInflater.inflate(R.layout.item_list_tables, parent, false))
    }

    override fun getItemCount(): Int {
        return table.size
    }

    override fun onBindViewHolder(holder: TableAdapter.ViewHolder, position: Int) {
        holder.nameTable_textView.text = table[position].name_table

        holder.itemView.setOnClickListener {
            selectOption(position)
        }

        holder.editTable_Button.setOnClickListener {

        }

        holder.deleteTable_Button.setOnClickListener {
            var tableDel = Tables(table[position].name_table)
            dbHandler?.dropTable(tableDel)
            Toast.makeText(context, "Table Deleted", Toast.LENGTH_SHORT).show()
            dbHandler?.close()

            val intent = Intent(context, ListTables::class.java)
            var bundle = Bundle()
            bundle.putSerializable("Database", database)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTable_textView = view.findViewById(R.id.nameTable_textView) as TextView
        val editTable_Button = view.findViewById(R.id.editTable_Button) as Button
        val deleteTable_Button = view.findViewById(R.id.deleteTable_Button) as Button
    }

    private fun selectOption(position: Int) {
        val DialogView =
            LayoutInflater.from(context).inflate(R.layout.select_option_table_dialog, null)

        val alertDialog = AlertDialog.Builder(context)
        with(alertDialog) {
            setTitle("Select an option")
            setView(DialogView)
            DialogView.showDescTableButton.setOnClickListener {
                val intent = Intent(context, DescriptionTable::class.java)
                var bundle = Bundle()
                bundle.putSerializable("Database", database)
                bundle.putSerializable("Table", table[position].name_table)
                intent.putExtras(bundle)
                context.startActivity(intent)
            }
            DialogView.showDataTableButton.setOnClickListener {
                val intent = Intent(context, DataTable::class.java)
                var bundle = Bundle()
                bundle.putSerializable("Database", database)
                bundle.putSerializable("Table", table[position].name_table)
                intent.putExtras(bundle)
                context.startActivity(intent)
            }
        }
        val alert = alertDialog.create()
        alert.show()
    }
}