precision mediump float;
/* "Uniform variable" does not have to be set per vertex such as "Attribute variable".
    It will use the same value for every vertex unless we change the value of the uniform variable */
uniform vec4 u_Color;
void main() {
    gl_FragColor = u_Color;
}