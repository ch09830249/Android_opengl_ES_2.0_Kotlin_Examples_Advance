/* "Uniform variable" does not have to be set per vertex such as "Attribute variable".
    It will use the same value for every vertex unless we change the value of the uniform variable */

/* When opengl builds a line and triangle, it builds the corresponding graph based on the vertices,
   then decomposes the image into fragments, and each fragment is executed once by the fragment shader */

/* "Varying variable" is a special variable type that mixes the values given to it and sends these values
    to the fragment shader*/
precision mediump float;
// uniform vec4 u_Color;
varying vec4 v_Color;   // new
void main() {
    gl_FragColor = v_Color;  // Modify
}