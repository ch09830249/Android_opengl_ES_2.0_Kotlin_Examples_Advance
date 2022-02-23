package com.mobiledrivetech.arhud.cylinder.Helper

class MatrixHelper {
    companion object {
        /**
         * @param m      生成的新矩陣
         * @param degree 視野角度
         * @param aspect 寬高比
         * @param n      到近處平面的距離
         * @param f      到遠處平面的距離
         */
        fun perspetiveM(m: FloatArray, degree: Float, aspect: Float, n: Float, f: Float) {
            //計算焦距
            val angle = (degree * Math.PI / 180.0).toFloat()
            val a = (1.0f / Math.tan(angle / 2.0)).toFloat()
            m[0] = a / aspect
            m[1] = 0f
            m[2] = 0f
            m[3] = 0f
            m[4] = 0f
            m[5] = a
            m[6] = 0f
            m[7] = 0f
            m[8] = 0f
            m[9] = 0f
            m[10] = -((f + n) / (f - n))
            m[11] = -1f
            m[12] = 0f
            m[13] = 0f
            m[14] = -(2f * f * n / (f - n))
            m[15] = 0f
        }
    }
}