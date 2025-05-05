package com.cavstecnologia.fluxodecaixa_pos2025.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class Utils {
    fun moneyDecimalFormatter(value : Double) : String{
        val separator : String = DecimalFormatSymbols.getInstance().decimalSeparator.toString();
        var formattedValue : String;

        val df : DecimalFormat = DecimalFormat("#.00");

        if (value < 1.0){
            formattedValue = String.format("%.2f", value);
            formattedValue = formattedValue.replace(".", separator);
            return formattedValue;
        }
        else{
            formattedValue = df.format(value);
            formattedValue = formattedValue.replace(".", separator);
            return formattedValue;
        }

    }
}