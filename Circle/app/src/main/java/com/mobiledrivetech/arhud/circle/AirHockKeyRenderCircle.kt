package com.example.airhockkey

import android.opengl.GLES20
import javax.microedition.khronos.opengles.GL10
import android.content.Context
import android.opengl.GLSurfaceView.Renderer
import javax.microedition.khronos.egl.EGLConfig
import com.example.airhockkey.Components.Circle


class AirHockKeyRenderCircle     //画圆
    (private val mContext: Context) : Renderer {
    private var circle: Circle? = null
    // private val cylinder: Cylinder? = null

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {

        // Clear screen
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        circle = Circle()
        circle!!.init(mContext)

//        cylinder = Cylinder.getInstance();
//        cylinder.init(mContext);
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        //        cylinder.projectionMatrix(width, height);
        circle!!.projectionMatrix(width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

//        cylinder.draw();
        circle!!.draw()
    }
}
