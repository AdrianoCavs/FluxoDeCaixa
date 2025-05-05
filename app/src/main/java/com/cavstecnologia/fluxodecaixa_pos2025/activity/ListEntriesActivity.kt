package com.cavstecnologia.fluxodecaixa_pos2025.activity

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cavstecnologia.fluxodecaixa_pos2025.R
import com.cavstecnologia.fluxodecaixa_pos2025.adapter.CashFlowAdapter
import com.cavstecnologia.fluxodecaixa_pos2025.database.DatabaseHandler
import com.cavstecnologia.fluxodecaixa_pos2025.databinding.ActivityListEntriesBinding
import com.cavstecnologia.fluxodecaixa_pos2025.databinding.ListElementBinding
import com.cavstecnologia.fluxodecaixa_pos2025.entity.CashFlowEntry

class ListEntriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListEntriesBinding;
    private lateinit var bindingList : ListElementBinding;
    private lateinit var db : DatabaseHandler;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListEntriesBinding.inflate(layoutInflater);
        bindingList = ListElementBinding.inflate(layoutInflater);
        setContentView(binding.mainList);
        //setContentView(R.layout.activity_list_entries)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_list)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DatabaseHandler(this);
        val records : Cursor = db.list();

        val adapter = CashFlowAdapter(this, records);
        binding.lvMain.adapter = adapter;

        bindingList.btEditListElement.setOnClickListener{
            val cashFlowEntry = CashFlowEntry (bindingList.tvIdCashFlowEntry.text.toString().toInt(), bindingList.tvType.toString(), bindingList.tvDetails.toString(), bindingList.tvValue.toString().toDouble(), bindingList.tvDate.toString());
            val idCashFlowEntry : Int = bindingList.tvIdCashFlowEntry.text.toString().toInt();
            val intent = Intent(this, MainActivity::class.java);
            intent.putExtra("idCashFlowEntry", idCashFlowEntry);
            //intent.putExtra("cashFlowEntryToEdit", cashFlowEntry);
            startActivity(intent);
            //data class CashFlowEntry (var _id : Int, var type : String, var detail : String, var value : Double, var date : String)
        }
    }
}