package com.informatika.bondoman.model.repository.transaction

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.local.dao.TransactionDao
import com.informatika.bondoman.model.local.entity.transaction.Category
import com.informatika.bondoman.model.local.entity.transaction.CategoryPercentage
import com.informatika.bondoman.model.local.entity.transaction.Location
import com.informatika.bondoman.model.local.entity.transaction.Transaction
import com.informatika.bondoman.model.remote.response.Item
import com.informatika.bondoman.model.remote.response.Items
import com.informatika.bondoman.model.remote.service.TransactionService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.awaitResponse
import timber.log.Timber
import java.io.File

class TransactionRepositoryImpl(private var transactionDao: TransactionDao, private var transactionService: TransactionService) :
    TransactionRepository {

    override var _listTransactionLiveData = MutableLiveData<Resource<List<Transaction>>>()
        private set
    override var listTransactionLiveData: LiveData<Resource<List<Transaction>>> =
        _listTransactionLiveData
        private set
        get() = _listTransactionLiveData

    override var _transactionLiveData = MutableLiveData<Resource<Transaction>>()
        private set
    override var transactionLiveData: LiveData<Resource<Transaction>> = _transactionLiveData
        private set
        get() = _transactionLiveData

    override var _categoryPercentageLiveData = MutableLiveData<Resource<List<CategoryPercentage>>>()
        private set
    override var categoryPercentageLiveData : LiveData<Resource<List<CategoryPercentage>>> = _categoryPercentageLiveData
        private set
        get() = _categoryPercentageLiveData

    override suspend fun getTransaction(id: Int) {
        _transactionLiveData.postValue(Resource.Loading())
        try {
            val transaction = transactionDao.get(id)
            _transactionLiveData.postValue(Resource.Success(transaction))
        } catch (e: Exception) {
            _transactionLiveData.postValue(Resource.Error(e))
        }
    }

    override suspend fun getAllTransaction() {
        _listTransactionLiveData.postValue(Resource.Loading())
        try {
            val transactionList = transactionDao.getAll()
            _listTransactionLiveData.postValue(Resource.Success(transactionList))
        } catch (e: Exception) {
            _listTransactionLiveData.postValue(Resource.Error(e))
        }
    }

    override suspend fun insertTransaction(
        title: String,
        category: Category,
        amount: Int,
        location: Location
    ) {
        transactionDao.insert(
            title = title,
            category = category,
            amount = amount,
            location_lat = location.lat,
            location_lon = location.lon,
            location_adminArea = location.adminArea
        )
    }

    override suspend fun getCategoryPercentage() {
        _categoryPercentageLiveData.postValue(Resource.Loading())
        try {
            val categoryPercentage = transactionDao.getCategoryPercentages()
            _categoryPercentageLiveData.postValue(Resource.Success(categoryPercentage))
        } catch (e: Exception) {
            _categoryPercentageLiveData.postValue(Resource.Error(e))
        }
    }

    override suspend fun insertTransaction(title: String, category: Category, amount: Int) {
        transactionDao.insert(title, category, amount)
    }

    override suspend fun updateTransaction(
        _id: Int,
        title: String,
        amount: Int,
        location: Location
    ) {
        transactionDao.update(_id, title, amount, location.lat, location.lon, location.adminArea)
    }

    override suspend fun updateTransaction(_id: Int, title: String, amount: Int) {
        transactionDao.update(_id, title, amount)
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    override suspend fun uploadBill(token: String, image: File): Resource<List<Item>> {
        try {
            val requestFile: RequestBody = image.asRequestBody("image/*".toMediaTypeOrNull())
            val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", image.name, requestFile)
            val call = transactionService.uploadBill(token, body)
            val response = call.awaitResponse()

            return if (response.isSuccessful) {
                val items: Items = response.body()!!.items
                Resource.Success(items.items)
            } else {
                throw Exception("Error upload bill")
            }
        } catch (e: Exception) {
            Timber.e(e)
            return Resource.Error(e)
        }
    }
}