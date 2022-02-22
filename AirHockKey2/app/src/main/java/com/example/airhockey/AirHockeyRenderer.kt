package com.example.airhockey

import android.opengl.GLES20.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLSurfaceView.Renderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import android.content.Context
import com.example.airhockey.util.LoggerConfig
import com.example.airhockey.util.TextResourceReader
import com.example.airhockey.util.ShaderHelper


class AirHockeyRenderer(context: Context): Renderer {

    private val context: Context = context
    private var program: Int? = null
    private val U_COLOR = "u_Color"
    private var uColorLocation = 0
    private val A_POSITION = "a_Position"
    private var aPositionLocation = 0

    var vertexData: FloatBuffer? = null
    val BYTES_PER_FLOAT = 4
    val POSITION_COMPONENT_COUNT = 2

    val tableVertices = floatArrayOf(
        0f, 0f,
        0f, 14f,
        9f, 14f,
        9f, 0f
    )
    val tableVerticesWithTriangles = floatArrayOf(
        // Triangle 1
        -0.5f, -0.5f,
        0.5f, 0.5f,
        -0.5f, 0.5f,

        // Triangle 2
        -0.5f, -0.5f,
        0.5f, -0.5f,
        0.5f, 0.5f,

        // Line 1
        -0.5f, 0f,
        0.5f, 0f,

        // Mallets
        0f, -0.25f,
        0f, 0.25f
    )
    init {
        vertexData = ByteBuffer
            .allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexData!!.put(tableVerticesWithTriangles)
    }

    // Calls this when the surface is created. It may also be called when the device
    // wakes up or when the user switches back to our activity.
    override fun onSurfaceCreated(glUnused: GL10?, p1: EGLConfig?) {
        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        // Read the shader code from the R.raw
        val vertexShaderSource = TextResourceReader
            .readTextFileFromResource(context, R.raw.simple_vertex_shader)
        val fragmentShaderSource = TextResourceReader
            .readTextFileFromResource(context, R.raw.simple_fragment_shader)

        // Compile the shader code
        val vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource)

        // Link two shader objects
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader)

        // Validate our program before we start using it
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program!!)
        }

        // Enable the OpenGL program
        glUseProgram(program!!)

        // Get the location of our uniform, and we store that location in uColorLocation
        uColorLocation = glGetUniformLocation(program!!, U_COLOR)

        // Get the location of our attribute
        aPositionLocation = glGetAttribLocation(program!!, A_POSITION)

        // Tell OpenGL where to find data for our attribute a_Position
        // An internal pointer of the buffer: To ensure that it starts reading at the very beginning, we call
        // position(0) to set the position to the beginning of our data
        vertexData!!.position(0)
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
            false, 0, vertexData)

        // Enable the attribute with a call
        glEnableVertexAttribArray(aPositionLocation)
    }

    // Whenever the size has changed (Portrait => Landscape)
    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        // Set the viewport size (Tell OpenGL the size of the surface it has available for rendering)
        glViewport(0, 0, width, height)    // Tell OpenGL the size of the surface it has available for rendering
    }

    // When it’s time to draw a frame.
    override fun onDrawFrame(glUnused: GL10?) {
        // Clear the screen
        glClear(GL_COLOR_BUFFER_BIT) // Wipe out all colors on the screen and fill the screen with the color previously defined by our call to glClearColor()

        // Draw the table
        // Update the value of u_Color in our shader code by calling glUniform4f()
        // White table
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f)
        glDrawArrays(GL_TRIANGLES, 0, 6) // 0: start    6: the number of vertices

        // Draw the Dividing Line
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f)
        glDrawArrays(GL_LINES, 6, 2)

        // Draw the first mallet blue
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f)
        glDrawArrays(GL_POINTS, 8, 1)

        // Draw the second mallet red
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f)
        glDrawArrays(GL_POINTS, 9, 1)
    }
}