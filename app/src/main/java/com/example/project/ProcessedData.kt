package com.example.project

class ProcessedData (encryptedAesKey: ByteArray?, outputData: ByteArray, hashKey: ByteArray?) {
    val encryptedAesKey: ByteArray? = encryptedAesKey
    val outputData: ByteArray = outputData
    val hashKey: ByteArray? = hashKey
}
