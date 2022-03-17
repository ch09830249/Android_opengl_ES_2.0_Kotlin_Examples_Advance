package com.mobiledrivetech.arhud.sinewave

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.airhockkey.Helper.ShaderHelper
import com.mobiledrivetech.arhud.practice3.Helper.MatrixHelper
import com.mobiledrivetech.arhud.practice3.Util.ReadResouceText
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SineWaveRenderer(private val mContext: Context, private val waveCount: Int, private val startAngle: Int, private val endAngle: Int, private val samplePointsIntervalAngle: Int) : GLSurfaceView.Renderer {
    // samplePointsIntervalAngle: 每隔多少角度取一點
    private var offset = 0
    private val wavePointsCount = (endAngle - startAngle) / samplePointsIntervalAngle + 1                      // 1: Additional point when x is 0
    private val vertexArray = FloatArray(wavePointsCount * waveCount * POSITION_COMPONENT_COUNT)
    private var floatBuffer: FloatBuffer? = null                                                               // A reference of the Buffer in the native environment

    // The Location of each variables in the shader program
    private var program = 0
    private var a_position = 0
    private var u_color = 0
    private var u_matrix = 0

    // Each 4X4 matrix
    private val mProjectionMatrix = FloatArray(16)          // projection matrix
    private val mModelMatrix = FloatArray(16)               // model matrix
    private val mViewMatrix = FloatArray(16)                // view matrix

    private val mPVMatrix = FloatArray(16)      // The product of the projection and view matrix
    private val mPVMMatrix = FloatArray(16)     // The product of the projection, view, and model matrix


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        generateSineWavePoints1(vertexArray)
        initVertexData(vertexArray)
        loadShaderAndProgram(mContext)
        loadShaderAttributes()
        bindAttributes()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        // Set matrices
        if (height > width) {
            // Portrait mode
            MatrixHelper.perspetiveM(mProjectionMatrix, 45f, width.toFloat() / height.toFloat(), 1f, 10f)

            Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 4f, 0f, 0f, 0f, 0f, 1f, 0f)

            Matrix.setIdentityM(mModelMatrix, 0)

            Matrix.scaleM(mModelMatrix, 0, 2f, 0.3f, 1f)

    //        Matrix.translateM(mModelMatrix, 0, -4.0f, 0.0f, -7.0f)
            Matrix.rotateM(mModelMatrix, 0, 0.0f, 0.0f, 1.0f, 0.0f)
        } else {
            // Landscape mode
            MatrixHelper.perspetiveM(mProjectionMatrix, 45f, width.toFloat() / height.toFloat(), 1f, 10f)

            Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 4f, 0f, 0f, 0f, 0f, 1f, 0f)

            Matrix.setIdentityM(mModelMatrix, 0)

            Matrix.scaleM(mModelMatrix, 0, 8f, 1f, 1f)

    //        Matrix.translateM(mModelMatrix, 0, -4.0f, 0.0f, -7.0f)
    //        Matrix.rotateM(mModelMatrix, 0, 60.0f, 0.0f, 1.0f, 0.0f)
        }

        Matrix.multiplyMM(mPVMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
        Matrix.multiplyMM(mPVMMatrix, 0, mPVMatrix, 0, mModelMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        transmitWave(vertexArray, 0.01f, TRANSMIT_LEFT)
        initVertexData(vertexArray)
        bindAttributes()
        GLES20.glUniformMatrix4fv(u_matrix, 1, false, mPVMMatrix, 0)
        GLES20.glUniform4f(u_color, 1.0f, 0.0f, 0.0f, 1.0f)
        for (i in 0 until waveCount) {
            GLES20.glDrawArrays(GLES20.GL_POINTS, i * wavePointsCount, wavePointsCount)
        }
    }

    private fun generateSineWavePoints(vertexData: FloatArray) {
        var z = 0.0f
        for (j in 1..waveCount) {
            for (i in startAngle..endAngle step samplePointsIntervalAngle) {
                val radian = Math.toRadians(i.toDouble())
                val cosineValue = kotlin.math.sin(radian)
                if (cosineValue > 1 || cosineValue < -1)
                    continue
                vertexData[offset++] = (i/endAngle.toFloat())
                vertexData[offset++] = cosineValue.toFloat()
                vertexData[offset++] = z
                vertexData[offset++] = 1.0f
            }
            z += 0.01f
        }
    }

    private fun generateSineWavePoints1(vertexData: FloatArray) {
        var z = 0.0f
        var changeY = 1.0f      // 1.0 ~ -1.0
        for (j in 1..waveCount) {
            for (i in startAngle..endAngle step samplePointsIntervalAngle) {
                val radian = Math.toRadians(i.toDouble())
                val cosineValue = kotlin.math.sin(radian)
                if (cosineValue > 1 || cosineValue < -1)
                    continue
                vertexData[offset++] = (i/endAngle.toFloat())
                vertexData[offset++] = cosineValue.toFloat() * changeY
                vertexData[offset++] = z
                vertexData[offset++] = 1.0f
            }
            changeY -= 2.0f / waveCount
            z += 0.01f
        }
    }

//    取左邊點的y座標
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

    private fun transmitWave (vertexData: FloatArray, shiftDst: Float, direction: String) {
        when (direction) {
            "right" -> {
                for (i in vertexData.indices) {
                    if (i % 4 == 0) {
                        vertexData[i] += shiftDst
                        if (vertexData[i] > 1.0f)
                            vertexData[i] = vertexData[i] - 2.0f
                    } else {
                        continue
                    }
                }
            }
            "left" -> {
                for (i in vertexData.indices) {
                    if (i % 4 == 0) {
                        vertexData[i] -= shiftDst
                        if (vertexData[i] < -1.0f)
                            vertexData[i] = 2 + vertexData[i]
                    } else {
                        continue
                    }
                }
            }
            else -> {}
        }
    }

//    private fun shrinkAmplitude (vertexData: FloatArray, shrinkRatio: Float) {
//        for (i in vertexData.indices) {
//            if (i % 4 == 1) {
//                vertexData[i] = vertexData[i] * shrinkRatio
//            } else {
//                continue
//            }
//        }
//    }

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
        u_matrix = GLES20.glGetUniformLocation(program, "u_Matrix")
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
        private const val TRANSMIT_RIGHT = "right"
        private const val TRANSMIT_LEFT = "left"
    }
}
