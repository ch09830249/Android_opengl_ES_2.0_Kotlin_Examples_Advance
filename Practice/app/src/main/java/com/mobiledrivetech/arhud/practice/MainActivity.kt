package com.mobiledrivetech.arhud.practice


import android.os.Bundle
import android.app.ActivityManager
import android.opengl.GLSurfaceView
import android.app.Activity
import android.content.Context
import android.content.pm.ConfigurationInfo


class MainActivity : Activity() {
    /** Hold a reference to our GLSurfaceView  */
    private var mGLSurfaceView: GLSurfaceView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGLSurfaceView = GLSurfaceView(this)

        // Check if the system supports OpenGL ES 2.0.
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo: ConfigurationInfo = activityManager.deviceConfigurationInfo
        val supportsEs2: Boolean = configurationInfo.reqGlEsVersion >= 0x20000

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            mGLSurfaceView!!.setEGLContextClientVersion(2)

            // Set the renderer to our demo renderer, defined below.
            mGLSurfaceView!!.setRenderer(TrianglesRenderer())
        } else {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return
        }

        setContentView(mGLSurfaceView)
    }

    override fun onResume() {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume()
        mGLSurfaceView!!.onResume()
    }

    override fun onPause() {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause()
        mGLSurfaceView!!.onPause()
    }
}