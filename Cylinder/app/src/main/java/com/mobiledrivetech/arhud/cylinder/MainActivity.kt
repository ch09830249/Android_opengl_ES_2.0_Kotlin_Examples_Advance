package com.mobiledrivetech.arhud.cylinder

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Build
import android.app.ActivityManager
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.content.pm.ConfigurationInfo
import android.util.Log
import android.view.View
import com.example.airhockkey.AirHockKeyRenderCylinder
import com.mobiledrivetech.arhud.cylinder.Components.Cylinder


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private var glSurfaceView: GLSurfaceView? = null
    private var mPreviousX = 0f     //上次螢幕上觸控位置的X座標
    private var mPreviousY = 0f     //上次螢幕上觸控位置的Y座標
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()
    }

    private fun initData() {
        if (supportsEs2()) {
            val myGlRender = AirHockKeyRenderCylinder(this)
            glSurfaceView!!.setEGLContextClientVersion(2)
            glSurfaceView!!.setRenderer(myGlRender)
            // RenderMode 有兩種，RENDERMODE_WHEN_DIRTY 和 RENDERMODE_CONTINUOUSLY，前者是懶惰渲染，需要手動調用
            // glSurfaceView.requestRender() 才會進行更新，而後者則是不停渲染。
            glSurfaceView!!.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        } else {
            Log.d(TAG, "This device does not support OpenGL ES 2.0.")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        glSurfaceView = findViewById(R.id.glsurface)
        glSurfaceView!!.setOnTouchListener { v, event ->
            val x = event.x     //獲得當前觸點X座標
            val y = event.y     //獲得當前觸點Y座標
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {}
                MotionEvent.ACTION_MOVE -> {
                    val dx = x - mPreviousX     // 活動距離X座標
                    val dy = y - mPreviousY     // 活動距離Y座標
                    if (dx > 0) {
                        Cylinder.instance.rotate(-dx, 1f, 0f, 0f)
                        // Cylinder.instance.translate(0.1f, 0, 0)
                    } else {
                        Cylinder.instance.rotate(dx, 1f, 0f, 0f)
                        // Cylinder.instance.translate(-0.1f, 0, 0)
                    }
                    if (dy > 0) {
                        Cylinder.instance.rotate(-dy, 0f, 0f, 1f)
                    } else {
                        Cylinder.instance.rotate(dy, 0f, 0f, 1f)
                    }
                }
            }
            mPreviousX = x  //
            mPreviousY = y
            true
        }

//        glSurfaceView!!.setOnTouchListener(object : View.OnTouchListener {
//            override fun onTouch(v: View?, event: MotionEvent): Boolean {
//                val x = event.x     //獲得當前觸點X座標
//                val y = event.y     //獲得當前觸點Y座標
//                when (event.action) {
//                    MotionEvent.ACTION_DOWN -> {}
//                    MotionEvent.ACTION_MOVE -> {
//                        val dx = x - mPreviousX
//                        val dy = y - mPreviousY
//                        if (dx > 0) {
//                            Cylinder.instance.rotate(-dx, 1f, 0f, 0f)
//                            // Cylinder.instance.translate(0.1f, 0, 0)
//                        } else {
//                            Cylinder.instance.rotate(dx, 1f, 0f, 0f)
//                            // Cylinder.instance.translate(-0.1f, 0, 0)
//                        }
//                        if (dy > 0) {
//                            Cylinder.instance.rotate(-dy, 0f, 0f, 1f)
//                        } else {
//                            Cylinder.instance.rotate(dy, 0f, 0f, 1f)
//                        }
//                    }
//                }
//                mPreviousX = x  //
//                mPreviousY = y
//                return true
//            }
//        })
    }

    private fun supportsEs2(): Boolean {
        // Check if the system supports OpenGL ES 2.0.
        val activityManager =
            getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo: ConfigurationInfo = activityManager
            .deviceConfigurationInfo
        // Even though the latest emulator supports OpenGL ES 2.0,
        // it has a bug where it doesn't set the reqGlEsVersion so
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

