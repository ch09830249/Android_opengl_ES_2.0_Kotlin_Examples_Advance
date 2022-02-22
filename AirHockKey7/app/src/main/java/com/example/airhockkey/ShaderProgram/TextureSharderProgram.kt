package com.example.airhockkey.ShaderProgram

import android.content.Context
import android.opengl.GLES20
import com.example.airhockkey.Helper.ShaderHelper
import com.example.airhockkey.R
import com.example.airhockkey.Util.ReadResouceText


class TextureSharderProgram(context: Context) {
    private val u_matrix: Int
    private val u_TextureUnit: Int
    private val u_TextureUnit1: Int

    val a_position: Int
    val a_TextureCoordinates: Int
    private val program: Int

    fun setUniforms(matrix: FloatArray?, textureId: Int) {
        GLES20.glUniformMatrix4fv(u_matrix, 1, false, matrix, 0)

        //把活動的紋理單元設定為紋理單元0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)

        //把紋理繫結到這個單元
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        //把選定的著色器，傳遞到到片段著色器的u_TextureUnit屬性
        GLES20.glUniform1i(u_TextureUnit, 0)
    }

    fun setUniforms1(textureId: Int) {

        //把活動的紋理單元設定為紋理單元1
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)

        //把紋理繫結到這個單元
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        //把選定的著色器，傳遞到到片段著色器的u_TextureUnit屬性
        GLES20.glUniform1i(u_TextureUnit1, 1)
    }

    fun useProgram() {
        //使用程式
        GLES20.glUseProgram(program)
    }

    init {
        //讀取著色器原始碼
        val fragment_shader_source = ReadResouceText.readResoucetText(context, R.raw.texture_fragment_shader)
        val vertex_shader_source = ReadResouceText.readResoucetText(context, R.raw.texture_vertex_shader)
        program = ShaderHelper.buildProgram(vertex_shader_source, fragment_shader_source)
        a_position = GLES20.glGetAttribLocation(program, "a_Position")
        a_TextureCoordinates = GLES20.glGetAttribLocation(program, "a_TextureCoordinates")
        u_matrix = GLES20.glGetUniformLocation(program, "u_Matrix")
        u_TextureUnit = GLES20.glGetUniformLocation(program, "u_TextureUnit")
        u_TextureUnit1 = GLES20.glGetUniformLocation(program, "u_TextureUnit1")
    }
}