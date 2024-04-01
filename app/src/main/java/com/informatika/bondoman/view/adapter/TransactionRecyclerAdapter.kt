package com.informatika.bondoman.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.informatika.bondoman.databinding.ItemLoadingBinding
import com.informatika.bondoman.databinding.ItemTransactionBinding
import com.informatika.bondoman.model.local.entity.transaction.Category
import com.informatika.bondoman.model.local.entity.transaction.Transaction
import com.informatika.bondoman.util.TransactionDiffUtil
import com.informatika.bondoman.view.viewholder.LoaderViewHolder
import com.informatika.bondoman.view.viewholder.TransactionViewHolder

class TransactionRecyclerAdapter(context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listTransaction: ArrayList<Transaction> = ArrayList()
    private val ITEM_TYPE_NORMAL = 1
    private val ITEM_TYPE_LOADER = 2
    private var listener: ItemTouchListener

    init {
        listener = context as ItemTouchListener
    }

    override fun getItemViewType(position: Int): Int {
        return if (listTransaction[position]._id == 0) {
            ITEM_TYPE_LOADER
        } else {
            ITEM_TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_NORMAL) {
            val mTransactionBinding = ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            TransactionViewHolder(mTransactionBinding)
        } else {
            val mLoadingBinding: ItemLoadingBinding = ItemLoadingBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            LoaderViewHolder(mLoadingBinding)
        }
    }

    override fun getItemCount(): Int {
        return listTransaction.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val clickListener: View.OnClickListener = View.OnClickListener {
            listener.onItemClick(listTransaction[position])
        }

        when (holder.itemViewType) {
            ITEM_TYPE_NORMAL -> {
                holder as TransactionViewHolder
                holder.setTransaction(listTransaction[position])
                holder.setClickListener(clickListener)
            }

            ITEM_TYPE_LOADER -> {
                // do nothing
            }
        }
    }

    fun setTransactionList(newList: ArrayList<Transaction>) {
        val diffResult = DiffUtil.calculateDiff(
            TransactionDiffUtil(newList, listTransaction)
        )
        diffResult.dispatchUpdatesTo(this)
        listTransaction.clear()
        listTransaction.addAll(newList)
//        notifyDataSetChanged()
    }

    private fun getLoaderItem(): Transaction {
        return Transaction(
            _id = 0,
            createdAt = "",
            title = "",
            category = Category.LOADER,
            amount = 0.0,
            location = null,
        )
    }

    fun addLoader() {
        if (!isLoading()) {
            if (listTransaction.isEmpty()) {
                val newListTransaction = ArrayList<Transaction>(listTransaction)
                newListTransaction.add(getLoaderItem())
                setTransactionList(newListTransaction)
//                notifyDataSetChanged()
            }
        }
    }

    fun removeLoader() {
        if (isLoading()) {
            if (!listTransaction.isEmpty()) {
                val newListTransaction = ArrayList<Transaction>(listTransaction)
                newListTransaction.remove(getLoaderItem())
                setTransactionList(newListTransaction)
            }
        }
    }

    fun isLoading(): Boolean {
        return listTransaction.isEmpty() || listTransaction[listTransaction.size - 1]._id == 0
    }

    interface ItemTouchListener {
        fun onItemClick(transaction: Transaction)
    }
}