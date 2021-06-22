package com.example.project

import android.util.Log
import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater

class Compressor {

    val TAG = "Compressor"

    /**
     *Compress ByteArray using Deflater with the default compression level
     * @param data the input byteArray to compress
     * @return compressed ByteArray
     */
    fun compress(data: ByteArray): ByteArray {
        val deflater = Deflater()
        deflater.setInput(data)
        deflater.finish()

        val buffer = ByteArray(1024)
        val outputStream = ByteArrayOutputStream()
        outputStream.use {
            while (!deflater.finished()) {
                val countOfBytesCompressed = deflater.deflate(buffer)
                it.write(buffer, 0, countOfBytesCompressed)
            }
        }
        return outputStream.toByteArray()
    }
}