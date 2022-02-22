package com.example.airhockkey

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Build
import android.app.ActivityManager
import android.content.Context
import android.util.Log
import android.opengl.GLSurfaceView


class MainActivity : AppCompatActivity() {

    private val TAG = "AirHockKey MainActivity"
    private var glSurfaceView: GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()
    }

    private fun initData() {
        if (supportsEs2()) {
            val myGlRender = AirHockKeyRender(this)
            // Set opengl version (Request an OpenGL ES 2.0 compatible context)
            glSurfaceView!!.setEGLContextClientVersion(2)

            // Assign our renderer
            glSurfaceView!!.setRenderer(myGlRender)

            // 2 RenderModes: (RENDERMODE_WHEN_DIRTY) (RENDERMODE_CONTINUOUSLY)
            // RENDERMODE_WHEN_DIRTY: Render when the func glSurfaceView.requestRender() calls
            // RENDERMODE_CONTINUOUSLY: Render constantly based on the user-given FPS
            glSurfaceView!!.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        } else {
            Log.d(TAG, "This device does not support OpenGL ES 2.0.")
        }
    }

    // find GLSurfaceView ID
    private fun initView() {
        glSurfaceView = findViewById(R.id.glsurface)
    }

    private fun supportsEs2(): Boolean {
        // Check if the system supports OpenGL ES 2.0
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo

        // Even though the latest emulator supports OpenGL ES 2.0,
        // there is a bug where it doesn't set the reqGlEsVersion so
        // the above check doesn't work. The below will detect if the
        // app is running on an emulator, and assume that it supports
        // OpenGL ES 2.0.
        return (configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"))))
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView!!.onPause()
    }
}
