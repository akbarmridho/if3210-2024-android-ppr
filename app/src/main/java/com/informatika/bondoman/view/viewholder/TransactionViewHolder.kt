package com.informatika.bondoman.view.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.informatika.bondoman.databinding.ItemTransactionBinding
import com.informatika.bondoman.model.local.entity.transaction.Transaction

class TransactionViewHolder(private var mTransactionBinding: ItemTransactionBinding) :
    RecyclerView.ViewHolder(mTransactionBinding.root) {
    fun setTransaction(transaction: Transaction) {
        mTransactionBinding.transaction = transaction
    }

    fun setClickListener(clickListener: View.OnClickListener) {
        mTransactionBinding.root.setOnClickListener(clickListener)
    }
}