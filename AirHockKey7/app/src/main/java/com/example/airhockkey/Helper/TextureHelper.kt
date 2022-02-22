package com.example.airhockkey.Helper

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.opengl.GLUtils



class TextureHelper {

    companion object {

        private val TAG = "TextureHelper"

        fun loadTexture(context: Context, resourceId: Int): Int {
            val textureId = IntArray(1)
            //建立 Texture object 並取 ID
            // 建立一個紋理物件，opengl會把生成的紋理id儲存到textureObjectId中，如果返回值是0就表示失敗，如果返回值不為0就是成功
            GLES20.glGenTextures(1, textureId, 0)
            if (textureId[0] == 0) {
                Log.e(TAG, "Create texture object failed")
                return 0
            }

            val options = Options()
            // 利用 options.inScaled = false 表示告訴Android我們需要圖片的原始資料，而不是影象的縮小版本
            options.inScaled = false

            // 將原本jpg檔解碼成bitmap
            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)
            if (bitmap == null) {
                Log.e(TAG, "Create bitmap failed")
                // 刪除建立的 texture object
                GLES20.glDeleteTextures(1, textureId, 0)
                return 0
            }

            /*
              第一個引數：告訴opengl作為一個二維紋理對待
              第二個引數：告訴opengl，要繫結那個紋理id
            */
            // 綁定紋理id，第一個引數表示作為一個二維紋理對待，第二個引數表示，繫結到那個紋理物件ID
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0])

            //設定過濾器
            /*
              GL_TEXTURE_MIN_FILTER:在縮小的情況下用  GL_LINEAR_MIPMAP_LINEAR：三線性過濾
              GL_TEXTURE_MAG_FILTER：在放大的情況下用  GL_TEXTURE_MAG_FILTER：雙線性過濾
             */
            // 對於縮小的情況，我們選擇 GL_LINEAR_MIPMAP_LINEAR 他告訴opengl使用三線性過濾
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR_MIPMAP_LINEAR
            )
            // 對於放大的情況我們使用GL_LINEAR他告訴opengl使用雙線性過濾
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
            )

            // 把圖片載入到opengl裡面
            // 告訴opengl讀入bitmap圖片資料，並把它複製到當前繫結的紋理id上
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

            // 釋放bitmap
            bitmap.recycle()

            // 生成mip貼圖
            // 生成MIP貼圖是一個比較容易的事情
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)

            // 解除紋理繫結  第二個引數傳0 就是解除繫結
            // 既然我們完成了紋理的載入，那我們需要解除與這個紋理的繫結，這樣我們就不會發生呼叫其他紋理方法，意外改變這個紋理情況
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

            return textureId[0]
        }
    }
}