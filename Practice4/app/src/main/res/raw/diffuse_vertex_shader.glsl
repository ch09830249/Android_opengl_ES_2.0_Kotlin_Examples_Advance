attribute vec4 a_Position;
uniform mat4 u_Matrix;
uniform vec4 light;             // 光原始位置

varying vec4 lightPo;          // 轉換後光源位置
varying vec4 outNormal;
varying vec4 viewPo;

void main() {
    vec4 final_object_position = u_Matrix * a_Position;
    outNormal = final_object_position;
    lightPo = u_Matrix * light;
    gl_Position =  final_object_position;
}