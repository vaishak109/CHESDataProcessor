package com.example.project

import java.security.MessageDigest

class SHA256 {

    /**
     *Completes the hash computation
     * @param data the input byteArray whose hash is to be computed
     * @return the array of bytes for the resulting hash value
     */
    fun hash(data: ByteArray): ByteArray {
        val md = MessageDigest.getInstance(SHA)
        return md.digest(data)
    }

    companion object {
        const val SHA = "SHA-256"
    }
}