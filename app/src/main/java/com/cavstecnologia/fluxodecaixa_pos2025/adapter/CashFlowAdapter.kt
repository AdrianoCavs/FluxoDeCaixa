package com.cavstecnologia.fluxodecaixa_pos2025.adapter

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.cavstecnologia.fluxodecaixa_pos2025.R
import com.cavstecnologia.fluxodecaixa_pos2025.entity.CashFlowEntry
import com.cavstecnologia.fluxodecaixa_pos2025.utils.Utils
import com.cavstecnologia.fluxodecaixa_pos2025.activity.MainActivity
import com.cavstecnologia.fluxodecaixa_pos2025.database.DatabaseHandler

class CashFlowAdapter(var context : Context, var cursor: Cursor) : BaseAdapter() {
    override fun getCount(): Int {
        return cursor.count;
    }

    override fun getItem(position: Int): Any {
        cursor.moveToPosition(position);
        val cashFlowEntry = CashFlowEntry(cursor.getInt(0), cursor.getString(1).toString(), cursor.getString(2).toString(), cursor.getDouble(3), cursor.getString(4));
        return cashFlowEntry;
    }

    override fun getItemId(position: Int): Long {
        cursor.moveToPosition(position);
        return cursor.getInt(0).toLong();
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;
        val listElement = inflater.inflate(R.layout.list_element, null);

        val tvDate = listElement.findViewById<TextView>(R.id.tv_date);
        val tvType = listElement.findViewById<TextView>(R.id.tv_type);
        val tvDetails = listElement.findViewById<TextView>(R.id.tv_details);
        val tvValue = listElement.findViewById<TextView>(R.id.tv_value);
        val tvLocalMoney = listElement.findViewById<TextView>(R.id.tv_local_money);

        val tvIdCashFlowEntry = listElement.findViewById<TextView>(R.id.tv_id_cash_flow_entry);
        val btEditListElement = listElement.findViewById<ImageButton>(R.id.bt_edit_list_element);
        val btDeleteListElement = listElement.findViewById<ImageButton>(R.id.bt_delete_list_element);

        cursor.moveToPosition(position);

        val util : Utils = Utils();

        val date = cursor.getString(4);
        val type = cursor.getString(1);
        val detail = cursor.getString(2);
        val value : String = util.moneyDecimalFormatter(cursor.getDouble(3));
        val id = cursor.getInt(0);

        tvDate.text = date;
        tvType.text = type;
        tvDetails.text = detail;
        tvValue.text = value;
        tvIdCashFlowEntry.text = id.toString();

        if (type.substring(0, 1) == "C"){
            tvValue.setTextColor(Color.GREEN);
            tvLocalMoney.setTextColor(Color.GREEN);
        }
        if (type.substring(0, 1) == "D"){
            tvValue.setTextColor(Color.RED);
            tvLocalMoney.setTextColor(Color.RED);
        }

        btEditListElement.setOnClickListener{
            val idCashFlowEntry : Int = tvIdCashFlowEntry.text.toString().toInt();
            val intent = Intent(context, MainActivity::class.java);
            intent.putExtra("idCashFlowEntry", idCashFlowEntry);
            context.startActivity(intent);
        }
        btDeleteListElement.setOnClickListener {
            val builder : AlertDialog.Builder = AlertDialog.Builder(context);
            builder.setMessage(R.string.are_you_sure_delete)
                .setTitle(R.string.confirmation)
                .setPositiveButton(R.string.yes){dialog, id ->
                    val db :DatabaseHandler = DatabaseHandler(context);
                    db.delete(tvIdCashFlowEntry.text.toString().toInt());

                    //os passos abaixos são para atualizar o layout em tempo real excluindo o item que foi deletado do banco
                    this.cursor = db.list();
                    notifyDataSetChanged();
                }
                .setNegativeButton(R.string.no){dialog,id->};
            val dialog : AlertDialog = builder.create();
            dialog.show();
        }
        return listElement;
    }

}