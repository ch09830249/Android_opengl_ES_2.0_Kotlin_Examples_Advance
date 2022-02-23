package com.mobiledrivetech.arhud.cylinder.Components

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import com.example.airhockkey.Helper.ShaderHelper
import com.example.airhockkey.Util.ReadResouceText
import com.mobiledrivetech.arhud.cylinder.Helper.MatrixHelper
import com.mobiledrivetech.arhud.cylinder.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


class Cylinder private constructor() {

    private val TAG = "Cylinder"

    private var floatBuffer: FloatBuffer? = null
    private var program = 0
    private var a_position = 0
    private var u_matrix = 0
    private val mProjectionMatrix = FloatArray(16)
    private val mModelMatrix = FloatArray(16)

    // 視圖矩陣
    private val mViewMatrix = FloatArray(16)
    private val mViewProjectionMatrix = FloatArray(16)
    private val mViewModelProjectionMatrix = FloatArray(16)

    // 圓心 x 座標
    private val x = 0f

    // 圓心 y 座標
    private val y = 0f

    // 圓半徑
    private val r = 0.6f

    // 三角形個數
    private val count = 50

    // 儲存shader中變數位置
    private var u_color = 0
    private var offerset = 0    // 第offset個float
    private lateinit var vertextData: FloatArray

    //
    private val drawList: ArrayList<DrawCommand> = ArrayList()

    fun init(context: Context) {
        // 設定Model Matrix為單位矩陣
        Matrix.setIdentityM(mModelMatrix, 0)

        // 1 生成頂點
        // 2 加載頂點到本地內存
        val point = Geometry.Companion.Point(0f, 0f, 0f)        // 圓柱中心
        val cylinder = Geometry.Companion.Cylinder(point, 0.4f, 0.5f)
        createPuck(cylinder, 50)
        initVertexData(vertextData)

        // 3 加載著色器的源碼並且加載程序
        loadShaderAndProgram(context)

        // 4 加載著色器中的屬性
        loadShaderAttributes()

        // 5 把著色器屬性和頂點數據綁定起來，開啟使用頂點
        bindAttributes()
    }

    // 創建圓柱
    fun createPuck(cylinder: Geometry.Companion.Cylinder, number: Int) {

        // 計算畫圓柱一共需要的頂點數
        val size = sizeOfCricleInVerTices(number) * 2 + sizeOfCylinderInVerTices(number)    // *2: 上下兩個圓

        // 創建圓柱所需要的Float array
        vertextData = FloatArray(size * POSITION_COMPONENT_COUNT)

        // 創建頂部圓
        val cylinderTop = Geometry.Companion.Circle(cylinder.center.translateY(cylinder.height / 2), cylinder.radius)

        // 創建底部圓
        val cylinderBottom = Geometry.Companion.Circle(cylinder.center.translateY(-cylinder.height / 2), cylinder.radius)

        // 畫側面
        appendCylinder(cylinder, number)

        // 畫頂部圓
        appendCircle(cylinderTop, number, true)

        // 畫底部圓
        appendCircle(cylinderBottom, number, false)
    }

    private fun appendCircle(circle: Geometry.Companion.Circle, number: Int, color: Boolean) {
        //
        val startVertex = offerset / FLOATS_PER_VERTEX
        val numberVertices = sizeOfCricleInVerTices(number)
        vertextData[offerset++] = circle.center.x
        vertextData[offerset++] = circle.center.y
        vertextData[offerset++] = circle.center.z
        for (i in 0..number) {
            // 計算每個圓心角的角度
            val angle = i.toFloat() / number.toFloat() * (Math.PI.toFloat() * 2f)
            vertextData[offerset++] = circle.center.x + circle.radius * Math.cos(angle.toDouble())
                .toFloat()
            vertextData[offerset++] = circle.center.y
            vertextData[offerset++] = circle.center.z + circle.radius * Math.sin(angle.toDouble())
                .toFloat()
        }
        Log.d(TAG, "$startVertex/$numberVertices$color")

        drawList.add(object : DrawCommand {
            override fun draw() {
                if (color) {
                    GLES20.glUniform4f(u_color, 0.0f, 1.0f, 0.0f, 1f)
                } else {
                    GLES20.glUniform4f(u_color, 1.0f, 0.0f, 0.0f, 1f)
                }
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numberVertices)
            }
        })
    }

    fun appendCylinder(cylinder: Geometry.Companion.Cylinder, number: Int) {
        //
        val startVertex = offerset / FLOATS_PER_VERTEX
        Log.d(TAG, "appendCylinder: $offerset/")
        val numberVertices = sizeOfCylinderInVerTices(number)
        val yStart = cylinder.center.y - cylinder.height / 2
        val yEed = cylinder.center.y + cylinder.height / 2
        for (i in 0..number) {
            val angle = i.toFloat() / number.toFloat() * (Math.PI.toFloat() * 2f)
            val xPosition = cylinder.center.x + cylinder.radius * Math.cos(angle.toDouble())
                .toFloat()
            val zPosition = cylinder.center.z + cylinder.radius * Math.sin(angle.toDouble())
                .toFloat()
            vertextData[offerset++] = xPosition
            vertextData[offerset++] = yStart
            vertextData[offerset++] = zPosition
            vertextData[offerset++] = xPosition
            vertextData[offerset++] = yEed
            vertextData[offerset++] = zPosition
        }
        Log.d(TAG, "$startVertex/$numberVertices")
        drawList.add(object : DrawCommand {
            override fun draw() {
                GLES20.glUniform4f(u_color, 1.0f, 1.0f, 1.0f, 1f)
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numberVertices)
            }
        })
    }

    fun initVertexData(vertexData: FloatArray) {
        floatBuffer = ByteBuffer
            .allocateDirect(vertexData.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData)
        floatBuffer!!.position(0)
    }

    fun loadShaderAndProgram(context: Context) {
        val fragment_shader_source = ReadResouceText.readResoucetText(context, R.raw.simple_fragment_shader)
        val vertex_shader_source = ReadResouceText.readResoucetText(context, R.raw.simple_vertex_shader)

        val mVertexshader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertex_shader_source)
        val mFragmentshader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragment_shader_source)

        program = ShaderHelper.linkProgram(mVertexshader, mFragmentshader)

        ShaderHelper.volidateProgram(program)

        GLES20.glUseProgram(program)
    }

    fun loadShaderAttributes() {
        u_color = GLES20.glGetUniformLocation(program, "u_Color")
        a_position = GLES20.glGetAttribLocation(program, "a_Position")
        u_matrix = GLES20.glGetUniformLocation(program, "u_Matrix")
    }

    fun bindAttributes() {
        floatBuffer!!.position(0)
        GLES20.glVertexAttribPointer(a_position, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT,
            false, 0, floatBuffer)
        GLES20.glEnableVertexAttribArray(a_position)
    }

    /**
     * 根據屏幕寬高創建正交矩陣，修復寬高比問題
     * (這裡是創建 "透視投影矩陣" 和 "視圖矩陣")
     *
     * @param width
     * @param height
     */
    fun projectionMatrix(width: Int, height: Int) {

        // 45 度視野角創建一個透視投影，這個視椎體從z軸-1開始，-10結束
        MatrixHelper.perspetiveM(mProjectionMatrix, 45f, width.toFloat() / height.toFloat(), 1f, 10f)

        // 創建視圖矩陣
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 2f, 2f, 0f, 0f,
            0f, 0f, 1f, 0f)
    }

    /**
     * 開始畫圖
     */
    fun draw() {
        //设置圆的颜色 红色
        //设置矩阵数据
        Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
        positionTableInScreen()
        GLES20.glUniformMatrix4fv(u_matrix, 1, false, mViewModelProjectionMatrix, 0)

        for (command in drawList) {
            command.draw()
        }
    }

    private fun positionTableInScreen() {
        // 矩陣相乘
        Matrix.multiplyMM(mViewModelProjectionMatrix, 0, mViewProjectionMatrix,
            0, mModelMatrix, 0)
    }

    fun translate(x: Float, y: Float, z: Float) // 設置沿xyz軸移動
    {
        Log.d(TAG, "translate")
        Matrix.translateM(mModelMatrix, 0, x, y, z)
    }

    // 旋轉
    fun rotate(angle: Float, x: Float, y: Float, z: Float) { // 設置繞xyz軸移動
        Log.d(TAG, "rotate")
        Matrix.rotateM(mModelMatrix, 0, angle, x, y, z)
    }

    // 縮放
    fun scale(x: Float, y: Float, z: Float) {
        Log.d(TAG, "scale")
        Matrix.scaleM(mModelMatrix, 0, x, y, z)
    }

    interface DrawCommand {
        fun draw()
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3

        private const val BYTES_PER_FLOAT = 4
        const val FLOATS_PER_VERTEX = 3

        val instance = Cylinder()

        private fun sizeOfCricleInVerTices(number: Int): Int {
            // 切成number個三角形，需要一個重複的頂點和一個圓心頂點,所以需要加2
            return 1 + number + 1   // 2: (圓心, 頂點重複)才能圍成圓
        }

        private fun sizeOfCylinderInVerTices(number: Int): Int {
            // 圍繞頂部和底部圓的每一個頂點，都需要兩個頂點（上圓的頂點和下圓的頂點，他們x和z相同，只有y不同），並且前兩個頂點需要重複倆次才能閉合
            return (number + 1) * 2
        }
    }
}
