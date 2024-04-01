package com.informatika.bondoman.model.remote.service

import com.informatika.bondoman.model.remote.response.UploadBillResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TransactionService {
    @Multipart
    @POST("bill/upload")
    fun uploadBill(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<UploadBillResponse>
}