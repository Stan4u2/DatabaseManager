package com.example.databasemanager.Adapter

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
import com.example.databasemanager.InsertDatabase
import com.example.databasemanager.ListTables
import com.example.databasemanager.MainMenu
import com.example.databasemanager.R
import com.example.databasemanager.database.MyDBHandler
import com.example.databasemanager.database.Tables.Databases

class DatabaseAdapter  : RecyclerView.Adapter<DatabaseAdapter.ViewHolder>() {

    var databases : MutableList<Databases> = ArrayList()
    lateinit var context: Context
    var dbHandler: MyDBHandler? = null

    fun DatabaseAdapter(databases : MutableList<Databases>, context: Context){
        this.databases = databases
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatabaseAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        dbHandler = MyDBHandler(context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_list_databases, parent, false))
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return databases.size
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: DatabaseAdapter.ViewHolder, position: Int) {
        holder.name_database.text = (databases[position].name_database)

        holder.itemView.setOnClickListener {

        }

        holder.button_edit_database.setOnClickListener {
            val intent = Intent(context, ListTables::class.java)
            var database = Databases(null, databases[position].name_database, databases[position].version_database)
            var bundle = Bundle()
            bundle.putSerializable("Database", database)
            intent.putExtras(bundle)
            startActivity(context, intent, null)
        }

        holder.button_delete_database.setOnClickListener {
            var process = dbHandler?.delete_database(databases[position])
            if (process!!){
                context.deleteDatabase(databases[position].name_database)
                Toast.makeText(context, "Database Deleted", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, MainMenu::class.java)
                startActivity(context, intent, null)
            } else {
                Toast.makeText(context, "Database not deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view){
        val name_database = view.findViewById(R.id.nameDatabase_textView) as TextView
        val button_edit_database = view.findViewById(R.id.edit_Button) as Button
        val button_delete_database = view.findViewById(R.id.delete_Button) as Button
    }




}