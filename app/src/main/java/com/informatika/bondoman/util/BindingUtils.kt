package com.informatika.bondoman.util

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import com.informatika.bondoman.model.local.entity.transaction.Category

object BindingUtils {
    @JvmStatic
    fun createTitle(title: String): String {
        // if the title is too long, stop at 20 characters and add "..."
        return if (title.length > 20) {
            title.substring(0, 20) + "..."
        } else {
            title
        }
    }

    @JvmStatic
    fun getCategory(category: Category): String {
        return when (category) {
            Category.INCOME -> "Income"
            Category.EXPENSE -> "Expense"
            Category.LOADER -> ""
        }
    }

    @JvmStatic
    fun getAmountString(amount: Int): String {
        return "$amount"
    }

    @JvmStatic
    fun createAmount(amount: Int): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        val formattedNumber: String = formatter.format(amount)
        return "$formattedNumber"
    }

    @JvmStatic
    fun isLocationNotNull(locAdminArea: String?): Boolean {
        return locAdminArea != null
    }

    @JvmStatic
    fun getDate(s: String): String? {
        return s.split("T")[0]
    }
}