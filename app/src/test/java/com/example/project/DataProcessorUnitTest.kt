package com.example.project

import android.util.Base64
import java.security.PrivateKey
import javax.crypto.Cipher
import org.junit.Test
import java.security.KeyFactory
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.SecretKey
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.crypto.spec.SecretKeySpec
import org.bouncycastle.util.encoders.Hex


class DataProcessorUnitTest {
    val dataProcessor = DataProcessor()

    @Test
    fun encryptionAloneIsWorking() {
        val processedData = dataProcessor.processData(INPUT_DATA, PUBLIC_KEY, false, false)
        val privateKey = getRSAPrivateKey(PRIVATE_KEY)
        val decryptedAesKey = decryptAesKey(processedData.encryptedAesKey, privateKey)
        val decryptedData = aesDecrypt(processedData.outputData, decryptedAesKey)
//        println("Original data: " + String(INPUT_DATA))
//        println("Decrypted data: " + String(decryptedData))
        assert(String(INPUT_DATA) == String(decryptedData))
        assert(processedData.hashKey == null)
    }

    @Test
    fun compressionAloneIsWorking() {
        val processedData = dataProcessor.processData(INPUT_DATA, null, true, false)
        val compressedData = processedData.outputData
        val sizeAfterCompression = compressedData.size
//        println("Size before compression: $SIZE_BEFORE_COMPRESSION")
//        println("Size after compression: $sizeAfterCompression")
//        println(Hex.toHexString(processedData.outputData))
        assert(sizeAfterCompression <= SIZE_BEFORE_COMPRESSION)
        assert(processedData.encryptedAesKey == null)
        assert(processedData.hashKey == null)
    }

    @Test
    fun sha256HashingAloneIsWorking() {
        val expectedHashKey = "1ac4b6916efc3e144a08e9405d7c46af2b12340b70235228a0c7b540a2276ea3"
        val processedData = dataProcessor.processData(INPUT_DATA, null, false, true)
        val actualHashKey = Hex.toHexString(processedData.hashKey)
        assert(expectedHashKey == actualHashKey)
    }

    @Test
    fun `compression + encryption is working`() {
        val processedData = dataProcessor.processData(INPUT_DATA, PUBLIC_KEY, true, false)
        val privateKey = getRSAPrivateKey(PRIVATE_KEY)
        val decryptedAesKey = decryptAesKey(processedData.encryptedAesKey, privateKey)
        val decryptedCompressedData = aesDecrypt(processedData.outputData, decryptedAesKey)
        val sizeAfterCompression = decryptedCompressedData.size
//        println("Size before compression: $SIZE_BEFORE_COMPRESSION")
//        println("Size after compression: $sizeAfterCompression")
        assert(Hex.toHexString(decryptedCompressedData) == "789c4b4c0402852410504806018514105048050300a2070a91")
        assert(sizeAfterCompression <= SIZE_BEFORE_COMPRESSION)
    }

    @Test
    fun `compression + SHA256 hashing is working`() {
        val expectedHashKey = "d1e39a8fedbad7263716848bf28054eed57c7f5a9bd6779a589581137f5195a1"
        val processedData = dataProcessor.processData(INPUT_DATA, null, true, true)
        val compressedData = processedData.outputData
        val sizeAfterCompression = compressedData.size
        val hashKey = Hex.toHexString(processedData.hashKey)
        assert(sizeAfterCompression <= SIZE_BEFORE_COMPRESSION)
        assert(expectedHashKey == hashKey)
    }

    @Test
    fun `encryption + SHA256 hashing is working`() {
        val processedData = dataProcessor.processData(INPUT_DATA, PUBLIC_KEY, false, true)
        val privateKey = getRSAPrivateKey(PRIVATE_KEY)
        val decryptedAesKey = decryptAesKey(processedData.encryptedAesKey, privateKey)
        val decryptedData = aesDecrypt(processedData.outputData, decryptedAesKey)
        assert(String(INPUT_DATA) == String(decryptedData))
        assert(processedData.hashKey != null)
    }

    @Test
    fun `compression + encryption + SHA256 hashing is working` () {
        val processedData = dataProcessor.processData(INPUT_DATA, PUBLIC_KEY, true, true)
        val privateKey = getRSAPrivateKey(PRIVATE_KEY)
        val decryptedAesKey = decryptAesKey(processedData.encryptedAesKey, privateKey)
        val decryptedCompressedData = aesDecrypt(processedData.outputData, decryptedAesKey)
        val sizeAfterCompression = decryptedCompressedData.size
        assert(Hex.toHexString(decryptedCompressedData) == "789c4b4c0402852410504806018514105048050300a2070a91")
        assert(sizeAfterCompression <= SIZE_BEFORE_COMPRESSION)
        assert(processedData.hashKey != null)
    }

    fun getRSAPrivateKey(key: String): PrivateKey {
        val keyBytes = Base64.decode(key, 76)
        val spec = PKCS8EncodedKeySpec(keyBytes)
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePrivate(spec)
    }

    fun decryptAesKey(data: ByteArray?, privateKey: PrivateKey): SecretKey {
        val cipher = Cipher.getInstance(node_rsa_init)
        cipher.init(
            Cipher.DECRYPT_MODE,
            privateKey,
            OAEPParameterSpec("SHA-1", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT)
        )
        val keyBytes = cipher.doFinal(data)
        return SecretKeySpec(keyBytes, "AES")
    }

    fun aesDecrypt(data: ByteArray, key: SecretKey): ByteArray {
        val cipher = Cipher.getInstance(AES)
        cipher.init(Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(data)
    }

    companion object {
        val INPUT_DATA = "aaaaa bbbbb ccccc ddddd eeeeee".toByteArray()
        val SIZE_BEFORE_COMPRESSION = INPUT_DATA.size
        const val PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMosS0pxQyKzGvMjJ8reTJxEMB+M2VtVFAZKYf3fgTrnu5i3iQkA0s2iw8E/vMHl4uGTc7YqrgINB/7ISgkbpEFTsnLkMGzB7eGGOdSDUgqaz8Amay8v+5+kWBcvFad7n18hCNeLn09tdWFkB2SQtSOvvaFtXVJcD04Y5tuZDrCQIDAQAB"
        const val PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIyixLSnFDIrMa8yMnyt5MnEQwH4zZW1UUBkph/d+BOue7mLeJCQDSzaLDwT+8weXi4ZNztiquAg0H/shKCRukQVOycuQwbMHt4YY51INSCprPwCZrLy/7n6RYFy8Vp3ufXyEI14ufT211YWQHZJC1I6+9oW1dUlwPThjm25kOsJAgMBAAECgYB7Lc3Q1U6xKngYFYV3AvCTcN2oCn43FFsYZY/D9a1lv7wKpghxxlpj4KxB6tbuz+J5yghAcYltShCVG1qHMVzNHHPjzIxC9dV2iNrZS55HAfU6IrmL4jH3bu19jO457/F4E/3vercwJnDQajOikxiGiwX7Wud8lbj/dhyIwoz1aQJBAMbKMxad26PY/Bdg7ZCOTMWDdAiMNi2+o1ZK2fyd9jJKCumqL9Vpm+zZYaTUttdmJohd1JzckwWH+mvOCcy5RBsCQQC1HBU1bWNOMm/CzKRlek60F/6yBpeDMl0p1vAp4JWT57UiZnmkdiUet3yRlaX+QcpqCVuJiNas8rtLsiRVNherAkApfeTB8Lr6bPzZKFsNlBYEF9btPc7FJ4hSJ5duOc48WuYCNYp8YemwuwK9c8SiGukqB2OsRgHN4r7rJ3s2JulHAkEAiTvqZj2pTgux3C4tNCflk1nLSSLtlCCJr7aK7XCcZyLgglycOB6+KfRnk/mNeohn1MmabzEdWpfjjIxEM711WQJBAId1whM9m7HkuSwnjcPmBRWAVhmh32UDBTOn0CCINoVWBZVQLiHZ0UFCVlyXrxWF0F/71bzjJ0cj9xBlzt0eMd8="
        const val node_rsa_init = "RSA/ECB/OAEPWithSHA1AndMGF1Padding"
        const val AES = "AES/ECB/PKCS5Padding"
    }
}