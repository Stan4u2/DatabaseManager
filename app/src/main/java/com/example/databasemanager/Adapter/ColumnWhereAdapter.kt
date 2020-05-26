package com.example.databasemanager.Adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.databasemanager.R
import com.example.databasemanager.database.Tables.Column
import com.example.databasemanager.database.Tables.Tables

class ColumnWhereAdapter(private val callBackInterface: CallBackInterface) :
    RecyclerView.Adapter<ColumnWhereAdapter.ViewHolder>() {

    var table: Tables = Tables("")
    var column: MutableList<Column> = ArrayList()
    var columnsSelected = ArrayList<String>()
    var valuesInserted = ArrayList<Column>()
    lateinit var context: Context

    fun ColumnWhereAdapter(
        column: MutableList<Column>,
        columnsSelected: ArrayList<String>,
        valuesInserted: ArrayList<Column>,
        table: Tables,
        context: Context
    ) {
        this.column = column
        this.columnsSelected = columnsSelected
        this.valuesInserted = valuesInserted
        this.table = table
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_list_where, parent, false))
    }

    override fun getItemCount(): Int {
        return column.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameColumnWhere_textView.text = column[position].name_column

        //This if verifies if that column has already been selected if it has then it'll be already checked.
        var valueSet = false
        for (x in 0 until columnsSelected.size) {
            if (columnsSelected[x].substringBefore(" = ") == table.name_table + "." + column[position].name_column) {
                holder.whereSelectcheckBox.isChecked = true
                holder.valueSearchEditText.isEnabled = true

                for (x in 0 until valuesInserted.size) {
                    if (column[position].name_column == valuesInserted[x].name_column && valuesInserted[x].table == table.name_table) {
                        holder.valueSearchEditText.setText(valuesInserted[x].value)
                        valueSet = true
                        break
                    }
                }
            } else {
                holder.whereSelectcheckBox.isChecked = false
                holder.valueSearchEditText.isEnabled = false
                holder.valueSearchEditText.setText("")
            }
            if(valueSet){
                break
            }
        }

        holder.whereSelectcheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                holder.valueSearchEditText.isEnabled = true
                callBackInterface.passWhereDataCallBack(
                    column[position].name_column,
                    holder.valueSearchEditText.text.toString(),
                    true
                )
            } else {
                holder.valueSearchEditText.setText("")
                holder.valueSearchEditText.isEnabled = false
                callBackInterface.passWhereDataCallBack(
                    column[position].name_column,
                    holder.valueSearchEditText.text.toString(),
                    false
                )
            }
        }

        //When the text has changed I send the data to the activity to be stored in a array list.
        holder.valueSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                column[position].value = s.toString()
                callBackInterface.passWhereDataCallBack(
                    column[position].name_column,
                    column[position].value,
                    true
                )
            }

        })
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val whereSelectcheckBox = view.findViewById(R.id.whereSelectcheckBox) as CheckBox
        val nameColumnWhere_textView = view.findViewById(R.id.nameColumnWhere_textView) as TextView
        val valueSearchEditText = view.findViewById(R.id.valueSearchEditText) as EditText
    }

    interface CallBackInterface {
        fun passWhereDataCallBack(columnSent: String, valueToFind: String, keep: Boolean)
    }
}