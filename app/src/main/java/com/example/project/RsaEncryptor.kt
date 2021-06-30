package com.example.project

import android.os.Build
import android.util.Base64
import java.security.PublicKey
import javax.crypto.Cipher
import java.security.spec.X509EncodedKeySpec
import java.security.KeyFactory
import java.security.spec.MGF1ParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource


class RsaEncryptor (){

    /**
     *returns the public key needed for RSA encryption from the given Base64 encoded Public Key string
     * @param publicKeyString a Base64 encoded RSA Public Key string
     * @return RSA Public Key
     */
    fun getPublicKeyFromBase64EncodedString(publicKeyString: String): PublicKey {
        val keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT)
        val publicKeySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(publicKeySpec);
    }

    /**
     *encrypt ByteArray using Cipher with RSA transformation
     * @param data the input byteArray to encrypt
     * @return encrypted ByteArray
     */
    fun encrypt(data: ByteArray, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance(node_rsa_init)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        cipher.init(
            Cipher.ENCRYPT_MODE, publicKey,
            OAEPParameterSpec("SHA-1", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT)
        )
        return cipher.doFinal(data)
    }

    companion object {
        const val RSA = "RSA"
        const val node_rsa_init = "RSA/ECB/OAEPWithSHA1AndMGF1Padding"
    }
}