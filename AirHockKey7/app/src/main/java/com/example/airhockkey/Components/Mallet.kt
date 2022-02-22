package com.example.airhockkey.Components

import android.opengl.GLES20
import com.example.airhockkey.ShaderProgram.ColorShaderProgram
import java.nio.ByteOrder
import java.nio.ByteBuffer
import java.nio.FloatBuffer


class Mallet {
    private val BYTES_PER_FLOAT = 4
    private val floatBuffer: FloatBuffer
    private val POSITION_COMPONENT_COUNT = 2
    private val COLOR_COMPONENT_COUNT = 3
    private val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT
    var MalletVertices = floatArrayOf(
        //頂點
        0f, -0.4f,
        //rgb
        1f, 0f, 0f,


        0f, 0.4f,
        0f, 0f, 1f
    )

    fun bindData(colorShaderProgram: ColorShaderProgram) {
        setAttributeLocation(
            0,
            colorShaderProgram.a_position,
            POSITION_COMPONENT_COUNT,
            STRIDE
        )
        setAttributeLocation(
            POSITION_COMPONENT_COUNT,
            colorShaderProgram.a_color,
            COLOR_COMPONENT_COUNT,
            STRIDE
        )
    }

    fun draw() {
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 2)
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
            .allocateDirect(MalletVertices.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(MalletVertices)
    }
}