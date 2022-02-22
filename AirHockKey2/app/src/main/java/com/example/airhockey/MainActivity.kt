package com.example.airhockey

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ConfigurationInfo
import android.opengl.GLSurfaceView
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast




class MainActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private var rendererSet: Boolean  = false     // Remember if our GLSurfaceView is in a valid state or not

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        glSurfaceView = GLSurfaceView(this)

        // Check version
        val activityManager: ActivityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo: ConfigurationInfo = activityManager.deviceConfigurationInfo
        val supportsEs2: Boolean = configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")));

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context
            glSurfaceView.setEGLContextClientVersion(2)
            // Assign our renderer
            glSurfaceView.setRenderer(AirHockeyRenderer(this))
            rendererSet = true
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Add our GLSurfaceView to the activity and display it on the screen
        setContentView(glSurfaceView);

    }

    override fun onPause() {
        super.onPause()
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    override fun onResume() {
        super.onResume()
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }
}