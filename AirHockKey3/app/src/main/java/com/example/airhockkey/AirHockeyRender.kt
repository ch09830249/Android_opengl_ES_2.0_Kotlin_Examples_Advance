package com.example.airhockkey

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLSurfaceView.Renderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer



class AirHockeyRender(private val mContext: Context) : Renderer {

    private val verticeData: FloatBuffer

    // Store the locations of the variables in the shader
    // private var u_color = 0      // No use
    private var a_color  = 0     // new (attribute vec4 a_Color;)
    private var a_position = 0

    private val COLOR_COMPONENT_COUNT = 3   // new (3 components: RGB)
    private val POSITION_COMPONENT_COUNT = 2
    private val BYTES_PER_FLOAT = 4

    // new (Because there two attributes in the tableVertices array, this stride help us retrieve the right attribute.)
    /* We have the same data array with both position attributes and color attributes. OpenGL cannot assume that
    the next position follows the previous position. If OpenGL reads vertex data, it needs to skip the color if it wants
    to read the next vertex data. data, so we need stride (STRIDE) to tell opengl how many bytes are between each position
    , so he knows how much to skip */
    private val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    // Draw the triangle based on the winding order (counterclockwise)
    private val tableVertices = floatArrayOf(
        // Vertices (In order not to write these vertices repeatedly)
        0f, 0f,
        // Vertex color (RGB)
        1f, 1f, 1f,

        -0.5f, -0.5f,
        0.7f, 0.7f, 0.7f,

        0.5f, -0.5f,
        0.7f, 0.7f, 0.7f,

        0.5f, 0.5f,
        0.7f, 0.7f, 0.7f,

        -0.5f, 0.5f,
        0.7f, 0.7f, 0.7f,

        -0.5f, -0.5f,
        0.7f, 0.7f, 0.7f,

        // line
        -0.5f, 0f,
        // line color
        1f, 0f, 0f,

        0.5f, 0f,
        // line color
        1f, 0f, 0f,

        // two points (mallets)
        0f, -0.25f,
        // point color
        1f, 0f, 0f,

        0f, 0.25f,
        // point color
        0f, 0f, 1f
    )

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // 當surface被創建時，GlsurfaceView會調用這個方法，這個發生在應用程序
        // 第一次運行的時候或者從其他Activity回來的時候都會調用

        // Clear the screen
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        // Read two shader source codes from the R.raw
        val fragment_shader_source: String = ReadResouceText.readResoucetText(mContext, R.raw.fragment_shader)
        val vertex_shader_source: String = ReadResouceText.readResoucetText(mContext, R.raw.vertex_shader)

        // Compile two shader source codes
        val mVertexshader: Int = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertex_shader_source)
        val mFragmentshader: Int = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragment_shader_source)

        // Link the program (Link two shader objects)
        val program: Int = ShaderHelper.linkProgram(mVertexshader, mFragmentshader)

        // Validate our program before we start using it
        ShaderHelper.volidateProgram(program)

        // Enable the OpenGL program
        GLES20.glUseProgram(program)

        // Get the location of our attribute, and we store that location in a_color
        a_color = GLES20.glGetAttribLocation(program, "a_Color");   // Modify

        // Get the location of our attribute
        a_position = GLES20.glGetAttribLocation(program, "a_Position")

        // 綁定a_position和verticeData頂點位置
        /*
           第1個參數，放shader中attribute的位置
           第2個參數，每個頂點有多少分量，我們這裡只有2個分量 (x, y表示位置)
           第3個參數，數據類型
           第4個參數，只有整形才有意義，忽略
           第5個參數，一個數組有多個屬性才有意義，我們只有一個屬性，傳0 (只有position)
           第6個參數，opengl從哪裡讀取數據
         */
        // Tell OpenGL where to find data for our attribute a_Position
        // An internal pointer of the buffer: To ensure that it starts reading at the very beginning, we call
        // position(0) to set the position to the beginning of our data
        verticeData.position(0)
        GLES20.glVertexAttribPointer(
            a_position, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT,
            false, STRIDE, verticeData
        )

        // Enable the attribute with a call
        GLES20.glEnableVertexAttribArray(a_position)

        // Start with the first color attribute, not the first position attribute, so we're skipping the first position
        verticeData.position(POSITION_COMPONENT_COUNT)
        GLES20.glVertexAttribPointer(
            a_color, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT,
            false, STRIDE, verticeData
        )

        // Enable the attribute with a call
        GLES20.glEnableVertexAttribArray(a_color)

    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        // 在Surface創建以後，每次surface尺寸大小發生變化，這個方法會被調用到，比如橫豎屏切換
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        // 當繪製每一幀數據的時候，會調用這個方法，這個方法一定要繪製一些東西，即使只是清空屏幕
        // 因為這個方法返回後，渲染區的數據會被交換並顯示在屏幕上，如果什麼都沒有畫，則會看到閃爍效果
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Draw rectangle
        // 指定著色器中u_color的顏色為白色
        // Draw the table
        // Update the value of u_Color in our shader code by calling glUniform4f()
        // White table
        // GLES20.glUniform4f(u_color, 1.0f, 1.0f, 1.0f, 1.0f)  // No use

        /*
          Vertex (two floats)
          First parameter: Draw triangles
          Second parameter: Vertex index starts from 0
          Third parameter: Read six vertices (two floats)

          Finally, we draw two triangles and assemble these two triangles as the rectangle

          第一個參數：繪製繪製三角形
          第二個參數：從頂點數組0索引開始讀
          第三個參數：讀入6個頂點

          最終繪製倆個三角形，組成矩形
         */
        // 改為 GL_TRIANGLE_FAN
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)

        // Draw the middle line
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2)

        // Draw two points (mallets)
        // Blue
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1)
        // Red
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1)
    }

    init {
        // Load the float array into the float buffer (native)
        verticeData = ByteBuffer.allocateDirect(tableVertices.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(tableVertices)
        verticeData.position(0)
    }


    // If we have a color value, how can we get the value of the corresponding color component
    fun color() {
        val red = Color.red(Color.GREEN) / 255f  // (0~255)
        val green = Color.green(Color.GREEN) / 255f
        val blue = Color.blue(Color.GREEN) / 255f


        val parseColor = Color.parseColor("#FFFFFF")
//        val red = Color.red(parseColor) / 255f
//        val green = Color.green(parseColor) / 255f
//        val blue = Color.blue(parseColor) / 255f
    }
}

