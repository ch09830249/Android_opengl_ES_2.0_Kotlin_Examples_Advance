/* When we define each vertex, the vertex shader will be called once.
 It will receive the current vertex position in the gl_Position property */

attribute vec4 a_Position;
attribute vec4 a_Color;     // new
varying vec4 v_Color;       // new

void main() {
    v_Color = a_Color;
    gl_Position =  a_Position;  // OpenGL will take the value stored in gl_Position as the final position of the vertex, and then assemble these vertices into points, lines, triangles
    gl_PointSize = 10.0;
}
