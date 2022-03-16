attribute vec4 a_Position;          // 物體頂點原始位置
uniform mat4 u_Matrix;              // 轉換成eye space矩陣
uniform vec3 normal_vector_eye;
uniform vec3 light_position_eye;    // 光源位置 (eye)
uniform vec4 a_Color;

varying vec4 v_Color;
void main() {
    vec3 object_position_eye = vec3(u_Matrix * a_Position);                     // ok      物體頂點 (eye)
//    vec3 normal_vector_eye = normalize(object_position_eye);                     ok      法向量 (eye)
    float distance = length(light_position_eye - object_position_eye);          // ok      光源和點的距離
    vec3 lightvector = normalize(light_position_eye - object_position_eye);     // ok      光和點連線向量
    float diffuse = max(dot(lightvector, normal_vector_eye), 0.1);
    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance))) * 2.0;
    v_Color = a_Color * diffuse;
    gl_Position =  u_Matrix * a_Position;
}