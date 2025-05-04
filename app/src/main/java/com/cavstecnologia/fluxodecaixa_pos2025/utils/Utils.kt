package com.cavstecnologia.fluxodecaixa_pos2025.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class Utils {
    fun moneyDecimalFormatter(value : Double) : String{
        val separator : String = DecimalFormatSymbols.getInstance().decimalSeparator.toString();

        val df : DecimalFormat = DecimalFormat("#.00");

        var formattedValue : String = df.format(value);
        formattedValue = formattedValue.replace(".", separator);

        return formattedValue;
    }
}