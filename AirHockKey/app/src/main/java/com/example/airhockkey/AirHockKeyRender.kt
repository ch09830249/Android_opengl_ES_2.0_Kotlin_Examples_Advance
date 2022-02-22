package com.example.airhockkey

import android.content.Context
import android.opengl.GLES20
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLSurfaceView.Renderer


class AirHockKeyRender(context: Context?) : Renderer {

    // This func is called when the surface is created. It may also be called when the device
    // wakes up or when the user switches back to our activity
    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        // Set the clear color
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
    }

    // This func is called when the size has changed (EX: Portrait => Landscape)
    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        // Set the viewport size (Tell OpenGL the size of the surface it has available for rendering)
        GLES20.glViewport(0, 0, width, height)      // Tell OpenGL the size of the surface it has available for rendering
    }

    // This func is called when it’s time to draw a frame
    // This method must draw something even if we clean the screen
    // After the result of this method returns, the data in the render area will be exchanged and displayed on the screen
    // If we do nothing in this method, we will see the flickering effect
    override fun onDrawFrame(gl: GL10) {
        // Wipe out all colors on the screen and fill the screen with the color previously defined by our call to glClearColor()
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }
}
