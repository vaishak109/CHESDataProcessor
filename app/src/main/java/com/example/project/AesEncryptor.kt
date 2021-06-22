package com.example.project

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class AesEncryptor {
    /**
     *generates a secret key required for aes encryption using KeyGenerator class
     * @return generated SecretKey
     */
    fun generateKey(): SecretKey {
        val keygen = KeyGenerator.getInstance(AES)
        val random = SecureRandom()
        keygen.init(KEY_SIZE, random)
        return keygen.generateKey()
    }

    /**
     *encrypt the ByteArray using Cipher class with "AES/ECB/PKCS5Padding" transformation
     * @param data the input byteArray to encrypt
     * @param key the secret key required for encryption
     * @return encrypted ByteArray
     */
    fun encrypt(data: ByteArray, key: SecretKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(data)
    }

    companion object {
        const val AES = "AES"
        const val KEY_SIZE = 256
        const val IV_SIZE = 16
        const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
    }
}