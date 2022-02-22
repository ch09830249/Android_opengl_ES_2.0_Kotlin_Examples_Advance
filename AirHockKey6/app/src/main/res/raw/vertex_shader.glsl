/* When we define each vertex, the vertex shader will be called once.
 It will receive the current vertex position in the gl_Position property */

attribute vec4 a_Position;
attribute vec4 a_Color;

/* We do not directly use the value of the vertex array as the vertex,
   but the product of the vertex array and the matrix as the vertex,
   which means that the vertex array is not used as normalized device coordinates
   , it only represents virtual coordinates, while the array and matrix The product
    of will become normalized device coordinates, this matrix will convert virtual
    coordinates to normalized device coordinates */
uniform mat4 u_Matrix; // new (4*4 matrix)

varying vec4 v_Color;

void main() {
    v_Color = a_Color;
    gl_Position =   u_Matrix * a_Position;  // OpenGL will take the value stored in gl_Position as the final position of the vertex, and then assemble these vertices into points, lines, triangles
    gl_PointSize = 10.0;
}
