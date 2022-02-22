package com.example.airhockkey

import android.opengl.GLES20
import javax.microedition.khronos.opengles.GL10
import com.example.airhockkey.Helper.MatrixHelper
import android.content.Context
import com.example.airhockkey.Helper.TextureHelper
import com.example.airhockkey.ShaderProgram.ColorShaderProgram
import com.example.airhockkey.ShaderProgram.TextureSharderProgram
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix
import com.example.airhockkey.Components.Line
import com.example.airhockkey.Components.Mallet
import com.example.airhockkey.Components.Table
import javax.microedition.khronos.egl.EGLConfig


class AirHockKeyRender4(context: Context) : Renderer {
    //紋理
    private val mContext: Context = context

    //投影矩陣
    private val mProjectionMatrix = FloatArray(16)

    //模型矩陣
    private val mModelMatrix = FloatArray(16)

    // 桌子, 線, 木槌
    private var table: Table? = null
    private var mallet: Mallet? = null
    private var line: Line? = null

    // 兩種 shader program, 一個給 texture 用, 另一個由給話單一顏色用
    private var textureSharderProgram: TextureSharderProgram? = null
    private var colorShaderProgram: ColorShaderProgram? = null

    // 兩個 textures
    private var textureid = 0
    private var textureid1 = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //清空螢幕，並顯示藍色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        // New Table, Line and Mallet objects
        table = Table()
        mallet = Mallet()
        line = Line()

        // For texture 的兩個shaders
        textureSharderProgram = TextureSharderProgram(mContext)

        // 畫其他物件的兩個shaders
        colorShaderProgram = ColorShaderProgram(mContext)

        // Load texture 1
        textureid = TextureHelper.loadTexture(mContext, R.drawable.image1)

        // Load texture 2
        textureid1 = TextureHelper.loadTexture(mContext, R.drawable.image2)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        //在Surface建立以後，每次surface尺寸大小發生變化，這個方法會被呼叫到，比如橫豎屏切換
        //設定螢幕的大小
        GLES20.glViewport(0, 0, width, height)

        //45度視野角建立一個透視投影，這個視椎體從z軸-1開始，-10結束
        MatrixHelper.perspetiveM(mProjectionMatrix, 45f, width.toFloat() / height.toFloat(), 1f, 10f)

        //設定為單位矩陣
        Matrix.setIdentityM(mModelMatrix, 0)

        //向z軸平移-3f
        Matrix.translateM(mModelMatrix, 0, 0f, 0f, -3f)

        //繞著x軸旋轉-60度
        Matrix.rotateM(mModelMatrix, 0, -60f, 1.0f, 0f, 0f)

        val temp = FloatArray(16)

        //矩陣相乘
        Matrix.multiplyMM(temp, 0, mProjectionMatrix, 0, mModelMatrix, 0)

        //把矩陣重複賦值到投影矩陣
        System.arraycopy(temp, 0, mProjectionMatrix, 0, temp.size)
    }

    override fun onDrawFrame(gl: GL10) {
        //清除螢幕所有顏色，然後重設glClearColor的顏色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        //畫桌子 (用texture shader)
        textureSharderProgram!!.useProgram()
        textureSharderProgram!!.setUniforms(mProjectionMatrix, textureid)
        textureSharderProgram!!.setUniforms1(textureid1)
        table!!.bindData(textureSharderProgram!!)
        table!!.draw()

        //畫木槌 (用一般 shader)
        colorShaderProgram!!.useProgram()
        colorShaderProgram!!.setUniforms(mProjectionMatrix)
        mallet!!.bindData(colorShaderProgram!!)
        mallet!!.draw()

        //畫線 (用一般 shader)
//        colorShaderProgram!!.useProgram()
//        colorShaderProgram!!.setUniforms(mProjectionMatrix)
        line!!.bindData(colorShaderProgram!!)
        line!!.draw()
    }

}