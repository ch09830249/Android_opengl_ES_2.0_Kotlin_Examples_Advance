package com.mobiledrivetech.arhud.practice2.Components

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import com.example.airhockkey.Helper.ShaderHelper
import com.mobiledrivetech.arhud.practice2.Helper.MatrixHelper
import com.mobiledrivetech.arhud.practice2.R
import com.mobiledrivetech.arhud.practice2.Util.ReadResouceText
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


class Cylinder private constructor() {
    private val TAG = "Cylinder"
    private var floatBuffer: FloatBuffer? = null    // A reference of the Buffer in the native environment
    private var program = 0                         // program ID
    private var a_position = 0                      // a_Position variable location (vertex shader)
    private var u_matrix = 0                        // u_Matrix variable location   (vertex shader)
    private var u_color = 0                         // u_Color variable location    (fragment shader)
    private val mProjectionMatrix = FloatArray(16)          // projection matrix
    private val mModelMatrix = FloatArray(16)               // model matrix
    private val mViewMatrix = FloatArray(16)                // view matrix
    private val mViewProjectionMatrix = FloatArray(16)      // The product of the view and projection matrix
    private val mViewModelProjectionMatrix = FloatArray(16)     // The product of the view, model, and projection matrix
    private var offerset = 0    // 第offset個float
    private lateinit var vertextData: FloatArray    // A reference of the vertices in the Delvik environment
    // Draw command list
    private val drawList: ArrayList<DrawCommand> = ArrayList()

    // This interface is for creating the drawing task and put the tasks into the above drawList
    interface DrawCommand {
        fun draw()
    }

    fun init(context: Context) {
        // Set Model Matrix as Identity matrix
        Matrix.setIdentityM(mModelMatrix, 0)
        // 1. 生成頂點
        // 2. 加載頂點到本地內存 (float array)
        val point = Geometry.Companion.Point(0f, 0f, 0f)        // The center of the cylinder
        val cylinder = Geometry.Companion.Cylinder(point, 0.4f, 0.5f)   // Create the cylinder object
        // 創建圓柱體 (冰球) 所需要的所有點 (Cylinder物件, 幾等分)
        createPuck(cylinder, 50)
        // 初始 float buffer 並將 創建好的vertices放入    (float array (Delvik) => float buffer (Native))
        initVertexData(vertextData)
        // 3. 加載著色器的源碼並且加載程序
        loadShaderAndProgram(context)
        // 4. 加載著色器中的屬性 (取shader中變數的位置)
        loadShaderAttributes()
        // 5. 把著色器屬性和頂點數據綁定起來，開啟使用頂點
        bindAttributes()
    }

    // 創建圓柱 (冰球)
    fun createPuck(cylinder: Geometry.Companion.Cylinder, number: Int) {
        // 計算畫圓柱一共需要的頂點數
        val size = sizeOfCricleInVerTices(number) * 2 + sizeOfCylinderInVerTices(number)    // *2: 上下兩個圓
        // 創建圓柱所需要的 Float array (放在Java的)
        vertextData = FloatArray(size * POSITION_COMPONENT_COUNT)
        // 創建頂部圓                                            // 圓柱中心向 "上" 移變 "頂" 部圓圓心
        val topCircle = Geometry.Companion.Circle(cylinder.center.translateY(cylinder.height / 2), cylinder.radius)
        // 創建底部圓                                            // 圓柱中心向 "下" 移變 "底" 部圓圓心
        val bottomCircle = Geometry.Companion.Circle(cylinder.center.translateY(-cylinder.height / 2), cylinder.radius)
        // 畫側面
        appendCylinder(cylinder, number)
        // 畫頂部圓
        appendCircle(topCircle, number, "Green")
        // 畫底部圓
        appendCircle(bottomCircle, number, "Blue")
    }

    // 創建冰球的上下圓
    private fun appendCircle(circle: Geometry.Companion.Circle, number: Int, color: String) {
        val startVertex = offerset / FLOATS_PER_VERTEX
        val numberVertices = sizeOfCricleInVerTices(number)
        // 圓心
        vertextData[offerset++] = circle.center.x
        vertextData[offerset++] = circle.center.y
        vertextData[offerset++] = circle.center.z

        // 圓邊上的頂點
        for (i in 0..number) {
            // 計算每個圓心角的角度
            val angle = i.toFloat() / number.toFloat() * (Math.PI.toFloat() * 2f)
            vertextData[offerset++] = circle.center.x + circle.radius * Math.cos(angle.toDouble()).toFloat()        // x + r (cos(theta))
            vertextData[offerset++] = circle.center.y
            vertextData[offerset++] = circle.center.z + circle.radius * Math.sin(angle.toDouble()).toFloat()        // y + r (sin(theta))
        }

        Log.d(TAG, "$startVertex/$numberVertices        Color: $color")

        drawList.add(object : DrawCommand {
            override fun draw() {
                // Select color based on the input
                when (color) {
                    "Red" -> GLES20.glUniform4f(u_color, 1.0f, 0.0f, 0.0f, 1f)
                    "Green" -> GLES20.glUniform4f(u_color, 0.0f, 1.0f, 0.0f, 1f)
                    else -> GLES20.glUniform4f(u_color, 0.0f, 0.0f, 1.0f, 1f)
                }
                // Draw the circle (Primitive: GL_TRIANGLE_FAN)
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numberVertices)
            }
        })
    }

    // 創建圓柱側面
    fun appendCylinder(cylinder: Geometry.Companion.Cylinder, number: Int) {
        val startVertex = offerset / FLOATS_PER_VERTEX
        Log.d(TAG, "appendCylinder: $offerset/")
        val numberVertices = sizeOfCylinderInVerTices(number)
        val yStart = cylinder.center.y - cylinder.height / 2    // 頂部圓的y座標
        val yEed = cylinder.center.y + cylinder.height / 2      // 底部圓的y座標
        for (i in 0..number) {
            val angle = i.toFloat() / number.toFloat() * (Math.PI.toFloat() * 2f)
            val xPosition = cylinder.center.x + cylinder.radius * Math.cos(angle.toDouble()).toFloat()  // x + r (cos(theta))
            val zPosition = cylinder.center.z + cylinder.radius * Math.sin(angle.toDouble()).toFloat()  // y + r (sin(theta))
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
                // The color of the cylinder's side: White
                GLES20.glUniform4f(u_color, 1.0f, 1.0f, 1.0f, 1f)
                // Draw the side of the cylinder (Primitive: GL_TRIANGLE_STRIP)
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
        GLES20.glVertexAttribPointer(a_position, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, floatBuffer)
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
        // 創建透視投影矩陣 (45 度視野角創建一個透視投影，這個視椎體從z軸-1開始，-10 結束)
        MatrixHelper.perspetiveM(mProjectionMatrix, 45f, width.toFloat() / height.toFloat(), 1f, 10f)
        // 創建視圖矩陣
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 2f, 0f, 0f, 0f, 0f, 1f, 0f)
    }

    /**
     * 開始畫圖
     */
    fun draw() {
        Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
        Matrix.multiplyMM(mViewModelProjectionMatrix, 0, mViewProjectionMatrix, 0, mModelMatrix, 0)
        GLES20.glUniformMatrix4fv(u_matrix, 1, false, mViewModelProjectionMatrix, 0)
        for (command in drawList) {
            command.draw()
        }
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
        private const val BYTES_PER_FLOAT = 4
        const val FLOATS_PER_VERTEX = 3

        // Singleton pattern (Only one instance)
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
