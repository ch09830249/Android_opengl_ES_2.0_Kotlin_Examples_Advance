package com.mobiledrivetech.arhud.practice4.Util

import android.content.Context
import java.io.IOException

import java.io.BufferedReader

import java.io.InputStreamReader

import java.io.InputStream

// Read the shader source code from R.raw
class ReadResouceText {
    companion object {
        fun readResoucetText(context: Context, resouceId: Int): String {
            val body = StringBuffer()
            try {
                val inputStream: InputStream = context.resources.openRawResource(resouceId)
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var nextline: String?
                while (bufferedReader.readLine().also { nextline = it } != null) {
                    body.append(nextline)
                    body.append("\n")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return body.toString()
        }
    }
}
