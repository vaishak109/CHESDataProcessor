package com.example.project

import android.util.Log
import java.lang.Exception

class DataProcessor {
    /**
     *Performs operations such as compression, encryption and hashing based on the input configuration
     * @param data the input byteArray to be processed
     * @param encryptionRSAKey the public key required for rsa encryption. If null, encryption will be disabled
     * @param compressionEnabled a flag indicating whether compression is enabled or not
     * @param hashingEnabled a flag indicating whether hashing is enabled or not
     * @return ProcessedData which contains processed data, encrypted key and hash key
     * @throws DataProcessorException
     */

    @Throws(DataProcessorException::class)
    fun processData(data: ByteArray, encryptionRSAKey: String?, compressionEnabled: Boolean, hashingEnabled: Boolean): ProcessedData {
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
                throw DataProcessorException(exception.message.toString())
            }
        }

        //performs encryption if enabled
        if (encryptionRSAKey != null) {
            val aesEncryptor = AesEncryptor()
            val rsaEncryptor = RsaEncryptor()
            try {
                val aesSecretKey = aesEncryptor.generateKey()
                outputData = aesEncryptor.encrypt(outputData, aesSecretKey)
                val rsaPublicKey = rsaEncryptor.getPublicKeyFromBase64EncodedString(encryptionRSAKey)
                encryptedSecretKey = rsaEncryptor.encrypt(aesSecretKey.encoded, rsaPublicKey)
            } catch (exception: Exception) {
                Log.d("Encryption", " ".plus(exception.message))
                throw DataProcessorException(exception.message.toString())
            }
        }

        //performs hashing if enabled
        if(hashingEnabled) {
            val sha = SHA256()
            try {
                hashKey = sha.hash(outputData)
            } catch (exception: Exception) {
                Log.d("Hashing", " ".plus(exception.message))
                throw DataProcessorException(exception.message.toString())
            }
        }
        return ProcessedData(encryptedSecretKey, outputData, hashKey)
    }
}