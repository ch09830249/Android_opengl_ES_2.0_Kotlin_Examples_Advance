package com.example.airhockkey.Components

import android.opengl.GLES20
import com.example.airhockkey.ShaderProgram.TextureSharderProgram
import java.nio.ByteOrder
import java.nio.ByteBuffer
import java.nio.FloatBuffer


class Table {
    private val BYTES_PER_FLOAT = 4
    private val floatBuffer: FloatBuffer
    private val POSITION_COMPONENT_COUNT = 2
    private val TEXTURE_COMPONENT_COUNT = 2
    private val STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * BYTES_PER_FLOAT
    var tableVertices = floatArrayOf(
        //頂點座標xy
        0f, 0f,
        //ST紋理座標
        0.5f, 0.5f,

        -0.5f, -0.8f,
        0f, 0.9f,

        0.5f, -0.8f,
        1f, 0.9f,

        0.5f, 0.8f,
        1f, 0.1f,

        -0.5f, 0.8f,
        0f, 0.1f,

        -0.5f, -0.8f,
        0f, 0.9f
    )

    // 設定 shader program 中的變數
    fun bindData(textureSharderProgram: TextureSharderProgram) {
        // 設定 點的位置 到 shader 中 (attribute vec4 a_Position;)
        setAttributeLocation(
            0,
            textureSharderProgram.a_position,
            POSITION_COMPONENT_COUNT,
            STRIDE
        )

        // 設定 texture 到 shader 中 (attribute vec2 a_TextureCoordinates;)
        setAttributeLocation(
            POSITION_COMPONENT_COUNT,
            textureSharderProgram.a_TextureCoordinates,
            TEXTURE_COMPONENT_COUNT,
            STRIDE
        )
    }

    fun draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)
    }

    fun setAttributeLocation(
        dataOffset: Int,
        attributeLocation: Int,
        componentCount: Int,
        strite: Int
    ) {
        floatBuffer.position(dataOffset)
        GLES20.glVertexAttribPointer(
            attributeLocation, componentCount, GLES20.GL_FLOAT,
            false, strite, floatBuffer
        )
        GLES20.glEnableVertexAttribArray(attributeLocation)
        floatBuffer.position(0)
    }

    init {
        floatBuffer = ByteBuffer
            .allocateDirect(tableVertices.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(tableVertices)
    }
}