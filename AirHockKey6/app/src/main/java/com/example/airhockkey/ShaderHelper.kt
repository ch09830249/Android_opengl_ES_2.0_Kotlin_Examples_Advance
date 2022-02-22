package com.example.airhockkey

import android.opengl.GLES20
import android.util.Log


class ShaderHelper {

    companion object {

        private val TAG = "ShaderHelper"

        fun compileShader(type: Int, source: String?): Int {
            // Create shader object and return its ID
            val shaderId = GLES20.glCreateShader(type)
            // Check whether the creation of shader object is successful or not
            if (shaderId == 0) {
                Log.e(TAG, "Could not create new shader.")
                return 0
            }

            // Associate the shader object and the shader code (Upload the shader source code)
            GLES20.glShaderSource(shaderId, source)

            // Compile shader source code
            GLES20.glCompileShader(shaderId)

            // Retrieve the Compilation Status (result)
            val compileStatus = IntArray(1)

            // 取出shaderId的編譯狀態並把他寫入compileStatus的0索引
            GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

            // Print the shader info log to the Android log output.
            Log.d(TAG, "For compilation: " + GLES20.glGetShaderInfoLog(shaderId))

            if (compileStatus[0] == 0) {
                // If it failed, delete the shader object
                GLES20.glDeleteShader(shaderId)
                Log.e(TAG, "Compilation of shader failed.")
                return 0
            }
            return shaderId
        }

        fun linkProgram(mVertexshader: Int, mFragmentshader: Int): Int {
            // Create the program object
            val programId = GLES20.glCreateProgram()
            if (programId == 0) {
                Log.e(TAG, "Could not create new program")
                return 0
            }
            // Attach both our vertex shader and fragment shader to the program object
            GLES20.glAttachShader(programId, mVertexshader)
            GLES20.glAttachShader(programId, mFragmentshader)

            // Link the program
            GLES20.glLinkProgram(programId)

            // Retrieve the linking Status
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0)

            // Print the program info log to the Android log output.
            Log.d(TAG, "For linking: " + GLES20.glGetProgramInfoLog(programId))

            if (linkStatus[0] == 0) {
                // If it failed, delete the program object.
                GLES20.glDeleteProgram(programId)
                Log.d(TAG, "Linking of program failed.")
                return 0
            }
            return programId
        }

        // Check whether the program is valid or not
        fun volidateProgram(program: Int): Boolean {
            GLES20.glValidateProgram(program)
            val validateStatus = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_VALIDATE_STATUS, validateStatus, 0)
            Log.d(TAG, """
                 Results of validating program: ${validateStatus[0]}
                 Log:${GLES20.glGetProgramInfoLog(program)}
                 """.trimIndent()
            )
            return validateStatus[0] != 0
        }

        fun buildProgram(vertex_shader_source: String?, fragment_shader_source: String?): Int {
            // Compile the shader source code
            val mVertexshader = compileShader(GLES20.GL_VERTEX_SHADER, vertex_shader_source)
            val mFragmentshader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragment_shader_source)

            // Linking the program
            val program = linkProgram(mVertexshader, mFragmentshader)
            
            // validation
            volidateProgram(program)
            return program
        }
    }
}

