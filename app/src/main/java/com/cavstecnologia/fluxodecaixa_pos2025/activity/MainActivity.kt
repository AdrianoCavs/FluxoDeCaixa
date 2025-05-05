package com.cavstecnologia.fluxodecaixa_pos2025.activity

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.nfc.Tag
import android.os.Bundle
import android.text.method.DigitsKeyListener
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.cavstecnologia.fluxodecaixa_pos2025.R
import com.cavstecnologia.fluxodecaixa_pos2025.database.DatabaseHandler
import com.cavstecnologia.fluxodecaixa_pos2025.databinding.ActivityMainBinding
import com.cavstecnologia.fluxodecaixa_pos2025.entity.CashFlowEntry
import com.cavstecnologia.fluxodecaixa_pos2025.utils.Utils
import java.text.DecimalFormatSymbols
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var db : DatabaseHandler;
    private lateinit var binding: ActivityMainBinding;
    private lateinit var detailsItemAdapter: ArrayAdapter<CharSequence>;
    private val calendar = Calendar.getInstance();
    private val locale = Locale.getDefault();
    private var firstRadioButtonClick = true;
    private val separator : String = DecimalFormatSymbols.getInstance().decimalSeparator.toString();
    private val util = Utils();
    private lateinit var sharedPreferences: SharedPreferences;
    private var dark_mode = false;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge();
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.main);
        //setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initData();

        db = DatabaseHandler(this);

        sharedPreferences = getSharedPreferences("properties", Context.MODE_PRIVATE);
        dark_mode = sharedPreferences.getBoolean("dark_mode", false);
        when (dark_mode){
            true -> {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)};
            false -> {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)};
        }

        binding.swChangeTheme.setOnClickListener {
            val editor = sharedPreferences.edit();
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("dark_mode", false);
                editor.apply();
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean("dark_mode", true);
                editor.apply();
            }
            recreate();
        }

        binding.etValue.keyListener = DigitsKeyListener.getInstance("0123456789${separator}");

        binding.rbOptionCredit.setOnClickListener {
            //Toast.makeText(this, "ID do radio button: " + binding.rbOptionCredit.id, Toast.LENGTH_LONG).show();
            binding.actvDetailExposedDropdown.isEnabled = true;
            binding.actvDetailExposedDropdown.setText("");
            detailsItemAdapter = ArrayAdapter.createFromResource(this, R.array.details_elements_credit, android.R.layout.simple_spinner_dropdown_item);
            binding.actvDetailExposedDropdown.setAdapter(detailsItemAdapter);

            if (firstRadioButtonClick){
                firstRadioButtonClick = false
            }
            else{
                //binding.etValue.requestFocus();
                binding.actvDetailExposedDropdown.requestFocus();
            }
        }
        binding.rbOptionDebit.setOnClickListener {
            //Toast.makeText(this, "ID do radio button: " + binding.rbOptionDebit.id, Toast.LENGTH_LONG).show();
            binding.actvDetailExposedDropdown.isEnabled = true;
            binding.actvDetailExposedDropdown.setText("");
            detailsItemAdapter = ArrayAdapter.createFromResource(this, R.array.details_elements_debit, android.R.layout.simple_spinner_dropdown_item);
            binding.actvDetailExposedDropdown.setAdapter(detailsItemAdapter);

            if (firstRadioButtonClick){
                firstRadioButtonClick = false
                //binding.etValue.requestFocus();
            }
            else{
                binding.actvDetailExposedDropdown.requestFocus();
            }
        }

        binding.actvDetailExposedDropdown.setOnItemClickListener { parent, view, position, id ->
            binding.etValue.isEnabled = true;
        }

        binding.etValue.doAfterTextChanged {
            binding.etEntryDate.isEnabled = true;
        }

        binding.etEntryDate.setOnClickListener {
            showDatePickerDialog();
            binding.btSubmit.isEnabled = true;
        }

        binding.btSubmit.setOnClickListener{
            if (checkEmptyFields()){
                val radioButtonId = binding.rgInputType.checkedRadioButtonId;
                val selectedRadioButton : RadioButton = findViewById<RadioButton>(radioButtonId);
                var value : String = binding.etValue.text.toString();
                value = value.replace(",", ".");
                val cashFlowEntry : CashFlowEntry = CashFlowEntry(
                    0,
                    selectedRadioButton.text.toString(),
                    binding.actvDetailExposedDropdown.text.toString(),
                    value.toDouble(),
                    binding.etEntryDate.text.toString());
                db.insert(cashFlowEntry);
                Toast.makeText(this, R.string.insert_sucessful, Toast.LENGTH_LONG).show();
                emptyAllFields();
            }
            else{
                Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_LONG).show();
            }
        }

        binding.btCheckEntries.setOnClickListener{
            val intent = Intent(this, ListEntriesActivity::class.java);
            startActivity(intent);
        }

        binding.btBalance.setOnClickListener{
            emptyAllFields();
            val balance : Double = db.getCashFlowBalance();

            val balanceString : String = util.moneyDecimalFormatter(balance);

            val builder : AlertDialog.Builder = AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.balance_msg, balanceString)).setTitle(R.string.balance).setNeutralButton("OK"){_, _ ->};
            val dialog : AlertDialog = builder.create();
            dialog.show();
        }

    }//end of onCreate

    private fun initData() {
        if (intent.getIntExtra("idCashFlowEntry", 0) != 0){
            TODO("Veio da tela Lista pelo botão editar ->Implementar o preenchimento dos campos por chamada ao banco e colocar o botão salvar como update")
        }
    }

    private fun emptyAllFields() {
        binding.etEntryDate.setText("");
        binding.etEntryDate.isEnabled = false
        binding.etValue.setText("");
        binding.etValue.isEnabled = false;
        binding.actvDetailExposedDropdown.setText("");
        binding.actvDetailExposedDropdown.isEnabled = false;
        binding.btSubmit.isEnabled = false;
        binding.rgInputType.clearCheck();
        binding.tvInputType.requestFocus();
    }

    private fun checkEmptyFields(): Boolean {
        if (binding.rgInputType.checkedRadioButtonId == -1 ||
            binding.actvDetailExposedDropdown.text.isEmpty() ||
            binding.etValue.text!!.isEmpty() ||
            binding.etEntryDate.text!!.isEmpty()){
            return false;
        }
        else return true;
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(this, {_, selectedYear, selectedMonth, selectedDay ->
            val date : String = String.format("%02d",selectedDay)+"/"+String.format("%02d",selectedMonth+1)+"/"+String.format("%02d", selectedYear);
            binding.etEntryDate.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show();
    }
}