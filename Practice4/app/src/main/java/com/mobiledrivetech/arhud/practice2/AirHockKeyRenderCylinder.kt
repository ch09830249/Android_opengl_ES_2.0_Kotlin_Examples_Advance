package com.mobiledrivetech.arhud.practice4

import android.opengl.GLES20
import javax.microedition.khronos.opengles.GL10
import android.content.Context
import android.opengl.GLSurfaceView.Renderer
import javax.microedition.khronos.egl.EGLConfig
import com.mobiledrivetech.arhud.practice4.Components.Cylinder


class AirHockKeyRenderCylinder
    (private val mContext: Context) : Renderer {
    private var cylinder: Cylinder? = null
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        cylinder = Cylinder.instance
        cylinder!!.init(mContext)
    }
    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        cylinder!!.projectionMatrix(width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        cylinder!!.draw()
    }
}

