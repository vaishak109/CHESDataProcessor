package com.example.project

import java.security.PublicKey
import javax.crypto.Cipher
import java.security.spec.X509EncodedKeySpec
import java.security.KeyFactory

class RsaEncryptor (publicKey: String){

    private val publicKey: PublicKey
    init {
        val publicKeySpec = X509EncodedKeySpec(publicKey.toByteArray())
        val keyFactory = KeyFactory.getInstance(RSA);
        this.publicKey = keyFactory.generatePublic(publicKeySpec);
    }

    /**
     *encrypt ByteArray using Cipher with RSA transformation
     * @param data the input byteArray to encrypt
     * @return encrypted ByteArray
     */
    fun encrypt(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(RSA)
        cipher.init(Cipher.ENCRYPT_MODE, this.publicKey)
        return cipher.doFinal(data)
    }

    companion object {
        const val RSA = "RSA"
    }
}