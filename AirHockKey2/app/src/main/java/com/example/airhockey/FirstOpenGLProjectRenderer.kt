package com.example.airhockey


import android.opengl.GLES20.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLSurfaceView.Renderer

// Deprecated
class FirstOpenGLProjectRenderer: Renderer {
    override fun onSurfaceCreated(glUnused: GL10?, p1: EGLConfig?) {
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        // Set the viewport size
        glViewport(0, 0, width, height)    // Tell OpenGL the size of the surface it has available for rendering
    }

    override fun onDrawFrame(glUnused: GL10?) {
        // Clear the screen
        glClear(GL_COLOR_BUFFER_BIT) // Wipe out all colors on the screen and fill the screen with the color previously defined by our call to glClearColor()
    }
}