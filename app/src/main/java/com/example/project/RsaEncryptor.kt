package com.example.project

import java.security.PublicKey
import javax.crypto.Cipher

class RsaEncryptor {
    /**
     *encrypt ByteArray using Cipher with RSA transformation
     * @param data the input byteArray to encrypt
     * @param publicKey the public key required for encryption
     * @return encrypted ByteArray
     */
    fun encrypt(data: ByteArray, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance(RSA)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data)
    }

    companion object {
        const val RSA = "RSA"
    }
}