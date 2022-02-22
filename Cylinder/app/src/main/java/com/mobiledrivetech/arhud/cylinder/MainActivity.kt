package com.mobiledrivetech.arhud.cylinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Build
import android.app.ActivityManager
import android.opengl.GLSurfaceView
import com.example.airhockkey.AirHockKeyRenderCircle
import android.view.MotionEvent
import android.content.pm.ConfigurationInfo
import android.util.Log
import android.view.View
import com.mobiledrivetech.arhud.cylinder.Components.Cylinder


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private var glSurfaceView: GLSurfaceView? = null
    private var mPreviousX = 0f
    private var mPreviousY = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()
    }

    private fun initData() {
        if (supportsEs2()) {
            val myGlRender = AirHockKeyRenderCircle(this)
            //设置opengl版本
            glSurfaceView!!.setEGLContextClientVersion(2)
            glSurfaceView!!.setRenderer(myGlRender)
            //RenderMode 有两种，RENDERMODE_WHEN_DIRTY 和 RENDERMODE_CONTINUOUSLY，前者是懒惰渲染，需要手动调用
            // glSurfaceView.requestRender() 才会进行更新，而后者则是不停渲染。
            glSurfaceView!!.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        } else {
            Log.d(TAG, "不支持2.0版本")
        }
    }

    private fun initView() {
        glSurfaceView = findViewById(R.id.glsurface)
        glSurfaceView!!.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                val x = event.x //当前的触控位置X坐标
                val y = event.y //当前的触控位置X坐标
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = x - mPreviousX
                        val dy = y - mPreviousY
                        if (dx > 0) {
                            Cylinder.instance.rotate(-dx, 1f, 0f, 0f)
                            //                            Cylinder.getInstance().translate(0.1f, 0, 0);
                        } else {
                            Cylinder.instance.rotate(dx, 1f, 0f, 0f)
                            //                            Cylinder.getInstance().translate(-0.1f, 0, 0);
                        }
                        if (dy > 0) {
                            Cylinder.instance.rotate(-dy, 0f, 0f, 1f)
                        } else {
                            Cylinder.instance.rotate(dy, 0f, 0f, 1f)
                        }
                    }
                }
                mPreviousX = x
                mPreviousY = y
                return true
            }
        })
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

