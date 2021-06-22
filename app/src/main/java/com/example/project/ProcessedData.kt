package com.example.project

class ProcessedData (aesKey: ByteArray?, outputData: ByteArray, hashKey: ByteArray?) {
    val aesKey: ByteArray?
    val outputData: ByteArray
    val hashKey: ByteArray?
    init {
        this.aesKey = aesKey
        this.outputData = outputData
        this.hashKey = hashKey
    }
}