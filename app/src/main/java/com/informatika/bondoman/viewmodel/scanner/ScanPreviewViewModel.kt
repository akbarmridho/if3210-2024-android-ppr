package com.informatika.bondoman.viewmodel.scanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatika.bondoman.R
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.local.entity.transaction.Category
import com.informatika.bondoman.model.local.entity.transaction.Location
import com.informatika.bondoman.model.repository.transaction.TransactionRepository
import com.informatika.bondoman.prefdatastore.jwt.JWTManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ScanPreviewViewModel(
    private val transactionRepository: TransactionRepository,
    private val jwtManager: JWTManager
) : ViewModel() {
    private val _scanResult = MutableLiveData<ScanResult>()

    val scanResult: LiveData<ScanResult> = _scanResult

    fun scanImage(buffer: ByteArray, location: Location?) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = transactionRepository.uploadBill(jwtManager.getToken(), buffer)) {
                is Resource.Success -> {

                    for (item in result.data) {
                        if (location == null) {
                            transactionRepository.insertTransaction(
                                item.name,
                                Category.EXPENSE,
                                item.qty * item.price,
                            )
                        } else {
                            transactionRepository.insertTransaction(
                                item.name,
                                Category.EXPENSE,
                                item.qty * item.price,
                                location
                            )
                        }
                    }

                    _scanResult.postValue(ScanResult(success = R.string.scan_success))
                }

                is Resource.Error -> {
                    _scanResult.postValue(ScanResult(error = R.string.scan_failed))
                }

                else -> {
                    // do nothing
                }
            }


        }
    }
}