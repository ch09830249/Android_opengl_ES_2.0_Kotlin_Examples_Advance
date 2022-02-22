package com.example.airhockkey

import android.content.Context
import android.opengl.GLES20
import javax.microedition.khronos.opengles.GL10
import java.nio.ByteOrder
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix


class AirHockKeyRender3(context: Context) : Renderer {
    //調整寬高比
    private val verticeData: FloatBuffer
    private val BYTES_PER_FLOAT = 4
    private val POSITION_COMPONENT_COUNT = 2
    private val COLOR_COMPONENT_COUNT = 3
    private val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT
    private val mContext: Context

    //逆時針繪製三角形
    var tableVertices = floatArrayOf(

        //頂點
        0f, 0f,
        //頂點顏色值
        1f, 1f, 1f,

        -0.5f, -0.8f,
        0.7f, 0.7f, 0.7f,

        0.5f, -0.8f,
        0.7f, 0.7f, 0.7f,

        0.5f, 0.8f,
        0.7f, 0.7f, 0.7f,

        -0.5f, 0.8f,
        0.7f, 0.7f, 0.7f,

        -0.5f, -0.8f,
        0.7f, 0.7f, 0.7f,

        //線
        -0.5f, 0f,
        1f, 0f, 0f,

        0.5f, 0f,
        0f, 1f, 0f,

        //點
        0f, -0.4f,
        1f, 0f, 0f,

        0f, 0.4f,
        0f, 0f, 1f
    )
    private var a_position = 0
    private var a_color = 0

    //模型矩陣
    private val mModelMatrix = FloatArray(16)

    //投影矩陣
    private val mProjectionMatrix = FloatArray(16)
    private var u_matrix = 0

    override fun onSurfaceCreated(p0: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
        //當surface被建立時，GlsurfaceView會呼叫這個方法，這個發生在應用程式
        // 第一次執行的時候或者從其他Activity回來的時候也會呼叫

        //清空螢幕
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        //讀取著色器原始碼
        val fragment_shader_source = ReadResouceText.readResoucetText(mContext, R.raw.fragment_shader)
        val vertex_shader_source = ReadResouceText.readResoucetText(mContext, R.raw.vertex_shader)

        //編譯著色器原始碼
        val mVertexshader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertex_shader_source)
        val mFragmentshader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragment_shader_source)

        //連結程式
        val program = ShaderHelper.linkProgram(mVertexshader, mFragmentshader)

        //驗證opengl物件
        ShaderHelper.volidateProgram(program)

        //使用程式
        GLES20.glUseProgram(program)

        //獲取shader屬性
        a_position = GLES20.glGetAttribLocation(program, "a_Position")
        a_color = GLES20.glGetAttribLocation(program, "a_Color")
        u_matrix = GLES20.glGetUniformLocation(program, "u_Matrix")


        //繫結a_position和verticeData頂點位置
        /**
         * 第一個引數，這個就是shader屬性
         * 第二個引數，每個頂點有多少分量，我們這個只有來個分量
         * 第三個引數，資料型別
         * 第四個引數，只有整形才有意義，忽略
         * 第5個引數，一個數組有多個屬性才有意義，我們只有一個屬性，傳0
         * 第六個引數，opengl從哪裡讀取資料
         */
        verticeData.position(0)
        GLES20.glVertexAttribPointer(
            a_position, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT,
            false, STRIDE, verticeData
        )
        //開啟頂點
        GLES20.glEnableVertexAttribArray(a_position)
        verticeData.position(POSITION_COMPONENT_COUNT)
        GLES20.glVertexAttribPointer(
            a_color, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT,
            false, STRIDE, verticeData
        )

        //開啟頂點
        GLES20.glEnableVertexAttribArray(a_color)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        // 在Surface建立以後，每次surface尺寸大小發生變化，這個方法會被呼叫到，比如橫豎屏切換
        // 設定螢幕的大小
        GLES20.glViewport(0, 0, width, height)

        // 45度視野角建立一個透視投影(矩陣)，這個視椎體從z軸-1開始，-10結束
        MatrixHelper.perspetiveM(mProjectionMatrix, 45f, width.toFloat() / height.toFloat(), 1f, 10f)

        // 設定為單位矩陣
        Matrix.setIdentityM(mModelMatrix, 0)

        // 向z軸平移-2f
        Matrix.translateM(mModelMatrix, 0, 0f, 0f, -2f)

        // 向z軸平移-2f
        Matrix.translateM(mModelMatrix, 0, 0f, 0f, -2.5f)

        //繞著x軸旋轉-60度
        Matrix.rotateM(mModelMatrix, 0, -60f, 1.0f, 0f, 0f)     // a: 旋轉角度 (正方向: winding)

        val temp = FloatArray(16)

        //矩陣相乘
        Matrix.multiplyMM(temp, 0, mProjectionMatrix, 0, mModelMatrix, 0)

        //把矩陣重複賦值到投影矩陣
        System.arraycopy(temp, 0, mProjectionMatrix, 0, temp.size)
    }

    override fun onDrawFrame(gl: GL10) {
        //當繪製每一幀資料的時候，會呼叫這個放方法，這個方法一定要繪製一些東西，即使只是清空螢幕
        //因為這個方法返回後，渲染區的資料會被交換並顯示在螢幕上，如果什麼都沒有話，會看到閃爍效果
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glUniformMatrix4fv(u_matrix, 1, false, mProjectionMatrix, 0)

        //繪製長方形
        //指定著色器u_color的顏色為白色
        /**
         * 第一個引數：繪製繪製三角形
         * 第二個引數：從頂點陣列0索引開始讀
         * 第三個引數：讀入6個頂點
         *
         * 最終繪製倆個三角形，組成矩形
         */
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)

        //繪製分割線
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2)

        //繪製點
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1)
    }

    init {
        mContext = context
        //把float載入到本地記憶體
        verticeData = ByteBuffer.allocateDirect(tableVertices.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(tableVertices)
        verticeData.position(0)
    }
}