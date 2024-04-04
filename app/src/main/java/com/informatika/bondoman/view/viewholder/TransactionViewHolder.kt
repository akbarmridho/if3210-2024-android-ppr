package com.informatika.bondoman.view.viewholder

import android.view.View
import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.informatika.bondoman.databinding.ItemTransactionBinding
import com.informatika.bondoman.model.local.entity.transaction.Transaction

class TransactionViewHolder(private var mTransactionBinding: ItemTransactionBinding) :
    RecyclerView.ViewHolder(mTransactionBinding.root) {
    fun setTransaction(transaction: Transaction) {
        mTransactionBinding.transaction = transaction

        mTransactionBinding.tvListItemTransactionLocation.setOnClickListener{
            if (transaction.location != null) {
                val gmmIntentUri = "http://maps.google.com/maps?q=loc:${transaction.location.lat},${transaction.location.lon}"
                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(gmmIntentUri))
                mapIntent.setPackage("com.google.android.apps.maps")
                mTransactionBinding.root.context.startActivity(mapIntent)
            }
        }
    }

    fun setClickListener(clickListener: View.OnClickListener) {
        mTransactionBinding.root.setOnClickListener(clickListener)
    }
}