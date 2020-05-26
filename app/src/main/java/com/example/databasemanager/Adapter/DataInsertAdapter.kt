package com.example.databasemanager.Adapter

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.databasemanager.R
import com.example.databasemanager.database.Tables.Column

class DataInsertAdapter(private val callBackInterface: CallBackInterface) :
    RecyclerView.Adapter<DataInsertAdapter.ViewHolder>() {

    var column: MutableList<Column> = ArrayList()
    lateinit var context: Context

    fun DataInsertAdapter(column: MutableList<Column>, context: Context){
        this.column = column
        this.context = context
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataInsertAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_list_columns_insert_data,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return column.size
    }

    override fun onBindViewHolder(holder: DataInsertAdapter.ViewHolder, position: Int) {
        holder.nameColumn_textView.text = column[position].name_column
        holder.dataType_textView.text = column[position].data_type
        holder.default_textView.text = column[position].default

        when (column[position].data_type){
            "TEXT"-> holder.inputColumn.inputType = InputType.TYPE_CLASS_TEXT
            "INTEGER"-> holder.inputColumn.inputType = InputType.TYPE_CLASS_NUMBER
            "NUMERIC"-> holder.inputColumn.inputType = InputType.TYPE_CLASS_NUMBER
        }

        holder.itemView.setOnClickListener {
            //callBackInterface.passDataCallBack(position, column[position].name_column)
        }

        holder.inputColumn.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                callBackInterface.passDataCallBack(position, s.toString())
            }
        })
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameColumn_textView = view.findViewById(R.id.nameColumn_textView) as TextView
        val dataType_textView = view.findViewById(R.id.dataType_textView) as TextView
        val default_textView = view.findViewById(R.id.default_textView) as TextView
        val inputColumn = view.findViewById(R.id.inputColumn) as EditText
    }

    interface CallBackInterface {
        fun passDataCallBack(position: Int, text: String)
    }
}