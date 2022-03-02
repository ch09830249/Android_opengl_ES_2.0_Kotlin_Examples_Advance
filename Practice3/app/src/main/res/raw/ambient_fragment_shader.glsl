precision mediump float;
uniform vec4 u_Color;
uniform vec4 lightColor;
void main()
{
    //⾄少有%10的光
    float ambientStrength = 0.1;
    //環境光顏色
    vec4 ambient = ambientStrength * lightColor;
    //最終顏色 = 環境光顏色 * 物體顏色
    vec4 result = ambient * u_Color;
    gl_FragColor = result;
}