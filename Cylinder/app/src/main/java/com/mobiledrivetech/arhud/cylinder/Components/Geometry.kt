package com.mobiledrivetech.arhud.cylinder.Components

class Geometry {
    companion object {
        // 點 (座標)
        class Point(val x: Float, val y: Float, val z: Float) {
            // 沿 y 移動
            fun translateY(distance: Float): Point {
                return Point(x, y + distance, z)
            }
        }

        // 圓 (圓心, 半徑)
        class Circle(val center: Point, val radius: Float) {
            // 放大或縮小
            fun scale(scale: Float): Circle {
                return Circle(center, radius * scale)
            }
        }

        // 圓柱
        class Cylinder(val center: Point, val radius: Float, val height: Float)
    }
}
