package com.cavstecnologia.fluxodecaixa_pos2025.adapter

import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import com.cavstecnologia.fluxodecaixa_pos2025.R
import com.cavstecnologia.fluxodecaixa_pos2025.entity.CashFlowEntry
import com.cavstecnologia.fluxodecaixa_pos2025.utils.Utils
import androidx.core.graphics.toColorInt

class CashFlowAdapter(var conext : Context, var cursor: Cursor) : BaseAdapter() {
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
        val inflater = conext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;
        val listElement = inflater.inflate(R.layout.list_element, null);

        val tvDate = listElement.findViewById<TextView>(R.id.tv_date);
        val tvType = listElement.findViewById<TextView>(R.id.tv_type);
        val tvDetails = listElement.findViewById<TextView>(R.id.tv_details);
        val tvValue = listElement.findViewById<TextView>(R.id.tv_value);
        val tvLocalMoney = listElement.findViewById<TextView>(R.id.tv_local_money);

        cursor.moveToPosition(position);

        val util : Utils = Utils();

        val date = cursor.getString(4);
        val type = cursor.getString(1);
        val detail = cursor.getString(2);
        val value = util.moneyDecimalFormatter(cursor.getDouble(3));

        tvDate.text = date;
        tvType.text = type;
        tvDetails.text = detail;
        tvValue.text = value;

        if (type.substring(0, 1) == "C"){
            tvValue.setTextColor(Color.GREEN);
            tvLocalMoney.setTextColor(Color.GREEN);
        }
        if (type.substring(0, 1) == "D"){
            tvValue.setTextColor(Color.RED);
            tvLocalMoney.setTextColor(Color.RED);
        }


        return listElement;
    }

}