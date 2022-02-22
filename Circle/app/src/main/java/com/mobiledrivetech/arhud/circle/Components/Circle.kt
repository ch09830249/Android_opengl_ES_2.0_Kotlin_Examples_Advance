package com.example.airhockkey.Components

import android.opengl.GLES20
import com.example.airhockkey.Helper.ShaderHelper
import android.content.Context
import android.opengl.Matrix
import com.example.airhockkey.Util.ReadResouceText
import com.mobiledrivetech.arhud.circle.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


class Circle {
    private var floatBuffer: FloatBuffer? = null
    private var program = 0
    private var a_position = 0
    private var u_matrix = 0
    private val mProjectionMatrix = FloatArray(16)

    // 圓心 x 座標
    private val x = 0f

    // 圓心 y 座標
    private val y = 0f

    // 半徑
    private val r = 0.6f

    // 三角形個數
    private val count = 50
    private var u_color = 0

    fun init(context: Context) {
        //1 生成頂點
        val vertexData = vertexData

        //2 load頂點到本地內存
        initVertexData(vertexData)

        //3 Load shader source code and program
        loadShaderAndProgram(context)

        //4 加載著色器中的屬性
        loadShaderAttributes()

        //5 把著色器屬性和頂點數據綁定起來，開啟使用頂點
        bindAttributes()
    }

    //切分為count個三角形，需要一個重複的頂點和一個圓心頂點,所以需要加2
    //儲存頂點數據的容器
    /**
     * 生成圓的頂點
     *
     * @return 返回圓頂點的座標
     */
    val vertexData: FloatArray
        get() {
            //切成為count個三角形，需要一個 "重複的頂點" 和一個 "圓心頂點" ,所以需要加2
            val nodeCount = count + 2

            //存頂點數據的float array
            val vertexData = FloatArray(nodeCount * POSITION_COMPONENT_COUNT)
            var offset = 0

            // 設定圓心座標
            vertexData[offset++] = x
            vertexData[offset++] = y

            // 開始設定其他頂點
            for (i in 0 until count + 1) {
                val angleInRadians = (i.toFloat() / count.toFloat() * (Math.PI.toFloat() * 2f))     // 幾分之幾的圓
                vertexData[offset++] = x + r * Math.cos(angleInRadians.toDouble()).toFloat()
                vertexData[offset++] = y + r * Math.sin(angleInRadians.toDouble()).toFloat()
            }
            return vertexData
        }

    /**
     * 把頂點數據加載到本地內存中 (Native)
     *
     * @param vertexData 頂點數據
     */
    fun initVertexData(vertexData: FloatArray) {
        floatBuffer = ByteBuffer
            .allocateDirect(vertexData.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData)
        floatBuffer!!.position(0)
    }

    /**
     * Load shader code and program
     */
    fun loadShaderAndProgram(context: Context) {
        //Read shader source code
        val fragment_shader_source = ReadResouceText.readResoucetText(context, R.raw.simple_fragment_shader)
        val vertex_shader_source = ReadResouceText.readResoucetText(context, R.raw.simple_vertex_shader)

        // Compile
        val mVertexshader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertex_shader_source)
        val mFragmentshader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragment_shader_source)

        // Link
        program = ShaderHelper.linkProgram(mVertexshader, mFragmentshader)

        // Validate
        ShaderHelper.volidateProgram(program)

        // Use
        GLES20.glUseProgram(program)
    }

    /**
     * Load shader 中的變數
     */
    fun loadShaderAttributes() {
        //獲取shader屬性
        u_color = GLES20.glGetUniformLocation(program, "u_Color")
        a_position = GLES20.glGetAttribLocation(program, "a_Position")
        u_matrix = GLES20.glGetUniformLocation(program, "u_Matrix")
    }

    /**
     * 把著色器屬性和頂點數據綁定起來，開啟使用頂點
     */
    fun bindAttributes() {
        //綁定a_position和verticeData頂點位置
        /**
         * 第1個參數，這個就是shader屬性
         * 第2個參數，每個頂點有多少分量，我們這個只有來個分量
         * 第3個參數，數據類型
         * 第4個參數，只有整形才有意義，忽略
         * 第5個參數，一個數組有多個屬性才有意義，我們只有一個屬性，傳0
         * 第6個參數，opengl從哪裡讀取數據
         */
        floatBuffer!!.position(0)
        GLES20.glVertexAttribPointer(a_position, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT,
            false, 0, floatBuffer)

        // 開啟頂點
        GLES20.glEnableVertexAttribArray(a_position)
    }

    /**
     * 根據屏幕寬高創建正交矩陣，修復寬高比問題
     *
     * @param width  屏幕寬
     * @param height 屏幕高
     */
    fun projectionMatrix(width: Int, height: Int) {

        val a = if (width > height) width.toFloat() / height.toFloat() else height.toFloat() / width.toFloat()

        if (width > height) {
            Matrix.orthoM(mProjectionMatrix, 0, -a, a, -1f, 1f, -1f, 1f)
        } else {
            Matrix.orthoM(mProjectionMatrix, 0, -1f, 1f, -a, a, -1f, 1f)
        }
    }

    /**
     * 開始繪畫
     */
    fun draw() {
        //設置圓的顏色 (藍色)
        GLES20.glUniform4f(u_color, 0.0f, 0.0f, 1.0f, 1f)

        //設置矩陣數據
        GLES20.glUniformMatrix4fv(u_matrix, 1, false, mProjectionMatrix, 0)
        /**
         * 第1個參數：繪製繪製三角形
         * 第2個參數：從頂點數組0索引開始讀
         * 第3個參數：讀入幾個頂點
         *
         * 最終繪製成圓
         */
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, count + 2)
    }

    companion object {
        // 每個頂點包含的數據個數 (x, y)
        private const val POSITION_COMPONENT_COUNT = 2
        private const val BYTES_PER_FLOAT = 4
    }
}

