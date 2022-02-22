package com.example.airhockey.util

import android.content.Context
import android.content.res.Resources
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class TextResourceReader {
    companion object {
        fun readTextFileFromResource(context: Context, resourceId: Int): String {
            var body:StringBuilder  = StringBuilder()
            try {
                var inputStream: InputStream = context.getResources().openRawResource(resourceId);
                var inputStreamReader: InputStreamReader = InputStreamReader(inputStream)
                var bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
                var nextLine: String?
                // A little difference
                do {
                    nextLine = bufferedReader.readLine()
                    if (nextLine != null) {
                        body.append(nextLine)
                        body.append('\n')
                    } else {
                        break
                    }
                } while (true)
            } catch (e: IOException) {
                throw RuntimeException(
                        "Could not open resource: " + resourceId, e);
            } catch (nfe: Resources.NotFoundException) {
                throw RuntimeException("Resource not found: " + resourceId, nfe);
            }
            return body.toString();
        }
    }
}