package com.example.project

import android.util.Log
import java.lang.Exception
import java.security.PublicKey

class DataProcessor {
    /**
     *Performs operations such as compression, encryption and hashing based on the input configuration
     * @param data the input byteArray to be processed
     * @param encryptionKey the public key required for rsa encryption. If null, encryption will be disabled
     * @param compressionEnabled a flag indicating whether compression is enabled or not
     * @param needHashing a flag indicating whether hashing is enabled or not
     * @return ProcessedData which contains processed data, encrypted key and hash key
     */

    @Throws(DataProcessorException::class)
    fun processData(data: ByteArray, encryptionKey: PublicKey?, compressionEnabled: Boolean, hashingEnabled: Boolean): ProcessedData {
        var outputData: ByteArray = data
        var encryptedSecretKey: ByteArray? = null
        var hashKey: ByteArray? = null

        //performs compression if enabled
        if (compressionEnabled) {
            val compressor = Compressor()
            try {
                outputData = compressor.compress(outputData)
            } catch (exception: Exception) {
                Log.d("Compression operation", " ".plus(exception.message))
            }
        }

        //performs encryption if enabled
        if (encryptionKey != null) {
            val aesEncryptor = AesEncryptor()
            val rsaEncryptor = RsaEncryptor()
            try {
                val aesSecretKey = aesEncryptor.generateKey()
                outputData = aesEncryptor.encrypt(outputData, aesSecretKey)
                encryptedSecretKey = rsaEncryptor.encrypt(aesSecretKey.encoded, encryptionKey)
            } catch (exception: Exception) {
                Log.d("Encryption", " ".plus(exception.message))
            }
        }

        //performs hashing if enabled
        if(hashingEnabled) {
            val sha = SHA256()
            try {
                hashKey = sha.hash(outputData)
            } catch (exception: Exception) {
                Log.d("Hashing", " ".plus(exception.message))
            }
        }
        return ProcessedData(encryptedSecretKey, outputData, hashKey)
    }
}