package com.informatika.bondoman.util

import androidx.recyclerview.widget.DiffUtil
import com.informatika.bondoman.model.local.entity.transaction.Transaction

class TransactionDiffUtil(private val newList: ArrayList<Transaction>? = null, private val oldList: ArrayList<Transaction>? = null) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList?.get(oldItemPosition)?._id == newList?.get(newItemPosition)?._id
    }

    override fun getOldListSize(): Int {
        return oldList?.size ?: 0
    }

    override fun getNewListSize(): Int {
        return newList?.size ?: 0
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList?.get(oldItemPosition) === newList?.get(newItemPosition)
    }
}