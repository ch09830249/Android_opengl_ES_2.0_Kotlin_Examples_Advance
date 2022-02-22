package com.example.airhockkey.ShaderProgram

import android.content.Context
import android.opengl.GLES20
import com.example.airhockkey.Helper.ShaderHelper
import com.example.airhockkey.R
import com.example.airhockkey.Util.ReadResouceText


class ColorShaderProgram(context: Context) {
    val a_color: Int
    val a_position: Int
    private val u_matrix: Int
    private val program: Int

    fun setUniforms(matrix: FloatArray?) {
        GLES20.glUniformMatrix4fv(u_matrix, 1, false, matrix, 0)
    }

    fun useProgram() {
        //使用程式
        GLES20.glUseProgram(program)
    }

    init {
        //讀取著色器原始碼
        val fragment_shader_source = ReadResouceText.readResoucetText(context, R.raw.fragment_shader)
        val vertex_shader_source = ReadResouceText.readResoucetText(context, R.raw.vertex_shader)
        program = ShaderHelper.buildProgram(vertex_shader_source, fragment_shader_source)
        a_color = GLES20.glGetAttribLocation(program, "a_Color")
        a_position = GLES20.glGetAttribLocation(program, "a_Position")
        u_matrix = GLES20.glGetUniformLocation(program, "u_Matrix")
    }
}