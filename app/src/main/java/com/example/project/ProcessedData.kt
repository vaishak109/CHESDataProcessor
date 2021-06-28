package com.example.project

class ProcessedData (encryptedAesKey: ByteArray?, outputData: ByteArray, hashKey: ByteArray?) {
    val aesKey: ByteArray? = aesKey
    val outputData: ByteArray = outputData
    val hashKey: ByteArray? = hashKey
}
