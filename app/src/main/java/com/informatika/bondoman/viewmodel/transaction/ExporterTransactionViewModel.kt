package com.informatika.bondoman.viewmodel.transaction

import androidx.lifecycle.ViewModel
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.local.ExportType
import com.informatika.bondoman.model.repository.transaction.TransactionRepository
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream

class ExporterTransactionViewModel(private var transactionRepository: TransactionRepository) :
    ViewModel() {
    suspend fun export(type: ExportType): ByteArrayOutputStream? {
        transactionRepository.getAllTransaction()
        val data = transactionRepository.listTransactionLiveData.value

        if (data !is Resource.Success) {
            return null
        }

        val outputStream = ByteArrayOutputStream()
        val headers = arrayOf(
            "date",
            "title",
            "category",
            "amount",
            "location",
        )

        val workbook: Workbook = if (type == ExportType.XLSX) {
            XSSFWorkbook()
        } else {
            HSSFWorkbook()
        }

        val worksheet = workbook.createSheet()
        val headerRow = worksheet.createRow(0)

        for ((cellIndex, header) in headers.withIndex()) {
            val cell = headerRow.createCell(cellIndex)
            cell.setCellValue(header)
        }

        for ((dataIndex, data) in data.data.withIndex()) {
            val dataRow = worksheet.createRow(dataIndex + 1)
            dataRow.createCell(0).setCellValue(data.createdAt)
            dataRow.createCell(1).setCellValue(data.title)
            dataRow.createCell(2).setCellValue(data.category.toString())
            dataRow.createCell(3).setCellValue(data.amount)
            dataRow.createCell(4).setCellValue(data.location?.adminArea)
        }

        workbook.write(outputStream)
        workbook.close()

        return outputStream
    }
}