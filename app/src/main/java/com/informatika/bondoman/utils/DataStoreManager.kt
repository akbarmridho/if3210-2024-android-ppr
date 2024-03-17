package com.informatika.bondoman.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.security.crypto.EncryptedFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.MasterKey
import io.github.osipxd.security.crypto.createEncrypted
import java.io.File

class DataStoreManager(private val context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val dataStore: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.createEncrypted {
            EncryptedFile.Builder(
                context,
                dataStoreFile("jwt_token.preferences_pb"),
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
        }
    }

    fun dataStoreFile(filename: String): File {
        return File(context.filesDir, "datastore/$filename")
    }

}
