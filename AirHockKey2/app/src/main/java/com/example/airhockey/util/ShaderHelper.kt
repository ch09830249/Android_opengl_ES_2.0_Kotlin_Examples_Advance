package com.example.airhockey.util

import android.opengl.GLES20.*
import android.util.Log
import android.opengl.GLES20.GL_COMPILE_STATUS
import android.opengl.GLES20.GL_LINK_STATUS
import android.opengl.GLES20.glGetProgramInfoLog
import android.opengl.GLES20.GL_VALIDATE_STATUS
import android.opengl.GLES20.glValidateProgram




class ShaderHelper {
    companion object {

        private val TAG = "ShaderHelper"

        fun compileVertexShader(shaderCode: String): Int {
            return compileShader(GL_VERTEX_SHADER, shaderCode)
        }

        fun compileFragmentShader(shaderCode: String): Int {
            return compileShader(GL_FRAGMENT_SHADER, shaderCode)
        }

        private fun compileShader(type: Int, shaderCode: String): Int {
            val shaderObjectId: Int = glCreateShader(type)
            // Check whether the creation of shader object is successful or not
            if (shaderObjectId == 0) {
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Could not create new shader.")
                }
                return 0
            }

            // Associate the shader object and the shader code
            glShaderSource(shaderObjectId, shaderCode)

            // Compile shader
            glCompileShader(shaderObjectId)

            // Retrieving the Compilation Status
            val compileStatus = IntArray(1)
            glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0)

            if (LoggerConfig.ON) {
                // Print the shader info log to the Android log output.
                Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:"
                        + glGetShaderInfoLog(shaderObjectId))
            }

            if (compileStatus[0] == 0) {
            // If it failed, delete the shader object.
                glDeleteShader(shaderObjectId)
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Compilation of shader failed.")
                }
                return 0;
            }
            return shaderObjectId;
        }

        fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
            // Create the program object
            val programObjectId: Int = glCreateProgram()
            if (programObjectId == 0) {
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Could not create new program")
                }
                return 0
            }

            // Attach both our vertex shader and our fragment shader to the program object
            glAttachShader(programObjectId, vertexShaderId)
            glAttachShader(programObjectId, fragmentShaderId)

            // Link the program
            glLinkProgram(programObjectId)

            // Retrieving the linking Status
            val linkStatus = IntArray(1)
            glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0)

            if (LoggerConfig.ON) {
                // Print the program info log to the Android log output.
                Log.v(TAG, "Results of linking program:\n"
                        + glGetProgramInfoLog(programObjectId))
            }

            if (linkStatus[0] == 0) {
                // If it failed, delete the program object.
                glDeleteProgram(programObjectId)
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Linking of program failed.")
                }
                return 0
            }
            return programObjectId

        }

        // Check whether the program is valid or not
        fun validateProgram(programObjectId: Int): Boolean {
            // Validate the program
            glValidateProgram(programObjectId)
            val validateStatus = IntArray(1)
            // Check the results
            glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0)
            Log.v(TAG, """
                 Results of validating program: ${validateStatus[0]}
                 Log:${glGetProgramInfoLog(programObjectId)}
                 """.trimIndent()
            )
            return validateStatus[0] != 0
        }
    }
}