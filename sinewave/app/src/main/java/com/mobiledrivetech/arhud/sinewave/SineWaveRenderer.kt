package com.mobiledrivetech.arhud.sinewave

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.example.airhockkey.Helper.ShaderHelper
import com.mobiledrivetech.arhud.practice3.Util.ReadResouceText
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SineWaveRenderer(private val mContext: Context, private val numberOfWaves: Int, private val startAngle: Int, private val endAngle: Int, private val a: Int) : GLSurfaceView.Renderer {
    // a: 每隔多少角度取一點
    private var offset = 0
    private val WAVE_POINTS_COUNT = (endAngle - startAngle) / a + 1
    private val vertexArray = FloatArray(WAVE_POINTS_COUNT * numberOfWaves * POSITION_COMPONENT_COUNT)          // 73 * 20 (number of waves) * 4 (number of components in each vertex)
    private var floatBuffer: FloatBuffer? = null                                                                     // A reference of the Buffer in the native environment
    private var amplitude = 0.5f

    private var program = 0  // program ID
    private var a_position = 0
    private var u_color = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        generateSineWavePoints(vertexArray)
        shrinkAmplitude(vertexArray, amplitude)
        initVertexData(vertexArray)
        loadShaderAndProgram(mContext)
        loadShaderAttributes()
        bindAttributes()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        transmitWave(vertexArray, 0.01f, true)
        initVertexData(vertexArray)
        bindAttributes()
        GLES20.glUniform4f(u_color, 1.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, WAVE_POINTS_COUNT * numberOfWaves)            // 73 * 20 (number of waves)
    }

    private fun generateSineWavePoints(vertexData: FloatArray) {
        var w = 1.0f
        var x = 0.0f
        for (j in 1..numberOfWaves) {
            for (i in startAngle..endAngle step a) {
                val radian = Math.toRadians(i.toDouble())
                val cosineValue = kotlin.math.sin(radian)
                if (cosineValue > 1 || cosineValue < -1)
                    continue
                vertexData[offset++] = (i/endAngle.toFloat()) + x
                vertexData[offset++] = cosineValue.toFloat()
                vertexData[offset++] = 0.0f
                vertexData[offset++] = w
            }
            x += 0.01f
            w += 0.01f
        }
    }

    // 取左邊點的y座標
//    private fun transmitWaveRight (vertexData: FloatArray) {
//        val lastVertexY = vertexData[289]   // 紀錄最後一個點的y座標值
//        for (i in 291 downTo 4) {
//            if (i % 4 == 1) {
//                val nextVertexY = i - 4
//                vertexData[i] = vertexData[nextVertexY]
//            } else {
//                continue
//            }
//            vertexData[1] = lastVertexY
//        }
//    }

    private fun transmitWave (vertexData: FloatArray, shift: Float, right: Boolean) {
        if (right) {
            for (i in vertexData.indices) {
                if (i % 4 == 0) {
                    vertexData[i] += shift
                    if (vertexData[i] > 1.0f)
                        vertexData[i] = vertexData[i] - 2.0f
                } else {
                    continue
                }
            }
        } else {
            for (i in vertexData.indices) {
                if (i % 4 == 0) {
                    vertexData[i] -= shift
                    if (vertexData[i] < -1.0f)
                        vertexData[i] = 2 + vertexData[i]
                } else {
                    continue
                }
            }
        }
    }

    private fun shrinkAmplitude (vertexData: FloatArray, shrinkRatio: Float) {
        for (i in vertexData.indices) {
            if (i % 4 == 1) {
                vertexData[i] = vertexData[i] * shrinkRatio
            } else {
                continue
            }
        }
    }

    private fun initVertexData(vertexData: FloatArray) {
        floatBuffer = ByteBuffer
            .allocateDirect(vertexData.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData)
        floatBuffer!!.position(0)
    }

    private fun loadShaderAndProgram(context: Context) {

        val fragmentShaderSource = ReadResouceText.readResoucetText(context, R.raw.fragment_shader)
        val vertexShaderSource = ReadResouceText.readResoucetText(context, R.raw.vertex_shader)

        val mVertexshader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource)
        val mFragmentshader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource)

        program = ShaderHelper.linkProgram(mVertexshader, mFragmentshader)

        ShaderHelper.volidateProgram(program)

        GLES20.glUseProgram(program)
    }

    private fun loadShaderAttributes() {
        u_color = GLES20.glGetUniformLocation(program, "u_Color")
        a_position = GLES20.glGetAttribLocation(program, "a_Position")
    }

    private fun bindAttributes() {
        floatBuffer!!.position(0)
        GLES20.glVertexAttribPointer(a_position, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, floatBuffer)
        GLES20.glEnableVertexAttribArray(a_position)
    }

    companion object {
        private const val TAG = "SineWaveRenderer"
        private const val POSITION_COMPONENT_COUNT = 4
        private const val BYTES_PER_FLOAT = 4
    }
}
