package com.example.project

/**
 * Processed data
 *
 * This class represents the processed data after undergoing compression, encryption and hashing based on the configuration
 *
 * @property encryptedAesKey the RSA encrypted AES Secret Key. If encryption is disabled this field will be null
 * @property outputData the processed data after undergoing compression and encryption based on the configuration
 * @property hashKey the SHA256 digest of the processed data(compression + encryption based on the configuration). If disabled this field will be null
 * @constructor creates an instance of the DataProcessor class with the given properties
 */
class ProcessedData (encryptedAesKey: ByteArray?, outputData: ByteArray, hashKey: ByteArray?) {
    val encryptedAesKey: ByteArray? = encryptedAesKey
    val outputData: ByteArray = outputData
    val hashKey: ByteArray? = hashKey
}
